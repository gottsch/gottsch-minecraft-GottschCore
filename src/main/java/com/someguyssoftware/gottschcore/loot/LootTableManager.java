package com.someguyssoftware.gottschcore.loot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;
import com.someguyssoftware.gottschcore.loot.conditions.LootConditionManager;
import com.someguyssoftware.gottschcore.loot.functions.ILootFunction;
import com.someguyssoftware.gottschcore.loot.functions.LootFunctionManager;

import net.minecraft.util.ResourceLocation;

@Deprecated
/**
 * 
 * @author Mark Gottschling on May 29, 2020
 *
 */
public class LootTableManager /*extends JsonReloadListener*/ {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON_INSTANCE = (new GsonBuilder())
			.registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
			.registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer())
			.registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer())
			.registerTypeAdapter(IntClamper.class, new IntClamper.Serializer())
			.registerTypeAdapter(LootPool.class, new LootPool.Serializer())
			.registerTypeAdapter(LootTable.class, new LootTable.Serializer())
			.registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer())
			.registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer())
			.registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer())
			.registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
			.create();

	// TEMP make public
	public Map<ResourceLocation, LootTable> registeredLootTables = ImmutableMap.of();
//	private final LootPredicateManager predicateManager;

	/*
	 * From ForgeHooks
	 */
	private static ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal<Deque<LootTableContext>>();

	/*
	 * 
	 */
	private final File baseFolder;
	
	/*
	 * From ForgeHooks
	 */
	private static LootTableContext getLootTableContext() {
		LootTableContext ctx = lootContext.get().peek();
		if (ctx == null) {
			throw new JsonParseException("Invalid call stack, could not grab json context!");
		}
		return ctx;
	}

	/**
	 * 
	 * @param predicateManager
	 */
