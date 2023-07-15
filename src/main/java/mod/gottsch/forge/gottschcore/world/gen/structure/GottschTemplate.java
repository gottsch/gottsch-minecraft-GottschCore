package mod.gottsch.forge.gottschcore.world.gen.structure;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;

import mod.gottsch.forge.gottschcore.GottschCore;
import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;


/**
 * 
 * @author Mark Gottschling on Mar 26, 2021
 *
 */
public class GottschTemplate extends StructureTemplate {
	/** blocks in the structure */
	private final List<GottschTemplate.Palette> palettes = Lists.newArrayList();
	/** entities in the structure */
	private final List<GottschTemplate.EntityInfo> entities = Lists.newArrayList();
	/** size of the structure */
	private BlockPos size = BlockPos.ZERO;
	private String author = "?";

	/*
	 * A map of all the specials within the template.
	 */
	private final Multimap<Block, BlockInfoContext> tagBlockMap = ArrayListMultimap.create();

	/*
	 * A list of block classes to check for post processing
	 */
	public static final List<String> deferredBlocks = Lists.newArrayList();

	static {
		deferredBlocks.add(DoorBlock.class.getSimpleName());
		deferredBlocks.add(TorchBlock.class.getSimpleName());
		deferredBlocks.add(LeverBlock.class.getSimpleName());
	}

	public BlockPos getSize() {
		return this.size;
	}

	public BlockPos getSize(Rotation rotation) {
		return switch(rotation) {
		case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
		default -> this.size;
		};
	}

	public void setAuthor(String authorIn) {
		this.author = authorIn;
	}

	public String getAuthor() {
		return this.author;
	}

	private static void addToLists(GottschTemplate.BlockInfo blockInfo, List<GottschTemplate.BlockInfo> blockInfos, List<GottschTemplate.BlockInfo> p_237149_2_, List<GottschTemplate.BlockInfo> p_237149_3_) {
		if (blockInfo.nbt != null) {
			p_237149_2_.add(blockInfo);
		} else if (!blockInfo.state.getBlock().hasDynamicShape() && blockInfo.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
			blockInfos.add(blockInfo);
		} else {
			p_237149_3_.add(blockInfo);
		}
	}

	private static List<GottschTemplate.BlockInfo> buildInfoList(List<GottschTemplate.BlockInfo> p_237151_0_, List<GottschTemplate.BlockInfo> p_237151_1_, List<GottschTemplate.BlockInfo> p_237151_2_) {
		Comparator<GottschTemplate.BlockInfo> comparator = Comparator.<GottschTemplate.BlockInfo>comparingInt((p_237154_0_) -> {
			return p_237154_0_.pos.getY();
		}).thenComparingInt((p_237153_0_) -> {
			return p_237153_0_.pos.getX();
		}).thenComparingInt((p_237148_0_) -> {
			return p_237148_0_.pos.getZ();
		});
		p_237151_0_.sort(comparator);
		p_237151_2_.sort(comparator);
		p_237151_1_.sort(comparator);
		List<GottschTemplate.BlockInfo> list = Lists.newArrayList();
		list.addAll(p_237151_0_);
		list.addAll(p_237151_2_);
		list.addAll(p_237151_1_);
		return list;
	}

	/**
	 * 
	 */
	public List<GottschTemplate.BlockInfo> filterBlocks(BlockPos pos, PlacementSettings placement, Block block) {
		return this.filterBlocks(pos, placement, block, true);
	}

	/**
	 * 
	 */
	public List<GottschTemplate.BlockInfo> filterBlocks(BlockPos pos, PlacementSettings placement, Block block, boolean rotate) {
		List<GottschTemplate.BlockInfo> list = Lists.newArrayList();
		BoundingBox mutableboundingbox = placement.getBoundingBox();
		if (this.palettes.isEmpty()) {
			return Collections.emptyList();
		} else {
			for(GottschTemplate.BlockInfo GottschTemplateNew$blockinfo : placement.getRandomPalette(this.palettes, pos).blocks(block)) {
				BlockPos blockpos = rotate ? calculateRelativePosition(placement, GottschTemplateNew$blockinfo.pos).offset(pos) : GottschTemplateNew$blockinfo.pos;
				if (mutableboundingbox == null || mutableboundingbox.isInside(blockpos)) {
					list.add(new GottschTemplate.BlockInfo(blockpos, GottschTemplateNew$blockinfo.state.rotate(placement.getRotation()), GottschTemplateNew$blockinfo.nbt));
				}
			}

			return list;
		}
	}

