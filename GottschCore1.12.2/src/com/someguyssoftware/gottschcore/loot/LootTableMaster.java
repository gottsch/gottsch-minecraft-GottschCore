/**
 * 
 */
package com.someguyssoftware.gottschcore.loot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @author Mark Gottschling on Dec 1, 2018
 *
 */
public class LootTableMaster {
	private IMod mod;
	private String lootTablesFolderName; // CUSTOM_LOOT_TABLES_PATH

	private LootContext context;

	/*
	 * customized loot table manager
	 */
	private LootTableManager lootTableManager;

	/*
	 * relative location of loot tables to lootTablesFolderName - in resource path
	 * or file system
	 */
//	private List<String> lootTableFolderLocations; // CHEST_LOOT_TABLE_FOLDER_LOCATIONS // ?? is this needed or passed in?

	private Map<String, List<ResourceLocation>> lootTablesResourceLocationMap = new HashMap<>();
	private Map<String, List<LootTable>> lootTablesMap = new HashMap<>();

	/**
	 * Create in your @Mod class in preInt() or int().
	 */
	public LootTableMaster(IMod mod, String resourcePath, String folderName) {
		this.mod = mod;
		this.lootTablesFolderName = folderName;
	}

	/**
	 * Creates all the necessary folder and resources before actual loading of loot tables.
	 * Call in your @Mod class in preInt() or int().
	 * 
	 * @param resourceRootPath
	 * @param modID
	 */
	public void buildAndExpose(String resourceRootPath, String modID, List<String> locations) {
		GottschCore.logger.debug("loot table folder locations -> {}", locations);
		// create paths to custom loot tables if they don't exist
		for (String location : locations) {
			GottschCore.logger.debug("buildAndExpose location -> {}", location);
			createLootTableFolder(modID, location);
			exposeLootTable(resourceRootPath, modID, location);
		}
	}

	/**
	 * Call in WorldEvent.Load event handler.
	 * @param world
	 */
	public void init(WorldServer world) {
		// create a new loot table manager for custom file-system loot tables
		this.lootTableManager = new LootTableManager(Paths.get(getMod().getConfig().getModsFolder()).toAbsolutePath().toFile());

		// create a context
		this.context = new LootContext.Builder(world, lootTableManager).build();
	}
	
	/**
	 * Call in WorldEvent.Load event handler.
	 * Overide this method if you have a different cache mechanism.
	 * @param modIDIn
	 * @param location
	 */
	public void register(String modID, List<String> locations) {
		for (String location : /*this.lootTableFolderLocations*/locations) {
			// get loot table files as ResourceLocations from the file system location
			List<ResourceLocation> locs = getLootTablesResourceLocations(modID, location);
			// build a key
			String key = getMod().getId() + ":" + getLootTablesFolderName() + "/" +  modID + "/" + location;
			
			// add to map
			if (!lootTablesResourceLocationMap.containsKey(key)) {
				lootTablesResourceLocationMap.put(key, locs);
			}
			
			// load each ResourceLocation as LootTable and map it.
			for (ResourceLocation loc : locs) {
				LootTable lootTable = lootTableManager.getLootTableFromLocation(loc);
				if (!lootTablesMap.containsKey(key)) {
					lootTablesMap.put(key, new ArrayList<>());
				}
				GottschCore.logger.debug("mapping [key][loot table] -> [{}] [{}]", key, loc);
				lootTablesMap.get(key).add(lootTable);
			}
			
		}
	}

	/**
	 * 
	 * @param folder
	 */
	protected void createLootTableFolder(String modID, String location) {
		// ensure that the requried properties (modID) is not null
		if (modID == null || modID.isEmpty())
			modID = getMod().getId();
		// build a path to the specified location
		// ie ../mods/[MODID]/[LOOT_TABLES]/[location]
		Path modsPath = Paths.get(getMod().getConfig().getModsFolder());
		Path folder = Paths.get(modsPath.toString(), getMod().getId(), getLootTablesFolderName(), modID, ((location != null && !location.equals("")) ? (location + "/") : "")).toAbsolutePath();

		if (Files.notExists(folder)) {
			GottschCore.logger.debug("loot tables folder \"{}\" will be created.", folder.toString());
			try {
				Files.createDirectories(folder);

			} catch (IOException e) {
				GottschCore.logger.warn("Unable to create loot tables folder \"{}\"", folder.toString());
			}
		}
	}