//	public LootTableManager(LootPredicateManager predicateManager) {
//		super(GSON_INSTANCE, "loot_tables");
//		this.predicateManager = predicateManager;
//	}
	
	/**
	 * 
	 * @param folder
	 */
	public LootTableManager(File folder) {
		// set this class' version of baseFolder and reload this class' tables
		this.baseFolder = folder;
	}

	public LootTable getLootTableFromLocation(ResourceLocation ressources) {
		return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
	}

	/**
	* Will have to be called on either 1) creation or 2) ServerStartEvent and run through all the folders and files like register does
	*/
	public void load(/*Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn*/ResourceLocation location) {
		// setup immutable map of location -> loot table
		Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
		builder.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
		ImmutableMap<ResourceLocation, LootTable> immutableMap = builder.build();
		
//		JsonObject jsonobject = objectIn.remove(LootTables.EMPTY);
//		if (jsonobject != null) {
//			GottschCore.LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object) LootTables.EMPTY);
//		}

		// TODO need to actually load file as string
//		objectIn.forEach((resourceLocation, jsonObject) -> {
//			try (IResource resource = resourceManagerIn.getResource(getPreparedPath(resourceLocation));) {
//				LootTable lootTable = loadLootTable(GSON_INSTANCE,
//						resourceLocation, jsonObject, resource == null || !resource.getPackName().equals("Default"),
//						this);
//				builder.put(resourceLocation, lootTable);
//			} catch (Exception exception) {
//				LOGGER.error("Couldn't parse loot table {}", resourceLocation, exception);
//			}
//
//		});
		if (location.getPath().contains(".")) {
			GottschCore.LOGGER.debug("Invalid loot table name '{}' (can't contain periods)", (Object) location);
			return;
		}
		else {
			GottschCore.LOGGER.debug("loading loot table from location -> {}", location);
			LootTable lootTable = this.loadLootTable(location);

			if (lootTable == null) {
				GottschCore.LOGGER.debug("custom location null, loading loot table from builtin -> {}", location);
				lootTable = this.loadBuiltinLootTable(location);
			}
			
			if (lootTable == null) {
				GottschCore.LOGGER.warn("Couldn't find resource table {}", (Object) location);
			}
			else {
				immutableMap.put(location, lootTable);
			}
		}
		

//		ValidationTracker validationTracker = new ValidationTracker(LootParameterSets.GENERIC,
//				this.predicateManager::func_227517_a_, immutableMap::get);
//		immutableMap.forEach((resourceLocation, lootTable) -> {
//			func_227508_a_(validationTracker, resourceLocation, lootTable);
//		});
//		validationTracker.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
//			LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
//		});

		this.registeredLootTables = immutableMap;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	private LootTable loadLootTable(ResourceLocation resource) {
		GottschCore.LOGGER.debug("baseFolder -> {}", this.baseFolder);
		LootTable lootTable = null;
		
		if (LootTableManager.this.baseFolder == null) {
			return null;
		} else {
			File lootTableFile = new File(new File(this.baseFolder, resource.getNamespace()), resource.getPath() + ".json");
			GottschCore.LOGGER.debug("file -> {}", lootTableFile.getAbsolutePath());
			if (lootTableFile.exists()) {
				GottschCore.LOGGER.debug("file exists!");
				if (lootTableFile.isFile()) {
					GottschCore.LOGGER.debug("file is a file!");
					String data;
					try {
						data = Files.toString(lootTableFile, StandardCharsets.UTF_8);
					} catch (IOException e) {
						GottschCore.LOGGER.warn("Couldn't load loot table {} from {}", resource, lootTableFile, e);
						return null;
					}
					
					try {
						return LootTableManager.loadLootTable(LootTableManager.GSON_INSTANCE, resource, data, true, LootTableManager.this);
					} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
						GottschCore.LOGGER.error("Couldn't load loot table {} from {}", resource, lootTableFile, jsonparseexception);
					}
				}
				else {
					GottschCore.LOGGER.warn("Expected to find loot table {} at {} but it was a folder.", resource, lootTableFile);
				}
			}
		}
		return lootTable;
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	private LootTable loadBuiltinLootTable(ResourceLocation resource) {
		URL url = LootTableManager.class.getResource("/assets/" + resource.getNamespace() + "/loot_tables/" + resource.getPath() + ".json");

		if (url != null) {
			String s;

			try {
				s = Resources.toString(url, StandardCharsets.UTF_8);
			} catch (IOException ioexception) {
				GottschCore.LOGGER.warn("Couldn't load loot table {} from {}", resource, url, ioexception);
				return LootTable.EMPTY_LOOT_TABLE;
			}

			try {
				// return
				return LootTableManager.loadLootTable(LootTableManager.GSON_INSTANCE, resource, s, false, LootTableManager.this);

			} catch (JsonParseException jsonparseexception) {
				GottschCore.LOGGER.error("Couldn't load loot table {} from {}", resource, url, jsonparseexception);
				return LootTable.EMPTY_LOOT_TABLE;
			}
		} else {
			return null;
		}
	}

	
	/**
	 * 
	 * @param gson
	 * @param resourceLocation
	 * @param jsonObject
	 * @param custom
	 * @param lootTableManager
	 * @return
	 */
	@Nullable
	public static LootTable loadLootTable(Gson gson, ResourceLocation resourceLocation, String data, boolean custom,
			LootTableManager lootTableManager) {
		Deque<LootTableContext> que = lootContext.get();
		if (que == null) {
			que = Queues.newArrayDeque();
			lootContext.set(que);
		}

		LootTable lootTable = null;
		try {
			que.push(new LootTableContext(resourceLocation, custom));
			lootTable = gson.fromJson(data, LootTable.class);
			que.pop();
		} catch (JsonParseException e) {
			que.pop();
			throw e;
		}

		if (lootTable != null) {
			lootTable.freeze();
		}
		return lootTable;
	}

//	public static void func_227508_a_(ValidationTracker validationTracker, ResourceLocation resourceLocation,
//			LootTable lootTable) {
//		lootTable.func_227506_a_(validationTracker.func_227529_a_(lootTable.getParameterSet())
//				.func_227531_a_("{" + resourceLocation + "}", resourceLocation));
//	}
//
//	public static JsonElement toJson(LootTable lootTableIn) {
//		return GSON_INSTANCE.toJsonTree(lootTableIn);
//	}
//
//	public Set<ResourceLocation> getLootTableKeys() {
//		return this.registeredLootTables.keySet();
//	}

	/**
	 * From ForgeHooks
	 * 
	 * @author Mark Gottschling on May 29, 2020
	 *
	 */
	private static class LootTableContext {
		public final ResourceLocation name;
		private final boolean vanilla;
		public final boolean custom;
		public int poolCount = 0;
		public int entryCount = 0;
		private HashSet<String> entryNames = Sets.newHashSet();

		private LootTableContext(ResourceLocation name, boolean custom) {
			this.name = name;
			this.custom = custom;
			this.vanilla = "minecraft".equals(this.name.getNamespace());
		}

		private void resetPoolCtx() {
			this.entryCount = 0;
			this.entryNames.clear();
		}

		public String validateEntryName(@Nullable String name) {
			if (name != null && !this.entryNames.contains(name)) {
				this.entryNames.add(name);
				return name;
			}

			if (!this.vanilla)
				throw new JsonParseException("Loot Table \"" + this.name.toString() + "\" Duplicate entry name \""
						+ name + "\" for pool #" + (this.poolCount - 1) + " entry #" + (this.entryCount - 1));

			int x = 0;
			while (this.entryNames.contains(name + "#" + x))
				x++;

			name = name + "#" + x;
			this.entryNames.add(name);

			return name;
		}
	}
}