	public static BlockPos calculateRelativePosition(PlacementSettings placement, BlockPos p_186266_1_) {
		return transform(p_186266_1_, placement.getMirror(), placement.getRotation(), placement.getRotationPivot());
	}

	public static Vec3 transformedVec3d(PlacementSettings placementIn, Vec3 pos) {
		return transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
	}

	/**
	 * Non-Decay version.
	 * Adds blocks and entities from this structure to the given world.
	 */
	public boolean placeInWorld(ServerLevelAccessor world, BlockPos pos, BlockPos pos2, PlacementSettings placement, final Block NULL_BLOCK, Map<BlockState, BlockState> replacementBlocks, RandomSource random, int flags) {
		if (this.palettes.isEmpty()) {
			return false;
		} else {
			List<GottschTemplate.BlockInfo> blockInfoList = placement.getRandomPalette(this.palettes, pos).blocks();
			if ((!blockInfoList.isEmpty() || !placement.isIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
				BoundingBox boundingBox = placement.getBoundingBox();
				List<BlockPos> list1 = Lists.newArrayListWithCapacity(placement.shouldKeepLiquids() ? blockInfoList.size() : 0);
				List<Pair<BlockPos, CompoundTag>> list2 = Lists.newArrayListWithCapacity(blockInfoList.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int i1 = Integer.MIN_VALUE;
				int j1 = Integer.MIN_VALUE;

				//================= GottschCore =================//
				List<BlockInfoContext> blockInfoContexts = new ArrayList<>();
				//============== End of GottschCore ===============//

				for(GottschTemplate.BlockInfo blockInfo : processBlockInfos(world, pos, pos2, placement, blockInfoList, this)) {
					BlockPos blockPos = blockInfo.pos;
					//================= GottschCore =================//
					// replace block with null block if it is a marker block
					Block processedBlock = blockInfo.state.getBlock();
					if (this.tagBlockMap.containsKey(processedBlock)) {
						processedBlock = NULL_BLOCK;
					}

					//					if (boundingBox == null || boundingBox.isInside(blockpos)) {
					if ((boundingBox == null || boundingBox.isInside(pos)) && processedBlock != NULL_BLOCK) {
						//============== End of GottschCore ===============//
						FluidState fluidstate = placement.shouldKeepLiquids() ? world.getFluidState(blockPos) : null;
						BlockState processedBlockState = blockInfo.state.mirror(placement.getMirror()).rotate(placement.getRotation());
						//						GottschCore.LOGGER.debug("processedBlock -> {}", processedBlockState.getBlock().getRegistryName());
						//================= GottschCore =================//
						if (replacementBlocks != null && replacementBlocks.containsKey(processedBlockState)) {
//							GottschCore.LOGGER.debug("replacing block -> {} for ...", processedBlockState.getBlock().getRegistryName());
							processedBlockState = replacementBlocks.get(processedBlockState);
//							GottschCore.LOGGER.debug("... replacement block -> {}", processedBlockState.getBlock().getRegistryName());
						}
						//============== End of GottschCore ===============//
						if (blockInfo.nbt != null) {
							BlockEntity tileentity = world.getBlockEntity(blockPos);
							Clearable.tryClear(tileentity);
							world.setBlock(blockPos, Blocks.BARRIER.defaultBlockState(), 20);
						}

						//================= GottschCore =================//
						//// 1. Save the block context
						//// 2. checkif the block should be deferred                        
						BlockInfoContext blockInfoContext = new BlockInfoContext(blockInfo, new Coords(pos), processedBlockState);
						if (deferredBlocks.contains(processedBlockState.getBlock().getClass().getSimpleName())) { // why use the classname? the Block would work - it's a singleton
							blockInfoContexts.add(blockInfoContext);
						}
						else {
							// TODO run the method that contains the below condition
						}
						//============== End of GottschCore ===============//

						// TODO this condition will need to be in it's own method in order to handle deferred blocks
						if (world.setBlock(blockPos, processedBlockState, flags)) {
							i = Math.min(i, blockPos.getX());
							j = Math.min(j, blockPos.getY());
							k = Math.min(k, blockPos.getZ());
							l = Math.max(l, blockPos.getX());
							i1 = Math.max(i1, blockPos.getY());
							j1 = Math.max(j1, blockPos.getZ());
							list2.add(Pair.of(blockPos, blockInfo.nbt));
							if (blockInfo.nbt != null) {
								BlockEntity tileentity1 = world.getBlockEntity(blockPos);
								if (tileentity1 != null) {
									blockInfo.nbt.putInt("x", blockPos.getX());
									blockInfo.nbt.putInt("y", blockPos.getY());
									blockInfo.nbt.putInt("z", blockPos.getZ());
									if (tileentity1 instanceof RandomizableContainerBlockEntity) {
										blockInfo.nbt.putLong("LootTableSeed", random.nextLong());
									}
									tileentity1.load(blockInfo.nbt);
								}
							}

							if (fluidstate != null && processedBlockState.getBlock() instanceof LiquidBlockContainer) {
								((LiquidBlockContainer)processedBlockState.getBlock()).placeLiquid(world, blockPos, processedBlockState, fluidstate);
								if (!fluidstate.isSource()) {
									list1.add(blockPos);
								}
							}
						}
					}
				} // end of blockInfo processing

				boolean flag = true;
				Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while(flag && !list1.isEmpty()) {
					flag = false;
					Iterator<BlockPos> iterator = list1.iterator();

					while(iterator.hasNext()) {
						BlockPos blockpos2 = iterator.next();
						BlockPos blockpos3 = blockpos2;
						FluidState fluidstate2 = world.getFluidState(blockpos2);

						for(int k1 = 0; k1 < adirection.length && !fluidstate2.isSource(); ++k1) {
							BlockPos blockpos1 = blockpos3.relative(adirection[k1]);
							FluidState fluidstate1 = world.getFluidState(blockpos1);
							if (fluidstate1.getHeight(world, blockpos1) > fluidstate2.getHeight(world, blockpos3) || fluidstate1.isSource() && !fluidstate2.isSource()) {
								fluidstate2 = fluidstate1;
								blockpos3 = blockpos1;
							}
						}

						if (fluidstate2.isSource()) {
							BlockState blockstate2 = world.getBlockState(blockpos2);
							Block block = blockstate2.getBlock();
							if (block instanceof LiquidBlockContainer) {
								((LiquidBlockContainer)block).placeLiquid(world, blockpos2, blockstate2, fluidstate2);
								flag = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l) {
					if (!placement.getKnownShape()) {
						DiscreteVoxelShape voxelshapepart = new BitSetDiscreteVoxelShape(l - i + 1, i1 - j + 1, j1 - k + 1);
						int l1 = i;
						int i2 = j;
						int j2 = k;

						for(Pair<BlockPos, CompoundTag> pair1 : list2) {
							BlockPos blockpos5 = pair1.getFirst();
							voxelshapepart.fill(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2);
						}

						updateShapeAtEdge(world, flags, voxelshapepart, l1, i2, j2);
					}

					for(Pair<BlockPos, CompoundTag> pair : list2) {
						BlockPos blockpos4 = pair.getFirst();
						if (!placement.getKnownShape()) {
							BlockState blockstate1 = world.getBlockState(blockpos4);
							BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate1, world, blockpos4);
							if (blockstate1 != blockstate3) {
								world.setBlock(blockpos4, blockstate3, flags & -2 | 16);
							}

							world.blockUpdated(blockpos4, blockstate3.getBlock());
						}

						if (pair.getSecond() != null) {
							BlockEntity tileentity2 = world.getBlockEntity(blockpos4);
							if (tileentity2 != null) {
								tileentity2.setChanged();
							}
						}
					}
				}

				if (!placement.isIgnoreEntities()) {
					this.addEntitiesToWorld(world, pos, placement);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Decay version.
	 * Adds blocks and entities from this structure to the given world.
	 */
	public boolean placeInWorld(ServerLevel world, BlockPos blockPos, BlockPos pos2, PlacementSettings placement, IDecayProcessor decayProcessor, 
			final Block NULL_BLOCK, Map<BlockState, BlockState> replacementBlocks, Random random, int flags) {

		if (this.palettes.isEmpty()) {
			return false;
		} else {
			List<GottschTemplate.BlockInfo> blockInfoList = placement.getRandomPalette(this.palettes, blockPos).blocks();
			if ((!blockInfoList.isEmpty() || !placement.isIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
				BoundingBox boundingBox = placement.getBoundingBox();
				List<BlockPos> list1 = Lists.newArrayListWithCapacity(placement.shouldKeepLiquids() ? blockInfoList.size() : 0);
				List<Pair<BlockPos, CompoundTag>> list2 = Lists.newArrayListWithCapacity(blockInfoList.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int i1 = Integer.MIN_VALUE;
				int j1 = Integer.MIN_VALUE;

				//================= GottschCore =================//
				List<BlockInfoContext> blockInfoContexts = new ArrayList<>();
				//============== End of GottschCore ===============//

				for(GottschTemplate.BlockInfo blockInfo : processBlockInfos(world, blockPos, pos2, placement, blockInfoList, this)) {
					BlockPos blockpos = blockInfo.pos;
					//================= GottschCore =================//
					// replace block with null block if it is a marker block
					Block processedBlock = blockInfo.state.getBlock();
					if (this.tagBlockMap.containsKey(processedBlock)) {
						processedBlock = NULL_BLOCK;
					}

					//					if (boundingBox == null || boundingBox.isInside(blockpos)) {
					if ((boundingBox == null || boundingBox.isInside(blockPos)) && processedBlock != NULL_BLOCK) {
						//============== End of GottschCore ===============//

						//						FluidState fluidstate = placement.shouldKeepLiquids() ? serverWorld.getFluidState(blockpos) : null;
						BlockState processedBlockState = blockInfo.state.mirror(placement.getMirror()).rotate(placement.getRotation());

						//						GottschCore.LOGGER.debug("processedBlock -> {}", processedBlockState.getBlock().getRegistryName());
						//================= GottschCore Replacement Code =================//
						if (replacementBlocks != null && replacementBlocks.containsKey(processedBlockState)) {
//							GottschCore.LOGGER.debug("replacing block -> {} for ...", processedBlockState.getBlock().getRegistryName());
							processedBlockState = replacementBlocks.get(processedBlockState);
//							GottschCore.LOGGER.debug("... replacement block -> {}", processedBlockState.getBlock().getRegistryName());
						}
						// add blockstate to structure map (read pass).
						decayProcessor.add(new Coords(blockPos), blockInfo, processedBlockState);

						//============== End of GottschCore ===============//
					}
				} // end of blockInfo processing

				//================= GottschCore Code =================//
				// need the transformed size
				ICoords transformedSize = new Coords(getSize(placement.getRotation()));

				GottschCore.LOGGER.debug("transformed size -> {}", transformedSize.toShortString());
				List<DecayBlockInfo> decayBlockInfoList = decayProcessor.process(world, new Random(), transformedSize,
						NULL_BLOCK);
				GottschCore.LOGGER.debug("decayBlockInfoList.size={}", decayBlockInfoList.size());
				for (DecayBlockInfo decay : decayBlockInfoList) {
					if (decay.getState().getBlock() == NULL_BLOCK)
						continue;

					BlockInfo processed = decay.getBlockInfo();
					BlockPos decayPos = decay.getCoords().toPos();

					if (processed.nbt != null) {
						BlockEntity tileentity = world.getBlockEntity(decayPos);
						Clearable.tryClear(tileentity);
						world.setBlock(decayPos, Blocks.BARRIER.defaultBlockState(), 20);
					}

					FluidState fluidstate = placement.shouldKeepLiquids() ? world.getFluidState(decayPos) : null;
					if (world.setBlock(decayPos, decay.getState(), flags)) {
						i = Math.min(i, decayPos.getX());
						j = Math.min(j, decayPos.getY());
						k = Math.min(k, decayPos.getZ());
						l = Math.max(l, decayPos.getX());
						i1 = Math.max(i1, decayPos.getY());
						j1 = Math.max(j1, decayPos.getZ());
						list2.add(Pair.of(decayPos, processed.nbt));
						if (processed.nbt != null) {
							BlockEntity tileentity1 = world.getBlockEntity(decayPos);
							if (tileentity1 != null) {
								processed.nbt.putInt("x", decayPos.getX());
								processed.nbt.putInt("y", decayPos.getY());
								processed.nbt.putInt("z", decayPos.getZ());
								if (tileentity1 instanceof RandomizableContainerBlockEntity) {
									processed.nbt.putLong("LootTableSeed", random.nextLong());
								}

								tileentity1.load(processed.nbt);
							}
						}

						if (fluidstate != null && decay.getState().getBlock() instanceof LiquidBlockContainer) {
							((LiquidBlockContainer)decay.getState().getBlock()).placeLiquid(world, decayPos, decay.getState(), fluidstate);
							if (!fluidstate.isSource()) {
								list1.add(decayPos);
							}
						}
					}
				}
				//============== End of GottschCore ===============//

				// vanilla
				boolean flag = true;
				Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while(flag && !list1.isEmpty()) {
					flag = false;
					Iterator<BlockPos> iterator = list1.iterator();

					while(iterator.hasNext()) {
						BlockPos blockpos2 = iterator.next();
						BlockPos blockpos3 = blockpos2;
						FluidState fluidstate2 = world.getFluidState(blockpos2);

						for(int k1 = 0; k1 < adirection.length && !fluidstate2.isSource(); ++k1) {
							BlockPos blockpos1 = blockpos3.relative(adirection[k1]);
							FluidState fluidstate1 = world.getFluidState(blockpos1);
							if (fluidstate1.getHeight(world, blockpos1) > fluidstate2.getHeight(world, blockpos3) || fluidstate1.isSource() && !fluidstate2.isSource()) {
								fluidstate2 = fluidstate1;
								blockpos3 = blockpos1;
							}
						}

						if (fluidstate2.isSource()) {
							BlockState blockstate2 = world.getBlockState(blockpos2);
							Block block = blockstate2.getBlock();
							if (block instanceof LiquidBlockContainer) {
								((LiquidBlockContainer)block).placeLiquid(world, blockpos2, blockstate2, fluidstate2);
								flag = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l) {
					if (!placement.getKnownShape()) {
						DiscreteVoxelShape voxelshapepart = new BitSetDiscreteVoxelShape(l - i + 1, i1 - j + 1, j1 - k + 1);
						int l1 = i;
						int i2 = j;
						int j2 = k;

						for(Pair<BlockPos, CompoundTag> pair1 : list2) {
							BlockPos blockpos5 = pair1.getFirst();
							voxelshapepart.fill(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2);
						}

						updateShapeAtEdge(world, flags, voxelshapepart, l1, i2, j2);
					}

					for(Pair<BlockPos, CompoundTag> pair : list2) {
						BlockPos blockpos4 = pair.getFirst();
						if (!placement.getKnownShape()) {
							BlockState blockstate1 = world.getBlockState(blockpos4);
							BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate1, world, blockpos4);
							if (blockstate1 != blockstate3) {
								world.setBlock(blockpos4, blockstate3, flags & -2 | 16);
							}

							world.blockUpdated(blockpos4, blockstate3.getBlock());
						}

						if (pair.getSecond() != null) {
							BlockEntity tileentity2 = world.getBlockEntity(blockpos4);
							if (tileentity2 != null) {
								tileentity2.setChanged();
							}
						}
					}
				}

				if (!placement.isIgnoreEntities()) {
					this.addEntitiesToWorld((ServerLevel)world, blockPos, placement);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	   @Deprecated //Use Forge version
	   public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(LevelAccessor p_74518_, BlockPos p_74519_, BlockPos p_74520_, StructurePlaceSettings p_74521_, List<StructureTemplate.StructureBlockInfo> p_74522_) {
	      return processBlockInfos(p_74518_, p_74519_, p_74520_, p_74521_, p_74522_, null);
	   }

	public static List<GottschTemplate.BlockInfo> processBlockInfos(LevelAccessor world, BlockPos pos, BlockPos pos2, PlacementSettings placement, List<GottschTemplate.BlockInfo> blockInfos, @Nullable GottschTemplate template) {
		List<GottschTemplate.BlockInfo> list = Lists.newArrayList();

		for(GottschTemplate.BlockInfo GottschTemplateNew$blockinfo : blockInfos) {
			BlockPos blockpos = calculateRelativePosition(placement, GottschTemplateNew$blockinfo.pos).offset(pos);
			GottschTemplate.BlockInfo GottschTemplateNew$blockinfo1 = new GottschTemplate.BlockInfo(blockpos, GottschTemplateNew$blockinfo.state, GottschTemplateNew$blockinfo.nbt != null ? GottschTemplateNew$blockinfo.nbt.copy() : null);

			for(Iterator<StructureProcessor> iterator = placement.getProcessors().iterator(); GottschTemplateNew$blockinfo1 != null && iterator.hasNext(); GottschTemplateNew$blockinfo1 = iterator.next().process(world, pos, GottschTemplateNew$blockinfo, GottschTemplateNew$blockinfo1, placement, template)) {
			}

			if (GottschTemplateNew$blockinfo1 != null) {
				list.add(GottschTemplateNew$blockinfo1);
			}
		}

		return list;
	}

	public static List<GottschTemplate.EntityInfo> processEntityInfos(@Nullable GottschTemplate GottschTemplate, LevelAccessor p_215387_0_, BlockPos p_215387_1_, PlacementSettings p_215387_2_, List<GottschTemplate.EntityInfo> p_215387_3_) {
		List<GottschTemplate.EntityInfo> list = Lists.newArrayList();
		for(GottschTemplate.EntityInfo entityInfo : p_215387_3_) {
			Vec3 pos = transformedVec3d(p_215387_2_, entityInfo.pos).add(Vec3.atLowerCornerOf(p_215387_1_));
			BlockPos blockpos = calculateRelativePosition(p_215387_2_, entityInfo.blockPos).offset(p_215387_1_);
			GottschTemplate.EntityInfo info = new GottschTemplate.EntityInfo(pos, blockpos, entityInfo.nbt);
			for (StructureProcessor proc : p_215387_2_.getProcessors()) {
				info = proc.processEntity(p_215387_0_, p_215387_1_, entityInfo, info, p_215387_2_, GottschTemplate);
				if (info == null)
					break;
			}
			if (info != null)
				list.add(info);
		}
		return list;
	}

	private void addEntitiesToWorld(ServerLevelAccessor p_237143_1_, BlockPos p_237143_2_, PlacementSettings placementIn) {
		for(GottschTemplate.EntityInfo GottschTemplateNew$entityinfo : processEntityInfos(this, p_237143_1_, p_237143_2_, placementIn, this.entities)) {
			BlockPos blockpos = transform(GottschTemplateNew$entityinfo.blockPos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot()).offset(p_237143_2_);
			blockpos = GottschTemplateNew$entityinfo.blockPos; // FORGE: Position will have already been transformed by processEntityInfos
			if (placementIn.getBoundingBox() == null || placementIn.getBoundingBox().isInside(blockpos)) {
				CompoundTag CompoundTag = GottschTemplateNew$entityinfo.nbt.copy();
				Vec3 vector3d1 = GottschTemplateNew$entityinfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
				ListTag ListTag = new ListTag();
				ListTag.add(DoubleTag.valueOf(vector3d1.x));
				ListTag.add(DoubleTag.valueOf(vector3d1.y));
				ListTag.add(DoubleTag.valueOf(vector3d1.z));
				CompoundTag.put("Pos", ListTag);
				CompoundTag.remove("UUID");
				createEntityIgnoreException(p_237143_1_, CompoundTag).ifPresent((p_242927_6_) -> {
					float f = p_242927_6_.mirror(placementIn.getMirror());
					f = f + (p_242927_6_.getYRot() - p_242927_6_.rotate(placementIn.getRotation()));
					p_242927_6_.moveTo(vector3d1.x, vector3d1.y, vector3d1.z, f, p_242927_6_.getXRot());
					if (placementIn.shouldFinalizeEntities() && p_242927_6_ instanceof Mob) {
						((Mob)p_242927_6_).finalizeSpawn(p_237143_1_, p_237143_1_.getCurrentDifficultyAt(new BlockPos(vector3d1)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, CompoundTag);
					}

					p_237143_1_.addFreshEntityWithPassengers(p_242927_6_);
				});
			}
		}

	}

	private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor p_215382_0_, CompoundTag p_215382_1_) {
		try {
			return EntityType.create(p_215382_1_, p_215382_0_.getLevel());
		} catch (Exception exception) {
			return Optional.empty();
		}
	}

	/**
	 * Re-direct to GottschTemplate's version to ensure any extra processing is done.
	 */
	@Override
	   public void load(HolderGetter<Block> getter, CompoundTag tag) {
		load(getter, tag, new ArrayList<Block>(), new HashMap<BlockState, BlockState>());
	}

	/**
	 * 
	 * @param tag
	 * @param markerBlocks
	 * @param replacementBlocks
	 */
	public void load(HolderGetter<Block> getter, CompoundTag tag, List<Block> markerBlocks,
			Map<BlockState, BlockState> replacementBlocks) {
		this.palettes.clear();
		this.entities.clear();
		ListTag ListTag = tag.getList("size", 3);
		this.size = new BlockPos(ListTag.getInt(0), ListTag.getInt(1), ListTag.getInt(2));
		ListTag blocksNBT = tag.getList("blocks", 10);
		if (tag.contains("palettes", 9)) {
			ListTag palettesNBT2 = tag.getList("palettes", 9);

			for(int i = 0; i < palettesNBT2.size(); ++i) {
				this.loadPalette(getter, palettesNBT2.getList(i), blocksNBT, markerBlocks, replacementBlocks);
			}
		} else {
			this.loadPalette(getter, tag.getList("palette", 10), blocksNBT, markerBlocks, replacementBlocks);
		}

		ListTag ListTag5 = tag.getList("entities", 10);

		for(int j = 0; j < ListTag5.size(); ++j) {
			CompoundTag CompoundTag = ListTag5.getCompound(j);
			ListTag ListTag3 = CompoundTag.getList("pos", 6);
			Vec3 vector3d = new Vec3(ListTag3.getDouble(0), ListTag3.getDouble(1), ListTag3.getDouble(2));
			ListTag ListTag4 = CompoundTag.getList("blockPos", 3);
			BlockPos blockpos = new BlockPos(ListTag4.getInt(0), ListTag4.getInt(1), ListTag4.getInt(2));
			if (CompoundTag.contains("nbt")) {
				CompoundTag CompoundTag1 = CompoundTag.getCompound("nbt");
				this.entities.add(new GottschTemplate.EntityInfo(vector3d, blockpos, CompoundTag1));
			}
		}

	}

	private void loadPalette(HolderGetter<Block> getter, ListTag palettes, ListTag blocks, List<Block> markerBlocks,
			Map<BlockState, BlockState> replacementBlocks) {
		GottschTemplate.SimplePalette GottschTemplateNew$basicpalette = new GottschTemplate.SimplePalette();

		for(int i = 0; i < palettes.size(); ++i) {
			GottschTemplateNew$basicpalette.addMapping(NbtUtils.readBlockState(getter, palettes.getCompound(i)), i);
		}

		List<GottschTemplate.BlockInfo> list2 = Lists.newArrayList();
		List<GottschTemplate.BlockInfo> list = Lists.newArrayList();
		List<GottschTemplate.BlockInfo> list1 = Lists.newArrayList();

		for(int j = 0; j < blocks.size(); ++j) {
			CompoundTag CompoundTag = blocks.getCompound(j);
			ListTag ListTag = CompoundTag.getList("pos", 3);
			BlockPos blockPos = new BlockPos(ListTag.getInt(0), ListTag.getInt(1), ListTag.getInt(2));
			BlockState blockState = GottschTemplateNew$basicpalette.stateFor(CompoundTag.getInt("state"));
			CompoundTag CompoundTag1;
			if (CompoundTag.contains("nbt")) {
				CompoundTag1 = CompoundTag.getCompound("nbt");
			} else {
				CompoundTag1 = null;
			}

			GottschTemplate.BlockInfo GottschTemplateNew$blockinfo = new GottschTemplate.BlockInfo(blockPos, blockState, CompoundTag1);
			addToLists(GottschTemplateNew$blockinfo, list2, list, list1);

			//================= GottschCore =================//
			// check if a marker block
			Block block = blockState.getBlock();
			// TODO could check if block has nbt that is marker block
			/*
            if (GottschTemplateNew$blockinfo.nbt != null) {
                StructureMode structuremode = StructureMode.valueOf(GottschTemplateNew$blockinfo.nbt.getString("mode"));
                if (structuremode == StructureMode.DATA) {
                   GottschTemplateNew$blockinfo.nbt.getString("metadata"); // --> gets the name of the data
                }
             }
			 */
			if (block != Blocks.AIR && markerBlocks.contains(block)) {
				// add pos to map
//				GottschCore.LOGGER.debug("template map adding block -> {} with pos -> {}", block.getRegistryName(),
//						blockPos);
				tagBlockMap.put(block, new BlockInfoContext(GottschTemplateNew$blockinfo, new Coords(blockPos), blockState));
			}
			//============== End of GottschCore ===============//
		}

		List<GottschTemplate.BlockInfo> list3 = buildInfoList(list2, list, list1);
		this.palettes.add(new GottschTemplate.Palette(list3));
	}

	static class SimplePalette implements Iterable<BlockState> {
		public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
		private final IdMapper<BlockState> ids = new IdMapper<>(16);
		private int lastId;

		private SimplePalette() {
		}

		public int idFor(BlockState p_189954_1_) {
			int i = this.ids.getId(p_189954_1_);
			if (i == -1) {
				i = this.lastId++;
				this.ids.addMapping(p_189954_1_, i);
			}

			return i;
		}

		@Nullable
		public BlockState stateFor(int p_189955_1_) {
			BlockState blockstate = this.ids.byId(p_189955_1_);
			return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
		}

		public Iterator<BlockState> iterator() {
			return this.ids.iterator();
		}

		public void addMapping(BlockState p_189956_1_, int p_189956_2_) {
			this.ids.addMapping(p_189956_1_, p_189956_2_);
		}
	}

	public static class BlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		public final CompoundTag nbt;

		public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
			this.pos = pos;
			this.state = state;
			this.nbt = nbt;
		}

		public String toString() {
			return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
		}
	}

	public static class EntityInfo {
		public final Vec3 pos;
		public final BlockPos blockPos;
		public final CompoundTag nbt;

		public EntityInfo(Vec3 vec3d, BlockPos blockPos, CompoundTag nbt) {
			this.pos = vec3d;
			this.blockPos = blockPos;
			this.nbt = nbt;
		}
	}

	public static final class Palette {
		private final List<GottschTemplate.BlockInfo> blocks;
		private final Map<Block, List<GottschTemplate.BlockInfo>> cache = Maps.newHashMap();

		private Palette(List<GottschTemplate.BlockInfo> blockInfos) {
			this.blocks = blockInfos;
		}

		public List<GottschTemplate.BlockInfo> blocks() {
			return this.blocks;
		}

		public List<GottschTemplate.BlockInfo> blocks(Block block) {
			return this.cache.computeIfAbsent(block, (p_237160_1_) -> {
				return this.blocks.stream().filter((predicate) -> {
					return predicate.state.is(p_237160_1_);
				}).collect(Collectors.toList());
			});
		}
	}

	/**
	 * Wrapper for transform(BlockPos, Mirror, Rotation, BlockPos)
	 * 
	 * @param placementIn
	 * @param coords
	 * @return
	 */
	public static ICoords transform(PlacementSettings placement, ICoords coords) {
		return new Coords(transform(coords.toPos(), placement.getMirror(), placement.getRotation(), placement.getRotationPivot()));		
	}

	/**
	 * 
	 * @param random
	 * @param findBlock
	 * @return
	 */
	public ICoords findCoords(Random random, Block findBlock) {
		ICoords coords = null; // TODO should this be an empty object or Coords.EMPTY_COORDS
		List<BlockInfoContext> contextList = (List<BlockInfoContext>) getTagBlockMap().get(findBlock);
		List<ICoords> list = contextList.stream().map(c -> c.getCoords()).collect(Collectors.toList());
		if (list.isEmpty())
			return new Coords(0, 0, 0);
		if (list.size() == 1)
			coords = list.get(0);
		else
			coords = list.get(random.nextInt(list.size()));
		return coords;
	}
	
	/**
	 * 
	 * @param random
	 * @param findBlock
	 * @return
	 */
	public ICoords findCoords(RandomSource random, Block findBlock) {
		ICoords coords = null; // TODO should this be an empty object or Coords.EMPTY_COORDS
		List<BlockInfoContext> contextList = (List<BlockInfoContext>) getTagBlockMap().get(findBlock);
		List<ICoords> list = contextList.stream().map(c -> c.getCoords()).collect(Collectors.toList());
		if (list.isEmpty())
			return new Coords(0, 0, 0);
		if (list.size() == 1)
			coords = list.get(0);
		else
			coords = list.get(random.nextInt(list.size()));
		return coords;
	}

	/**
	 * 
	 * @return
	 */
	public Multimap<Block, BlockInfoContext> getTagBlockMap() {
		return tagBlockMap;
	}
}
