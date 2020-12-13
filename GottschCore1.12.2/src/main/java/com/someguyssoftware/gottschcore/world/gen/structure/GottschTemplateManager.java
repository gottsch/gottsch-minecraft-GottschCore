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
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.resource.AbstractResourceManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.gen.structure.template.Template;

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
	private Map<IBlockState, IBlockState> replacementMap;
	
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
        markerMap.put(StructureMarkers.SPAWNER, Blocks.MOB_SPAWNER);
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
        replacementMap.put(Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE), Blocks.AIR.getDefaultState());
        replacementMap.put(Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK), Blocks.MOB_SPAWNER.getDefaultState());    
	}

	/**
	 * generic load - load all templates in template map
	 */
	public GottschTemplateManager loadAll(List<String> locations) {
		GottschCore.logger.debug("loading all structures...");
		for (String location : locations) {
			GottschCore.logger.debug("loading from -> {}", location);
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
	public Template load(ResourceLocation templatePath, List<Block> markerBlocks, Map<IBlockState, IBlockState> replacementBlocks) {
		String key = templatePath.toString();
		
		if (this.getTemplates().containsKey(key)) {
			GottschCore.logger.debug("read template from master map using key -> {}", key);
			return this.templates.get(key);
		}

		this.readTemplate(templatePath, markerBlocks, replacementBlocks);
		if (this.templates.get(key) != null) {
			GottschCore.logger.debug("Loaded template from -> {}", key);
		}
		else {
			GottschCore.logger.debug("Unable to read template from -> {}", key);
		}
		return this.templates.containsKey(key) ? (Template) this.templates.get(key) : null;
	}
	
	/**
	 * This reads a structure template from the given location and stores it. This
	 * first attempts get the template from an external folder. If it isn't there
	 * then it attempts to take it from the minecraft jar.
	 */
	public boolean readTemplate(ResourceLocation location, List<Block> markerBlocks, Map<IBlockState, IBlockState> replacementBlocks) {
		String s = location.getResourcePath();
		GottschCore.logger.debug("template resource path -> {}", s);
		String suffix = "";
		if (!s.endsWith(".nbt")) {
			suffix = ".nbt";
		}
		Path path = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), s + suffix);
		File file1 = path.toFile();
		GottschCore.logger.debug("template file path -> {}", file1.getAbsoluteFile());
		if (!file1.exists()) {
			GottschCore.logger.debug("file does not exist, read from jar -> {}", file1.getAbsolutePath());
			return this.readTemplateFromJar(location, markerBlocks, replacementBlocks);
		} else {
			GottschCore.logger.debug("reading template from file system using file path -> {}", file1.getAbsolutePath());
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
	private boolean readTemplateFromJar(ResourceLocation id, List<Block> markerBlocks, Map<IBlockState, IBlockState> replacementBlocks) {
		String s = id.getResourceDomain();
		String s1 = id.getResourcePath();
		InputStream inputstream = null;
		boolean flag;

		try {
			GottschCore.logger.debug("attempting to open resource stream -> {}", "/assets/" + s + "/strucutres/" + s1 + ".nbt");
			inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/structures/" + s1 + ".nbt");
			this.readTemplateFromStream(id.toString(), inputstream, markerBlocks, replacementBlocks);
			return true;
			// TODO change from Throwable
		} catch (Throwable var10) {
			GottschCore.logger.error("error reading resource: ", var10);
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
			Map<IBlockState, IBlockState> replacementBlocks) throws IOException {
		
		NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);

		if (!nbttagcompound.hasKey("DataVersion", 99)) {
			nbttagcompound.setInteger("DataVersion", 500);
		}

		GottschTemplate template = new GottschTemplate();
		template.read(this.fixer.process(FixTypes.STRUCTURE, nbttagcompound), markerBlocks, replacementBlocks);
		GottschCore.logger.debug("adding template to map with key -> {}", id);
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
		this.templates.remove(templatePath.getResourcePath());
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

	public Map<IBlockState, IBlockState> getReplacementMap() {
		return replacementMap;
	}

	public void setReplacementMap(Map<IBlockState, IBlockState> replacementMap) {
		this.replacementMap = replacementMap;
	}

}