	/**
	 * 
	 * @param modID
	 * @param location
	 */
	protected void exposeLootTable(String resourceRootPath, String modID, String location) {
		// ensure that the requried properties are not null
		if (modID == null || modID.isEmpty())
			modID = getMod().getId();
		location = (location != null && !location.equals("")) ? (location + "/") : "";

		Path folder = null;
		Stream<Path> walk = null;

		FileSystem fs = getResourceAsFileSystem(resourceRootPath, modID, location);
		if (fs == null)
			return;

		try {
			// get the base path of the resource
			Path resourceBasePath = fs.getPath(resourceRootPath, modID, location);
			// GottschCore.logger.debug("resource base path -> {}",
			// resourceBasePath.toString());
			folder = Paths.get(getMod().getConfig().getModsFolder(), getMod().getId(), getLootTablesFolderName(), modID, location).toAbsolutePath();

			boolean isFirst = true;
			// proces all the files in the folder
			walk = Files.walk(resourceBasePath, 1);
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path resourceFilePath = it.next();
				// String tableName = resourceFilePath.getFileName().toString();
				// GottschCore.logger.debug("foreign mod loot_table -> {}",
				// resourceFilePath.toString());
				// check the first file, which is actually the given directory itself
				if (isFirst) {
					// create the file system folder if it doesn't exist
					if (Files.notExists(folder)) {
						createLootTableFolder(modID, location);
					}
				} else {
					// test if file exists on the file system
					Path fileSystemFilePath = Paths.get(folder.toString(), resourceFilePath.getFileName().toString()).toAbsolutePath();
					GottschCore.logger.debug("folderLootTablePath -> {}", fileSystemFilePath.toString());

					if (Files.notExists(fileSystemFilePath)) {
						// copy from resource/classpath to file path
						InputStream is = LootTableMaster.class.getResourceAsStream(resourceFilePath.toString());
						try (FileOutputStream fos = new FileOutputStream(fileSystemFilePath.toFile())) {
							byte[] buf = new byte[2048];
							int r;
							while ((r = is.read(buf)) != -1) {
								fos.write(buf, 0, r);
							}
						} catch (IOException e) {
							GottschCore.logger.error("Error exposing chestsheet resource to file system.", e);
						}
					}
				}
				isFirst = false;
			}
		} catch (Exception e) {
			GottschCore.logger.error("error:", e);
		} finally {
			// close the stream
			if (walk != null) {
				walk.close();
			}
		}

		// close the file system
		if (fs != null && fs.isOpen()) {
			try {
				fs.close();
			} catch (IOException e) {
				GottschCore.logger.debug("An error occurred attempting to close the FileSystem:", e);
			}
		}
	}

	/**
	 * 
	 * @param modID
	 * @param location
	 * @return
	 */
	protected FileSystem getResourceAsFileSystem(String resourceRootPath, String modID, String location) {
		FileSystem fs = null;
		Map<String, String> env = new HashMap<>();
		URI uri = null;

		// get the asset resource folder that is unique to this mod
		resourceRootPath = "/" + resourceRootPath.replaceAll("^/|/$", "") + "/";
		URL url = GottschCore.class.getResource(resourceRootPath + modID + "/" + location);
		if (url == null) {
			GottschCore.logger.error("Unable to locate resource {}", resourceRootPath + modID + "/" + location);
			return null;
		}

		// convert to a uri
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
			GottschCore.logger.error("An error occurred during loot table processing:", e);
			return null;
		}

		// split the uri into 2 parts - jar path and folder path within jar
		String[] array = uri.toString().split("!");
		try {
			fs = FileSystems.newFileSystem(URI.create(array[0]), env);
		} catch (IOException e) {
			GottschCore.logger.error("An error occurred during loot table processing:", e);
			return null;
		}

		return fs;
	}

	/**
	 * Register the custom loot tables
	 */
	public List<ResourceLocation> getLootTablesResourceLocations(String modIDIn, String locationIn) {
		// ensure that the requried properties (modID) is not null
		final String modID = (modIDIn == null || modIDIn.isEmpty()) ? getMod().getId() : modIDIn;
		final String location= (locationIn != null && !locationIn.equals("")) ? (locationIn + "/") : "";
//		final String location = locationIn;

		List<ResourceLocation> locs = new ArrayList<>();
		Path path = Paths.get(getMod().getConfig().getModsFolder(), getMod().getId(), getLootTablesFolderName(), modID, location).toAbsolutePath();

		 GottschCore.logger.debug("Path to custom loot table -> {}", path.toString());
		// check if path/folder exists
		if (Files.notExists(path)) {
			GottschCore.logger.debug("Unable to locate -> {}", path.toString());
			return locs;
		}

		try {
			Files.walk(path).filter(Files::isRegularFile).forEach(f -> {
				 GottschCore.logger.debug("Custom loot table -> {}", f.toAbsolutePath().toString());
				ResourceLocation loc = 
						new ResourceLocation(
								getMod().getId() + ":" + getLootTablesFolderName() 
								+ "/" + modID + "/" + location
										+ f.getFileName().toString().replace(".json", ""));
				GottschCore.logger.debug("Resource location -> {}", loc);
				locs.add(loc);
			});
		} catch (IOException e) {
			GottschCore.logger.error("Error processing custom loot table:", e);
		}

		return locs;
	}

