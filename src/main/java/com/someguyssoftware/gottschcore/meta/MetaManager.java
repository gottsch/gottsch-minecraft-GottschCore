/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.gottschcore.meta;

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

import net.minecraft.resources.ResourceLocation;

/**
 * @author Mark Gottschling on Jul 28, 2019
 *
 */
public class MetaManager extends AbstractResourceManager {

	private static final String ASSETS_FOLDER = "assets";

	/*
	 * 
	 */
	private final Map<String, IMeta> metaMap = Maps.<String, IMeta>newHashMap();

	public MetaManager(IMod mod, String resourceFolder) {
		super(mod, resourceFolder);
	}

	/**
	 * 
	 */
	public void clear() {
		metaMap.clear();
	}

	public MetaManager loadAll(List<String> locations) {
		GottschCore.LOGGER.debug("loading all meta files...");
		for (String location : locations) {
			GottschCore.LOGGER.debug("loading from -> {}", location);
			load(new ResourceLocation(location));
		}
		return this;
	}

	/*
	 * Load built-in meta files from classpath
	 */
	public IMeta load(ResourceLocation location) {
		String key = location.toString();

		if (this.getMetaMap().containsKey(key)) {
			GottschCore.LOGGER.debug("read meta from master map using key -> {}", key);
			return this.getMetaMap().get(key);
		}

		// TODO load meta from Json
		readMeta(location);
		GottschCore.LOGGER.debug("output from master meta map:");
		for (Entry<String, IMeta> entry : getMetaMap().entrySet()) {
			GottschCore.LOGGER.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
		}
		if (this.getMetaMap().get(key) != null) {
			GottschCore.LOGGER.debug("Loaded meta file from -> {}", key);
		} else {
			GottschCore.LOGGER.debug("Unable to read meta file from -> {}", key);
		}
		return this.getMetaMap().containsKey(key) ? (IMeta) this.getMetaMap().get(key) : null;
	}

	/**
	 * 
	 * @param location
	 * @return
	 */
	public boolean readMeta(ResourceLocation location) {
		String loc = location.getPath();
		GottschCore.LOGGER.debug("meta file resource path -> {}", loc);
		String suffix = "";
		if (!loc.endsWith(".json")) {
			suffix = ".json";
		}
		Path path = Paths.get(getMod().getConfig().getConfigFolder(), getMod().getId(), loc + suffix);
		File file = path.toFile();
		GottschCore.LOGGER.debug("meta file path -> {}", file.getAbsoluteFile());
		if (!file.exists()) {
			GottschCore.LOGGER.debug("file does not exist, read from jar -> {}", file.getAbsolutePath());
			return this.readFromJar(location);
		} else {
			GottschCore.LOGGER.debug("read file from stream (file system) -> {}", file.getAbsolutePath());
			InputStream inputstream = null;
			boolean flag;

			try {
				inputstream = new FileInputStream(file);
				this.readFromStream(location.toString(), inputstream);
				return true;
			} catch (Throwable e) {
				GottschCore.LOGGER.error("error reading from stream: ", e);
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
		String resourceDomain = location.getNamespace();
		String resourcePath = location.getPath();
		InputStream inputstream = null;
		boolean flag;

		try {
			Path path = Paths.get("/data", resourceDomain, getBaseResourceFolder(), resourcePath + ".json");
//			GottschCore.LOGGER.debug("attempting to open resource stream -> {}", "/assets/" + resourceDomain + "/structures/" + resourcePath + ".json");
			GottschCore.LOGGER.debug("attempting to open resource stream -> {}", path.toString());
//			inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + resourceDomain + "/structures/" + resourcePath + ".nbt");
			inputstream = getMod().getClass().getResourceAsStream(path.toString());

			this.readFromStream(location.toString(), inputstream);
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
	protected void readFromStream(String id, InputStream stream) throws IOException, Exception {
		GottschCore.LOGGER.debug("reading meta file from stream.");
		IMeta meta = null;

		// read json sheet in and minify it
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSMin minifier = new JSMin(stream, out);
		minifier.jsmin();

		// out minified json into a json reader
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Reader reader = new InputStreamReader(in);
		JsonReader jsonReader = new JsonReader(reader);

		// create a gson builder
		GsonBuilder gsonBuilder = new GsonBuilder();

		/*
		 * create types for all the properties of a StyleSheet
		 */
//		Type styleType = new TypeToken<IMeta>() {}.getType();

		/*
		 * register the types with the custom deserializer
		 */
//		gsonBuilder.registerTypeAdapter(styleType, new StyleDeserializer());
		Gson gson = gsonBuilder.create();

		// read minified json into gson and generate objects
		try {
			meta = gson.fromJson(jsonReader, Meta.class);
			GottschCore.LOGGER.debug("meta -> {}", meta);
		} catch (JsonIOException | JsonSyntaxException e) {
			// TODO change to custom exception
			throw new Exception("Unable to meta file:", e);
		} finally {
			// close objects
			try {
				jsonReader.close();
			} catch (IOException e) {
				GottschCore.LOGGER.warn("Unable to close JSON Reader when reading meta file.");
			}
		}

		// add meta to map
		this.getMetaMap().put(id, meta);
	}

	public Map<String, IMeta> getMetaMap() {
		return metaMap;
	}
}
