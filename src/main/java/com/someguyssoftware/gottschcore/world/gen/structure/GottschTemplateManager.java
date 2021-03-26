/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.resource.AbstractResourceManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.Template;

/**
 * @author Mark Gottschling on Feb 3, 2019
 *
 */
public class GottschTemplateManager extends AbstractResourceManager {
	   private final DataFixer fixer;
	
	/*
	 * templates is the master map where the key is the String representation resource location.
	 */
	private final Map<String, Template> templates = Maps.<String, Template>newHashMap();
	
	/*
	 * standard list of marker blocks to scan for 
	 */
	private List<Block> markerScanList;

	/*
	 * 
	 */
	private Map<StructureMarkers, Block> markerMap;
	
	/*
	 * standard list of replacements blocks.
	 * NOTE needs to be <IBlockState, IBlockState> (for v1.12.x anyway)
	 */
	private Map<BlockState, BlockState> replacementMap;
	
	/**
	 * 
	 * @param mod
	 * @param resourceFolder
	 * @param fixer
	 */
	public GottschTemplateManager(IMod mod, String resourceFolder, DataFixer fixer) {
		super(mod, resourceFolder);
		this.fixer = fixer;
		
        // setup standard list of markers
        markerMap = Maps.newHashMapWithExpectedSize(10);
        markerMap.put(StructureMarkers.CHEST, Blocks.CHEST);
        markerMap.put(StructureMarkers.BOSS_CHEST, Blocks.ENDER_CHEST);
        markerMap.put(StructureMarkers.SPAWNER, Blocks.SPAWNER);
        markerMap.put(StructureMarkers.ENTRANCE, Blocks.GOLD_BLOCK);
        markerMap.put(StructureMarkers.OFFSET, Blocks.REDSTONE_BLOCK);
        markerMap.put(StructureMarkers.PROXIMITY_SPAWNER, Blocks.IRON_BLOCK);
        markerMap.put(StructureMarkers.NULL, Blocks.BEDROCK);

        // default marker scan list
        markerScanList = Arrays.asList(new Block[] {
    			markerMap.get(StructureMarkers.CHEST),
    			markerMap.get(StructureMarkers.BOSS_CHEST),
    			markerMap.get(StructureMarkers.SPAWNER),
    			markerMap.get(StructureMarkers.ENTRANCE),
    			markerMap.get(StructureMarkers.OFFSET),
    			markerMap.get(StructureMarkers.PROXIMITY_SPAWNER)
    			});
        
        // TODO rename this to preWriteReplacementMap();
        // default replacement scan map
        replacementMap = Maps.newHashMap();
        replacementMap.put(Blocks.WHITE_WOOL.defaultBlockState(), Blocks.AIR.defaultBlockState());
        replacementMap.put(Blocks.BLACK_STAINED_GLASS.defaultBlockState(), Blocks.SPAWNER.defaultBlockState());    
	}

	/**
	 * generic load - load all templates in template map
	 */
	public GottschTemplateManager loadAll(List<String> locations) {
		GottschCore.LOGGER.debug("loading all structures...");
		for (String location : locations) {
			GottschCore.LOGGER.debug("loading from -> {}", location);
			load(new ResourceLocation(location), markerScanList, replacementMap);
		}		
		return this;
	}
	
	/**
	 * Load template file from classpath or file system
	 * @param server
	 * @param templatePath
	 * @return
	 */
	public Template load(ResourceLocation templatePath, List<Block> markerBlocks, Map<BlockState, BlockState> replacementBlocks) {
		String key = templatePath.toString();
		
		if (this.getTemplates().containsKey(key)) {
			GottschCore.LOGGER.debug("read template from master map using key -> {}", key);
			return this.templates.get(key);
		}

		this.readTemplate(templatePath, markerBlocks, replacementBlocks);
		if (this.templates.get(key) != null) {
			GottschCore.LOGGER.debug("Loaded template from -> {}", key);
		}
		else {
			GottschCore.LOGGER.debug("Unable to read template from -> {}", key);
		}
		return this.templates.containsKey(key) ? (Template) this.templates.get(key) : null;
	}
	