//	/**
//	 * @return the lootTablesResourcePath
//	 */
//	public String getLootTablesResourcePath() {
//		return lootTablesResourcePath;
//	}
//
//	/**
//	 * @param lootTablesResourcePath
//	 *            the lootTablesResourcePath to set
//	 */
//	public void setLootTablesResourcePath(String lootTablesResourcePath) {
//		this.lootTablesResourcePath = lootTablesResourcePath;
//	}

	/**
	 * @return the lootTablesFolderName
	 */
	public String getLootTablesFolderName() {
		return lootTablesFolderName;
	}

	/**
	 * @param lootTablesFolderName
	 *            the lootTablesFolderName to set
	 */
	public void setLootTablesFolderName(String lootTablesFolderName) {
		this.lootTablesFolderName = lootTablesFolderName;
	}

	/**
	 * @return the mod
	 */
	protected IMod getMod() {
		return mod;
	}

	/**
	 * @param mod
	 *            the mod to set
	 */
	protected void setMod(IMod mod) {
		this.mod = mod;
	}

	/**
	 * @return the context
	 */
	public LootContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	protected void setContext(LootContext context) {
		this.context = context;
	}

	/**
	 * @return the lootTableManager
	 */
	protected LootTableManager getLootTableManager() {
		return lootTableManager;
	}

	/**
	 * @param lootTableManager
	 *            the lootTableManager to set
	 */
	protected void setLootTableManager(LootTableManager lootTableManager) {
		this.lootTableManager = lootTableManager;
	}

	/**
	 * @return the lootTableFolderLocations
	 */
//	public List<String> getLootTableFolderLocations() {
//		if (lootTableFolderLocations == null)
//			lootTableFolderLocations = new ArrayList<>();
//		return lootTableFolderLocations;
//	}

	/**
	 * @param lootTableFolderLocations
	 *            the lootTableFolderLocations to set
	 */
//	public void setLootTableFolderLocations(List<String> lootTableFolderLocations) {
//		this.lootTableFolderLocations = lootTableFolderLocations;
//	}

	/**
	 * @return the lootTablesResourceLocationMap
	 */
	protected Map<String, List<ResourceLocation>> getLootTablesResourceLocationMap() {
		if (lootTablesResourceLocationMap == null)
			lootTablesResourceLocationMap = new HashMap<>();
		return lootTablesResourceLocationMap;
	}

	/**
	 * @param lootTablesResourceLocationMap
	 *            the lootTablesResourceLocationMap to set
	 */
	protected void setLootTablesResourceLocationMap(Map<String, List<ResourceLocation>> lootTablesResourceLocationMap) {
		this.lootTablesResourceLocationMap = lootTablesResourceLocationMap;
	}

	/**
	 * @return the lootTablesMap
	 */
	public Map<String, List<LootTable>> getLootTablesMap() {
		return lootTablesMap;
	}

	/**
	 * @param lootTablesMap
	 *            the lootTablesMap to set
	 */
	public void setLootTablesMap(Map<String, List<LootTable>> lootTablesMap) {
		this.lootTablesMap = lootTablesMap;
	}

}
