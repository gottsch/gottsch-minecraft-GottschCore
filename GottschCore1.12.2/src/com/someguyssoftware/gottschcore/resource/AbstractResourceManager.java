/**
 * 
 */
package com.someguyssoftware.gottschcore.resource;

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
import java.util.stream.Stream;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraft.util.ResourceLocation;


/**
 * @author Mark Gottschling on Jul 28, 2019
 *
 */
public class AbstractResourceManager implements IResourceManager {
	private IMod mod;
	
	/*
	 * the base folder name on the file system where resources should be located. 
	 * this location is after the /<configPath>/<modID>/ path 
	 */
	private String baseResourceFolder;
	
	public AbstractResourceManager(IMod mod, String resourceFolder) {
		setMod(mod);
		setBaseResourceFolder(resourceFolder);
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.resource.IResourceManager#buildAndExpose(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void buildAndExpose(String jarResourceRootPath, String modID, List<String> locations) {
		GottschCore.logger.debug("resource folder locations -> {}", locations);
		// create paths to custom resources if they don't exist
		for (String location : locations) {
			GottschCore.logger.debug("buildAndExpose location -> {}", location);
			createFileSystemResourceFolder(modID, location);
			exposeResource(jarResourceRootPath, modID, location);
		}
	}
	
	/**
	 * 
	 * @param location
	 */
	protected void createFileSystemResourceFolder(String modID, String location) {
		// build a path to the specified location
		Path folder = Paths.get(
				getMod().getConfig().getConfigFolder(), 
				getMod().getId(), 
				getBaseResourceFolder(), 
				modID, 
				((location != null && !location.equals("")) ? (location + "/") : "")
			).toAbsolutePath();

		if (Files.notExists(folder)) {
			GottschCore.logger.debug("resources folder \"{}\" will be created.", folder.toString());
			try {
				Files.createDirectories(folder);

			} catch (IOException e) {
				GottschCore.logger.warn("Unable to create resources folder \"{}\"", folder.toString());
			}
		}
	}
	
	protected void exposeResource(String jarResourceRootPath, String modID, String location) {
		// ensure that the requried properties are not null
		if (modID == null || modID.isEmpty())
			modID = getMod().getId();
		location = (location != null && !location.equals("")) ? (location + "/") : "";
		
		Path folder = null;
		Stream<Path> walk = null;
		GottschCore.logger.debug("resource as file system path -> {},{},{}", jarResourceRootPath.toString(), modID, location);

		FileSystem fs = getJarResourceAsFileSystemObject(jarResourceRootPath, modID, location);
		if (fs == null) {
			return;
		}

		try {
			// get the base path of the resource
			Path resourceBasePath = fs.getPath(jarResourceRootPath, modID, location);
			GottschCore.logger.debug("resource base path -> {}", resourceBasePath.toString());
			
			folder = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), getBaseResourceFolder(), modID, location).toAbsolutePath();

			boolean isFirst = true;
			// proces all the files in the folder
			walk = Files.walk(resourceBasePath, 1);
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path resourceFilePath = it.next();
				// check the first file, which is actually the given directory itself
				if (isFirst) {
					// create the file system folder if it doesn't exist
					if (Files.notExists(folder)) {
						createFileSystemResourceFolder(modID, location);
					}
				} else {
					// test if file exists on the file system
					Path fileSystemFilePath = Paths.get(folder.toString(), resourceFilePath.getFileName().toString()).toAbsolutePath();
					GottschCore.logger.debug("file system resource path -> {}", fileSystemFilePath.toString());

					if (Files.notExists(fileSystemFilePath)) {
						// copy from resource/classpath to file path
						InputStream is = getMod().getClass().getResourceAsStream(resourceFilePath.toString());
						try (FileOutputStream fos = new FileOutputStream(fileSystemFilePath.toFile())) {
							byte[] buf = new byte[2048];
							int r;
							while ((r = is.read(buf)) != -1) {
								fos.write(buf, 0, r);
							}
						} catch (IOException e) {
							GottschCore.logger.error("Error exposing resource to file system.", e);
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
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					GottschCore.logger.error("error:", e);
				}
			}
		}	
	}
	
	/**
	 * 
	 * @param modID
	 * @param location
	 * @return
	 */
	protected FileSystem getJarResourceAsFileSystemObject(String resourceRootPath, String modID, String location) {
		FileSystem fs = null;
		Map<String, String> env = new HashMap<>();
		URI uri = null;

		// get the asset resource folder that is unique to this mod
		resourceRootPath = "/" + resourceRootPath.replaceAll("^/|/$", "") + "/";
		URL url = getMod().getClass().getResource(resourceRootPath + modID + "/" + location);
		if (url == null) {
			GottschCore.logger.error("Unable to locate resource {}", resourceRootPath + modID + "/" + location);
			return null;
		}

		// convert to a uri
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
			GottschCore.logger.error("An error occurred during resource exposing processing:", e);
			return null;
		}

		// split the uri into 2 parts - jar path and folder path within jar
		String[] array = uri.toString().split("!");
		try {
			fs = FileSystems.newFileSystem(URI.create(array[0]), env);
		} catch (IOException e) {
			GottschCore.logger.error("An error occurred during resource exposing processing:", e);
			return null;
		}
		return fs;
	}
	
	/**
	 * Get all resource as ResourceLocation objects
	 */
	protected List<ResourceLocation> getResourceLocations(String modIDIn, String locationIn) {
		// ensure that the requried properties (modID) is not null
		final String modID = (modIDIn == null || modIDIn.isEmpty()) ? getMod().getId() : modIDIn;
		final String location= (locationIn != null && !locationIn.equals("")) ? (locationIn + "/") : "";

		List<ResourceLocation> locs = new ArrayList<>();
		Path path = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), getBaseResourceFolder(), modID, location).toAbsolutePath();

		 GottschCore.logger.debug("Path to custom resource -> {}", path.toString());
		// check if path/folder exists
		if (Files.notExists(path)) {
			GottschCore.logger.debug("Unable to locate -> {}", path.toString());
			return locs;
		}

		try {
			Files.walk(path).filter(Files::isRegularFile).forEach(f -> {
				 GottschCore.logger.debug("Custom resource file path-> {}", f.toAbsolutePath().toString());
				ResourceLocation loc = 
						new ResourceLocation(
								getMod().getId() + ":" + getBaseResourceFolder()
								+ "/" + modID + "/" + location
										+ f.getFileName().toString());//.replace(".json", ""));
				GottschCore.logger.debug("Resource location -> {}", loc);
				locs.add(loc);
			});
		} catch (IOException e) {
			GottschCore.logger.error("Error processing custom resource:", e);
		}
		return locs;
	}
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.resource.IResourceManager#getMod()
	 */
	@Override
	public IMod getMod() {
		return mod;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.resource.IResourceManager#setMod(com.someguyssoftware.gottschcore.mod.IMod)
	 */
	@Override
	public void setMod(IMod mod) {
		this.mod = mod;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.resource.IResourceManager#getFileSystemBaseResourceFolder()
	 */
	@Override
	public String getBaseResourceFolder() {
		return baseResourceFolder;
	}

	/* (non-Javadoc)
	 * @see com.someguyssoftware.gottschcore.resource.IResourceManager#setFileSystemBaseResourceFolder(java.lang.String)
	 */
	@Override
	public void setBaseResourceFolder(String resourceFolder) {
		this.baseResourceFolder = resourceFolder;
	}
}