	/**
	 * This reads a structure template from the given location and stores it. This
	 * first attempts get the template from an external folder. If it isn't there
	 * then it attempts to take it from the minecraft jar.
	 */
	public boolean readTemplate(ResourceLocation location, List<Block> markerBlocks, Map<BlockState, BlockState> replacementBlocks) {
		String s = location.getPath();
		GottschCore.LOGGER.debug("template resource path -> {}", s);
		String suffix = "";
		if (!s.endsWith(".nbt")) {
			suffix = ".nbt";
		}
		Path path = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), s + suffix);
		File file1 = path.toFile();
		GottschCore.LOGGER.debug("template file path -> {}", file1.getAbsoluteFile());
		if (!file1.exists()) {
			GottschCore.LOGGER.debug("file does not exist, read from jar -> {}", file1.getAbsolutePath());
			return this.readTemplateFromJar(location, markerBlocks, replacementBlocks);
		} else {
			GottschCore.LOGGER.debug("reading template from file system using file path -> {}", file1.getAbsolutePath());
			InputStream inputstream = null;
			boolean flag;

			try {
				inputstream = new FileInputStream(file1);
				this.readTemplateFromStream(location.toString(), inputstream, markerBlocks, replacementBlocks);
				return true;
			} catch (Throwable var10) {
				flag = false;
			} finally {
				IOUtils.closeQuietly(inputstream);
			}

			return flag;
		}
	}
	
	/**
	 * reads a template from the minecraft jar
	 */
	private boolean readTemplateFromJar(ResourceLocation id, List<Block> markerBlocks, Map<BlockState, BlockState> replacementBlocks) {
		String s = id.getNamespace();
		String s1 = id.getPath();
		InputStream inputstream = null;
		boolean flag;

		try {
			GottschCore.LOGGER.debug("attempting to open resource stream -> {}", "/assets/" + s + "/strucutres/" + s1 + ".nbt");
			inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/structures/" + s1 + ".nbt");
			this.readTemplateFromStream(id.toString(), inputstream, markerBlocks, replacementBlocks);
			return true;
			// TODO change from Throwable
		} catch (Throwable var10) {
			GottschCore.LOGGER.error("error reading resource: ", var10);
			flag = false;
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		return flag;
	}
	
	/**
	 * reads a template from an inputstream
	 */
	private void readTemplateFromStream(String id, InputStream stream, List<Block> markerBlocks, 
			Map<BlockState, BlockState> replacementBlocks) throws IOException {
		
		CompoundNBT nbt = CompressedStreamTools.readCompressed(stream);

		if (!nbt.contains("DataVersion", 99)) {
			nbt.putInt("DataVersion", 500);
		}

		GottschTemplate template = new GottschTemplate();
		template.load(NBTUtil.update(this.fixer, DefaultTypeReferences.STRUCTURE, nbt, nbt.getInt("DataVersion")), markerBlocks, replacementBlocks);
		GottschCore.LOGGER.debug("adding template to map with key -> {}", id);
		this.templates.put(id, template);
	}
	
	/**
	 * writes the template to an external folder
	 */
//	public boolean writeTemplate(@Nullable MinecraftServer server, ResourceLocation id) {
//		String s = id.getResourcePath();
//
//		if (server != null && this.templates.containsKey(s)) {
//			File file1 = new File(this.baseFolder);
//
//			if (!file1.exists()) {
//				if (!file1.mkdirs()) {
//					return false;
//				}
//			} else if (!file1.isDirectory()) {
//				return false;
//			}
//
//			File file2 = new File(file1, s + ".nbt");
//			Template template = this.templates.get(s);
//			OutputStream outputstream = null;
//			boolean flag;
//
//			try {
//				NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());
//				outputstream = new FileOutputStream(file2);
//				CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
//				return true;
//			} catch (Throwable var13) {
//				flag = false;
//			} finally {
//				IOUtils.closeQuietly(outputstream);
//			}
//
//			return flag;
//		} else {
//			return false;
//		}
//	}
	
	/**
	 * 
	 * @param templatePath
	 */
	public void remove(ResourceLocation templatePath) {
		this.templates.remove(templatePath.getPath());
	}
	
	public Map<String, Template> getTemplates() {
		return templates;
	}

	public List<Block> getMarkerScanList() {
		return markerScanList;
	}

	public void setMarkerScanList(List<Block> scanList) {
		this.markerScanList = scanList;
	}

	public Map<StructureMarkers, Block> getMarkerMap() {
		return markerMap;
	}

	public void setMarkerMap(Map<StructureMarkers, Block> markerMap) {
		this.markerMap = markerMap;
	}

	/**
	 * @return the fixer
	 */
	public DataFixer getFixer() {
		return fixer;
	}

	public Map<BlockState, BlockState> getReplacementMap() {
		return replacementMap;
	}

	public void setReplacementMap(Map<BlockState, BlockState> replacementMap) {
		this.replacementMap = replacementMap;
	}

}
