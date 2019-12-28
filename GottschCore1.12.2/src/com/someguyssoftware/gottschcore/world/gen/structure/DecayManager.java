/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.json.JSMin;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.resource.AbstractResourceManager;

import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Dec 20, 2019
 *
 */
public class DecayManager extends AbstractResourceManager {

	private static final String ASSETS_FOLDER = "assets";
	
	/*
	 * 
	 */
	private final Map<String, IDecayRuleSet> ruleSetMap = Maps.<String, IDecayRuleSet>newHashMap();
	
	public DecayManager(IMod mod, String resourceFolder) {
		super(mod, resourceFolder);
	}

	/**
	 * 
	 */
	public void clear() {
		ruleSetMap.clear();
	}
	
	public DecayManager loadAll(List<String> locations) {
		GottschCore.logger.debug("loading all decay ruleset files...");
		for (String location : locations) {
			GottschCore.logger.debug("loading from -> {}", location);
			load(new ResourceLocation(location));
		}		
		return this;
	}
	
	/*
	 * Load built-in decayRuleSet files from classpath
	 */
	public IDecayRuleSet  load(ResourceLocation location) {
		String key = location.toString();
		
		if (this.getRuleSetMap().containsKey(key)) {
			GottschCore.logger.debug("read ruleset from master map using key -> {}", key);
			return this.getRuleSetMap().get(key);
		}
		
		readRuleSet(location);
		GottschCore.logger.debug("output from master decayRuleSet map:");
		for (Entry<String, IDecayRuleSet> entry : getRuleSetMap().entrySet()) {
			GottschCore.logger.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
		}
		if (this.getRuleSetMap().get(key) != null) {
			GottschCore.logger.debug("Loaded decayRuleSet file from -> {}", key);
		}
		else {
			GottschCore.logger.debug("Unable to read decayRuleSet file from -> {}", key);
		}
		return this.getRuleSetMap().containsKey(key) ? (IDecayRuleSet) this.getRuleSetMap().get(key) : null;	
	}

	public boolean readRuleSet(ResourceLocation location) {
		String loc = location.getResourcePath();
		GottschCore.logger.debug("decay ruleset file resource path -> {}", loc);
		String suffix = "";
		if (!loc.endsWith(".json")) {
			suffix = ".json";
		}
		Path path = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), loc + suffix);
		File file = path.toFile();
		GottschCore.logger.debug("template file path -> {}", file.getAbsoluteFile());
		if (!file.exists()) {
			GottschCore.logger.debug("file does not exist, read from jar -> {}", file.getAbsolutePath());
			return this.readFromJar(location);
		} else {
			GottschCore.logger.debug("read file from stream (file system) -> {}", file.getAbsolutePath());
			InputStream inputstream = null;
			boolean flag;

			try {
				inputstream = new FileInputStream(file);
				this.readFromStream(location.toString(), inputstream);
				return true;
			} catch (Throwable e) {
				GottschCore.logger.error("error reading from stream: ", e);
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
	protected boolean readFromJar(ResourceLocation location) {
		String resourceDomain = location.getResourceDomain();
		String resourcePath = location.getResourcePath();
		InputStream inputstream = null;
		boolean flag;

		try {
			Path path = Paths.get(ASSETS_FOLDER, resourceDomain, getBaseResourceFolder(), resourcePath + ".json");
			GottschCore.logger.debug("attempting to open resource stream -> {}", path.toString());
			inputstream = getMod().getClass().getResourceAsStream(path.toString());

			this.readFromStream(location.toString(), inputstream);
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
	protected void readFromStream(String id, InputStream stream) throws IOException, Exception {
		GottschCore.logger.debug("reading decayRuleSet file from stream.");
		IDecayRuleSet decayRuleSet = null;
		
		// read json sheet in and minify it
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSMin minifier = new JSMin(stream, out);
		minifier.jsmin();

		// out minified json into a json reader
		ByteArrayInputStream in  = new ByteArrayInputStream(out.toByteArray());
		Reader reader = new InputStreamReader(in);
		JsonReader jsonReader = new JsonReader(reader);
		
		// create a gson builder
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		/*
		 * register the types with the custom deserializer
		 */
		Gson gson = gsonBuilder.create();	

		// read minified json into gson and generate objects
		try {
			decayRuleSet = gson.fromJson(jsonReader, DecayRuleSet.class);
			GottschCore.logger.debug("decayRuleSet -> {}", decayRuleSet);
		}
		catch(JsonIOException | JsonSyntaxException e) {
			// TODO change to custom exception
			throw new Exception("Unable to load decayRuleSet file:", e);
		}
		finally {
			// close objects
			try {
				jsonReader.close();
			} catch (IOException e) {
				GottschCore.logger.warn("Unable to close JSON Reader when reading decayRuleSet file.");
			}
		}
		
		// add decayRuleSet to map
		this.getRuleSetMap().put(id, decayRuleSet);
	}
	
	public Map<String, IDecayRuleSet> getRuleSetMap() {
		return ruleSetMap;
	}
}
