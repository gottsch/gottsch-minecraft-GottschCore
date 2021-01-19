package com.someguyssoftware.gottschcore.world.gen.structure;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.someguyssoftware.gottschcore.loot.conditions.BlockStateProperty;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;

public class GottschTemplate2 extends Template {
	/** blocks in the structure */
	private final List<List<GottschTemplate2.BlockInfo>> blocks = Lists.newArrayList();
	/** entities in the structure */
	private final List<GottschTemplate2.EntityInfo> entities = Lists.newArrayList();
	/** size of the structure */
	private BlockPos size = BlockPos.ZERO;
	private String author = "?";

	/*
	 * A map of all the specials within the template.
	 */
	private final Multimap<Block, BlockContext> tagBlockMap = ArrayListMultimap.create();

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

	public void setAuthor(String authorIn) {
		this.author = authorIn;
	}

	public String getAuthor() {
		return this.author;
	}

	/**
	 * takes blocks from the world and puts the data from them into this template
	 */
	public void takeBlocksFromWorld(World worldIn, BlockPos startPos, BlockPos size, boolean takeEntities, @Nullable Block toIgnore) {
		if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
			BlockPos blockpos = startPos.add(size).add(-1, -1, -1);
			List<GottschTemplate2.BlockInfo> list = Lists.newArrayList();
			List<GottschTemplate2.BlockInfo> list1 = Lists.newArrayList();
			List<GottschTemplate2.BlockInfo> list2 = Lists.newArrayList();
			BlockPos blockpos1 = new BlockPos(Math.min(startPos.getX(), blockpos.getX()), Math.min(startPos.getY(), blockpos.getY()), Math.min(startPos.getZ(), blockpos.getZ()));
			BlockPos blockpos2 = new BlockPos(Math.max(startPos.getX(), blockpos.getX()), Math.max(startPos.getY(), blockpos.getY()), Math.max(startPos.getZ(), blockpos.getZ()));
			this.size = size;

			for(BlockPos blockpos3 : BlockPos.getAllInBoxMutable(blockpos1, blockpos2)) {
				BlockPos blockpos4 = blockpos3.subtract(blockpos1);
				BlockState blockstate = worldIn.getBlockState(blockpos3);
				if (toIgnore == null || toIgnore != blockstate.getBlock()) {
					TileEntity tileentity = worldIn.getTileEntity(blockpos3);
					if (tileentity != null) {
						CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
						compoundnbt.remove("x");
						compoundnbt.remove("y");
						compoundnbt.remove("z");
						list1.add(new GottschTemplate2.BlockInfo(blockpos4, blockstate, compoundnbt));
					} else if (!blockstate.isOpaqueCube(worldIn, blockpos3) && !blockstate.isCollisionShapeOpaque(worldIn, blockpos3)) {
						list2.add(new GottschTemplate2.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
					} else {
						list.add(new GottschTemplate2.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
					}
				}
			}

			List<GottschTemplate2.BlockInfo> list3 = Lists.newArrayList();
			list3.addAll(list);
			list3.addAll(list1);
			list3.addAll(list2);
			this.blocks.clear();
			this.blocks.add(list3);
			if (takeEntities) {
				this.takeEntitiesFromWorld(worldIn, blockpos1, blockpos2.add(1, 1, 1));
			} else {
				this.entities.clear();
			}

		}
	}

	/**
	 * takes blocks from the world and puts the data them into this template
	 */
	private void takeEntitiesFromWorld(World worldIn, BlockPos startPos, BlockPos endPos) {
		List<Entity> list = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(startPos, endPos), (p_201048_0_) -> {
			return !(p_201048_0_ instanceof PlayerEntity);
		});
		this.entities.clear();

		for(Entity entity : list) {
			Vec3d vec3d = new Vec3d(entity.getPosX() - (double)startPos.getX(), entity.getPosY() - (double)startPos.getY(), entity.getPosZ() - (double)startPos.getZ());
			CompoundNBT compoundnbt = new CompoundNBT();
			entity.writeUnlessPassenger(compoundnbt);
			BlockPos blockpos;
			if (entity instanceof PaintingEntity) {
				blockpos = ((PaintingEntity)entity).getHangingPosition().subtract(startPos);
			} else {
				blockpos = new BlockPos(vec3d);
			}

			this.entities.add(new GottschTemplate2.EntityInfo(vec3d, blockpos, compoundnbt));
		}

	}

	public BlockPos calculateConnectedPos(PlacementSettings placementIn, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
		BlockPos blockpos = transformedBlockPos(placementIn, p_186262_2_);
		BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
		return blockpos.subtract(blockpos1);
	}

	public static BlockPos transformedBlockPos(PlacementSettings placementIn, BlockPos pos) {
		return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset());
	}

	// FORGE: Add overload accepting Vec3d
	public static Vec3d transformedVec3d(PlacementSettings placementIn, Vec3d pos) {
		return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset());
	}

	/**
	 * 
	 * @param pos
	 * @param placement
	 * @param block
	 * @return
	 */
	public List<GottschTemplate2.BlockInfo> addBlocksToList(BlockPos pos, PlacementSettings placement, Block block) {
		return this.addBlocksToList(pos, placement, block, true);
	}

	/**
	 * 
	 * @param pos
	 * @param placement
	 * @param block
	 * @param transformFlag
	 * @return
	 */
	public List<GottschTemplate2.BlockInfo> addBlocksToList(BlockPos pos, PlacementSettings placement, Block block, boolean transformFlag) {
		List<GottschTemplate2.BlockInfo> blockList = Lists.newArrayList();
		MutableBoundingBox boundingBox = placement.getBoundingBox();

		for(GottschTemplate2.BlockInfo blockInfo : placement.func_227459_a_(this.blocks, pos)) {
			BlockPos blockPos = transformFlag ? transformedBlockPos(placement, blockInfo.pos).add(pos) : blockInfo.pos;
			if (boundingBox == null || boundingBox.isVecInside(blockPos)) {
				BlockState blockState = blockInfo.state;
				if (blockState.getBlock() == block) {
					blockList.add(new GottschTemplate2.BlockInfo(blockPos, blockState.rotate(placement.getRotation()), blockInfo.nbt));
				}
			}
		}
		return blockList;
	}

	
	// TODO this is the main method to insert code changes to.
	/**
	 * Non-Decay version.
	 * Adds blocks and entities from this structure to the given world.
	 */
	public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, final Block NULL_BLOCK, Map<BlockState, BlockState> replacementBlocks, int flags) {
		if (this.blocks.isEmpty()) {
			return false;
		} else {
			List<GottschTemplate2.BlockInfo> list = placementIn.func_227459_a_(this.blocks, pos);
			if ((!list.isEmpty() || !placementIn.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
				MutableBoundingBox boundingBox = placementIn.getBoundingBox();
				List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementIn.func_204763_l() ? list.size() : 0);
				List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int i1 = Integer.MIN_VALUE;
				int j1 = Integer.MIN_VALUE;

				for(GottschTemplate2.BlockInfo blockInfo : processBlockInfos(this, worldIn, pos, placementIn, list)) {
					BlockPos blockPos = blockInfo.pos;
					if (boundingBox == null || boundingBox.isVecInside(blockPos)) {
						IFluidState fluidState = placementIn.func_204763_l() ? worldIn.getFluidState(blockPos) : null;
                        BlockState processedBlock = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                        /////////////////////////////// GottschCore ///////////////////////////////////
                        //// replace block with null block if it is a marker block
                        if (this.tagBlockMap.containsKey(processedBlock)) {
                            processedBlock = NULL_BLOCK;
                        }
                        /////////////////////////// End of GottschCore ///////////////////////////////



						if (blockInfo.nbt != null) {
							TileEntity tileentity = worldIn.getTileEntity(blockPos);
							IClearable.clearObj(tileentity);
							worldIn.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), 20);
                        }
                        
                        /////////////////////////////// GottschCore ///////////////////////////////////
                        //// 1. Save the block context
                        //// 2. checkif the block should be deferred
                        /////////////////////////// End of GottschCore ///////////////////////////////
                        BlockInfoContext blockInfoContext = new BlockInfoContext(processedBlockInfo, new Coords(pos), blockState);

						if (worldIn.setBlockState(blockPos, processedBlock, flags)) {
							i = Math.min(i, blockPos.getX());
							j = Math.min(j, blockPos.getY());
							k = Math.min(k, blockPos.getZ());
							l = Math.max(l, blockPos.getX());
							i1 = Math.max(i1, blockPos.getY());
							j1 = Math.max(j1, blockPos.getZ());
							list2.add(Pair.of(blockPos, blockInfo.nbt));
							if (blockInfo.nbt != null) {
								TileEntity tileEntity = worldIn.getTileEntity(blockPos);
								if (tileEntity != null) {
									blockInfo.nbt.putInt("x", blockPos.getX());
									blockInfo.nbt.putInt("y", blockPos.getY());
									blockInfo.nbt.putInt("z", blockPos.getZ());
									tileEntity.read(blockInfo.nbt);
									tileEntity.mirror(placementIn.getMirror());
									tileEntity.rotate(placementIn.getRotation());
								}
							}

							if (fluidState != null && processedBlock.getBlock() instanceof ILiquidContainer) {
								((ILiquidContainer)processedBlock.getBlock()).receiveFluid(worldIn, blockPos, processedBlock, fluidState);
								if (!fluidState.isSource()) {
									list1.add(blockPos);
								}
							}
						}
					}
				}

				boolean flag = true;
				Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while(flag && !list1.isEmpty()) {
					flag = false;
					Iterator<BlockPos> iterator = list1.iterator();

					while(iterator.hasNext()) {
						BlockPos blockpos2 = iterator.next();
						BlockPos blockpos3 = blockpos2;
						IFluidState ifluidstate2 = worldIn.getFluidState(blockpos2);

						for(int k1 = 0; k1 < adirection.length && !ifluidstate2.isSource(); ++k1) {
							BlockPos blockpos1 = blockpos3.offset(adirection[k1]);
							IFluidState ifluidstate1 = worldIn.getFluidState(blockpos1);
							if (ifluidstate1.getActualHeight(worldIn, blockpos1) > ifluidstate2.getActualHeight(worldIn, blockpos3) || ifluidstate1.isSource() && !ifluidstate2.isSource()) {
								ifluidstate2 = ifluidstate1;
								blockpos3 = blockpos1;
							}
						}

						if (ifluidstate2.isSource()) {
							BlockState blockstate2 = worldIn.getBlockState(blockpos2);
							Block block = blockstate2.getBlock();
							if (block instanceof ILiquidContainer) {
								((ILiquidContainer)block).receiveFluid(worldIn, blockpos2, blockstate2, ifluidstate2);
								flag = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l) {
					if (!placementIn.func_215218_i()) {
						VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
						int l1 = i;
						int i2 = j;
						int j2 = k;

						for(Pair<BlockPos, CompoundNBT> pair1 : list2) {
							BlockPos blockpos5 = pair1.getFirst();
							voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
						}

						func_222857_a(worldIn, flags, voxelshapepart, l1, i2, j2);
					}

					for(Pair<BlockPos, CompoundNBT> pair : list2) {
						BlockPos blockpos4 = pair.getFirst();
						if (!placementIn.func_215218_i()) {
							BlockState blockstate1 = worldIn.getBlockState(blockpos4);
							BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, worldIn, blockpos4);
							if (blockstate1 != blockstate3) {
                                ///// TODO this is where the world is updated
								worldIn.setBlockState(blockpos4, blockstate3, flags & -2 | 16);
							}

							worldIn.notifyNeighbors(blockpos4, blockstate3.getBlock());
						}

						if (pair.getSecond() != null) {
							TileEntity tileentity2 = worldIn.getTileEntity(blockpos4);
							if (tileentity2 != null) {
								tileentity2.markDirty();
							}
						}
					}
				}

				if (!placementIn.getIgnoreEntities()) {
					this.addEntitiesToWorld(worldIn, pos, placementIn, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset(), placementIn.getBoundingBox());
				}

				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Decay version
	 * @param worldIn
	 * @param pos
	 * @param placementIn
	 * @param decayProcessor
	 * @param NULL_BLOCK
	 * @param replacementBlocks
	 * @param flags
	 * @return
	 */
	public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, 
			@Nullable IDecayProcessor decayProcessor, final Block NULL_BLOCK, Map<BlockState, BlockState> replacementBlocks, int flags) {

		return false;
	}
	
	/**
	 * Wrapper for transformedBlockPos()
	 * 
	 * @param placementIn
	 * @param coords
	 * @return
	 */
	public static ICoords transformedCoords(PlacementSettings placement, ICoords coords) {
		return new Coords(transformedBlockPos(placement, coords.toPos()));
	}
	
	public static void func_222857_a(IWorld worldIn, int p_222857_1_, VoxelShapePart voxelShapePartIn, int x, int y, int z) {
		voxelShapePartIn.forEachFace((p_222856_5_, p_222856_6_, p_222856_7_, p_222856_8_) -> {
			BlockPos blockPos = new BlockPos(x + p_222856_6_, y + p_222856_7_, z + p_222856_8_);
			BlockPos blockPos1 = blockPos.offset(p_222856_5_);
			BlockState blockState = worldIn.getBlockState(blockPos);
			BlockState blockState1 = worldIn.getBlockState(blockPos1);
			BlockState blockState2 = blockState.updatePostPlacement(p_222856_5_, blockState1, worldIn, blockPos, blockPos1);
			if (blockState != blockState2) {
				worldIn.setBlockState(blockPos, blockState2, p_222857_1_ & -2 | 16);
			}

			BlockState blockstate3 = blockState1.updatePostPlacement(p_222856_5_.getOpposite(), blockState2, worldIn, blockPos1, blockPos);
			if (blockState1 != blockstate3) {
				worldIn.setBlockState(blockPos1, blockstate3, p_222857_1_ & -2 | 16);
			}

		});
	}
	
	/**
	 * 
	 * @param random
	 * @param findBlock
	 * @return
	 */
	public ICoords findCoords(Random random, Block findBlock) {
		ICoords coords = null; // TODO should this be an empty object or Coords.EMPTY_COORDS
		List<BlockContext> contextList = (List<BlockContext>) getTagBlockMap().get(findBlock);
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
	public Multimap<Block, BlockContext> getTagBlockMap() {
		return tagBlockMap;
	}
	
	@Deprecated // FORGE: Add template parameter
	public static List<GottschTemplate2.BlockInfo> processBlockInfos(IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<GottschTemplate2.BlockInfo> blockInfos) {
		return processBlockInfos(null, worldIn, offsetPos, placementSettingsIn, blockInfos);
	}

	public static List<GottschTemplate2.BlockInfo> processBlockInfos(@Nullable GottschTemplate2 template, IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<GottschTemplate2.BlockInfo> blockInfos) {
		List<GottschTemplate2.BlockInfo> list = Lists.newArrayList();

		for(GottschTemplate2.BlockInfo template$blockinfo : blockInfos) {
			BlockPos blockpos = transformedBlockPos(placementSettingsIn, template$blockinfo.pos).add(offsetPos);
			GottschTemplate2.BlockInfo blockInfo = new GottschTemplate2.BlockInfo(blockpos, template$blockinfo.state, template$blockinfo.nbt);

			for(Iterator<StructureProcessor> iterator = placementSettingsIn.getProcessors().iterator(); blockInfo != null && iterator.hasNext(); blockInfo = iterator.next().process(worldIn, offsetPos, template$blockinfo, blockInfo, placementSettingsIn, template)) {
				;
			}

			if (blockInfo != null) {
				list.add(blockInfo);
			}
		}

		return list;
	}

	// FORGE: Add processing for entities
	public static List<GottschTemplate2.EntityInfo> processEntityInfos(@Nullable GottschTemplate2 template, IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<GottschTemplate2.EntityInfo> blockInfos) {
		List<GottschTemplate2.EntityInfo> list = Lists.newArrayList();

		for(GottschTemplate2.EntityInfo entityInfo : blockInfos) {
			Vec3d pos = transformedVec3d(placementSettingsIn, entityInfo.pos).add(new Vec3d(offsetPos));
			BlockPos blockpos = transformedBlockPos(placementSettingsIn, entityInfo.blockPos).add(offsetPos);
			GottschTemplate2.EntityInfo info = new GottschTemplate2.EntityInfo(pos, blockpos, entityInfo.nbt);

			for (StructureProcessor proc : placementSettingsIn.getProcessors()) {
				info = proc.processEntity(worldIn, offsetPos, entityInfo, info, placementSettingsIn, template);
				if (info == null)
					break;
			}

			if (info != null)
				list.add(info);
		}

		return list;
	}

	@Deprecated // FORGE: Add PlacementSettings parameter (below) to pass to entity processors
	private void addEntitiesToWorld(IWorld worldIn, BlockPos offsetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset, @Nullable MutableBoundingBox boundsIn) {
		addEntitiesToWorld(worldIn, offsetPos, new PlacementSettings().setMirror(mirrorIn).setRotation(rotationIn).setCenterOffset(centerOffset).setBoundingBox(boundsIn), mirrorIn, rotationIn, offsetPos, boundsIn);
	}

	private void addEntitiesToWorld(IWorld worldIn, BlockPos offsetPos, PlacementSettings placementIn, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset, @Nullable MutableBoundingBox boundsIn) {
		for(GottschTemplate2.EntityInfo template$entityinfo : processEntityInfos(this, worldIn, offsetPos, placementIn, this.entities)) {
			BlockPos blockpos = getTransformedPos(template$entityinfo.blockPos, mirrorIn, rotationIn, centerOffset).add(offsetPos);
			blockpos = template$entityinfo.blockPos; // FORGE: Position will have already been transformed by processEntityInfos
			if (boundsIn == null || boundsIn.isVecInside(blockpos)) {
				CompoundNBT compoundnbt = template$entityinfo.nbt;
				Vec3d vec3d = getTransformedPos(template$entityinfo.pos, mirrorIn, rotationIn, centerOffset);
				vec3d = vec3d.add((double)offsetPos.getX(), (double)offsetPos.getY(), (double)offsetPos.getZ());
				Vec3d vec3d1 = template$entityinfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
				ListNBT listnbt = new ListNBT();
				listnbt.add(DoubleNBT.valueOf(vec3d1.x));
				listnbt.add(DoubleNBT.valueOf(vec3d1.y));
				listnbt.add(DoubleNBT.valueOf(vec3d1.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUIDMost");
				compoundnbt.remove("UUIDLeast");
				loadEntity(worldIn, compoundnbt).ifPresent((p_215383_4_) -> {
					float f = p_215383_4_.getMirroredYaw(mirrorIn);
					f = f + (p_215383_4_.rotationYaw - p_215383_4_.getRotatedYaw(rotationIn));
					p_215383_4_.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, f, p_215383_4_.rotationPitch);
					worldIn.addEntity(p_215383_4_);
				});
			}
		}

	}

	private static Optional<Entity> loadEntity(IWorld worldIn, CompoundNBT nbt) {
		try {
			return EntityType.loadEntityUnchecked(nbt, worldIn.getWorld());
		} catch (Exception var3) {
			return Optional.empty();
		}
	}

	public BlockPos transformedSize(Rotation rotationIn) {
		switch(rotationIn) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
		default:
			return this.size;
		}
	}

	public static BlockPos getTransformedPos(BlockPos targetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos offset) {
		int i = targetPos.getX();
		int j = targetPos.getY();
		int k = targetPos.getZ();
		boolean flag = true;
		switch(mirrorIn) {
		case LEFT_RIGHT:
			k = -k;
			break;
		case FRONT_BACK:
			i = -i;
			break;
		default:
			flag = false;
		}

		int l = offset.getX();
		int i1 = offset.getZ();
		switch(rotationIn) {
		case COUNTERCLOCKWISE_90:
			return new BlockPos(l - i1 + k, j, l + i1 - i);
		case CLOCKWISE_90:
			return new BlockPos(l + i1 - k, j, i1 - l + i);
		case CLOCKWISE_180:
			return new BlockPos(l + l - i, j, i1 + i1 - k);
		default:
			return flag ? new BlockPos(i, j, k) : targetPos;
		}
	}

	private static Vec3d getTransformedPos(Vec3d target, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset) {
		double d0 = target.x;
		double d1 = target.y;
		double d2 = target.z;
		boolean flag = true;
		switch(mirrorIn) {
		case LEFT_RIGHT:
			d2 = 1.0D - d2;
			break;
		case FRONT_BACK:
			d0 = 1.0D - d0;
			break;
		default:
			flag = false;
		}

		int i = centerOffset.getX();
		int j = centerOffset.getZ();
		switch(rotationIn) {
		case COUNTERCLOCKWISE_90:
			return new Vec3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
		case CLOCKWISE_90:
			return new Vec3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
		case CLOCKWISE_180:
			return new Vec3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
		default:
			return flag ? new Vec3d(d0, d1, d2) : target;
		}
	}

	public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
		return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
	}

	public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
		--p_191157_3_;
		--p_191157_4_;
		int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
		int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
		BlockPos blockpos = p_191157_0_;
		switch(p_191157_2_) {
		case COUNTERCLOCKWISE_90:
			blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
			break;
		case CLOCKWISE_90:
			blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
			break;
		case CLOCKWISE_180:
			blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
			break;
		case NONE:
			blockpos = p_191157_0_.add(i, 0, j);
		}

		return blockpos;
	}

	public MutableBoundingBox getMutableBoundingBox(PlacementSettings p_215388_1_, BlockPos p_215388_2_) {
		Rotation rotation = p_215388_1_.getRotation();
		BlockPos blockpos = p_215388_1_.getCenterOffset();
		BlockPos blockpos1 = this.transformedSize(rotation);
		Mirror mirror = p_215388_1_.getMirror();
		int i = blockpos.getX();
		int j = blockpos.getZ();
		int k = blockpos1.getX() - 1;
		int l = blockpos1.getY() - 1;
		int i1 = blockpos1.getZ() - 1;
		MutableBoundingBox mutableboundingbox = new MutableBoundingBox(0, 0, 0, 0, 0, 0);
		switch(rotation) {
		case COUNTERCLOCKWISE_90:
			mutableboundingbox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
			break;
		case CLOCKWISE_90:
			mutableboundingbox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
			break;
		case CLOCKWISE_180:
			mutableboundingbox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
			break;
		case NONE:
			mutableboundingbox = new MutableBoundingBox(0, 0, 0, k, l, i1);
		}

		switch(mirror) {
		case LEFT_RIGHT:
			this.func_215385_a(rotation, i1, k, mutableboundingbox, Direction.NORTH, Direction.SOUTH);
			break;
		case FRONT_BACK:
			this.func_215385_a(rotation, k, i1, mutableboundingbox, Direction.WEST, Direction.EAST);
		case NONE:
		}

		mutableboundingbox.offset(p_215388_2_.getX(), p_215388_2_.getY(), p_215388_2_.getZ());
		return mutableboundingbox;
	}

	private void func_215385_a(Rotation rotationIn, int offsetFront, int p_215385_3_, MutableBoundingBox p_215385_4_, Direction p_215385_5_, Direction p_215385_6_) {
		BlockPos blockpos = BlockPos.ZERO;
		if (rotationIn != Rotation.CLOCKWISE_90 && rotationIn != Rotation.COUNTERCLOCKWISE_90) {
			if (rotationIn == Rotation.CLOCKWISE_180) {
				blockpos = blockpos.offset(p_215385_6_, offsetFront);
			} else {
				blockpos = blockpos.offset(p_215385_5_, offsetFront);
			}
		} else {
			blockpos = blockpos.offset(rotationIn.rotate(p_215385_5_), p_215385_3_);
		}

		p_215385_4_.offset(blockpos.getX(), 0, blockpos.getZ());
	}

	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		if (this.blocks.isEmpty()) {
			nbt.put("blocks", new ListNBT());
			nbt.put("palette", new ListNBT());
		} else {
			List<GottschTemplate2.BasicPalette> list = Lists.newArrayList();
			GottschTemplate2.BasicPalette template$basicpalette = new GottschTemplate2.BasicPalette();
			list.add(template$basicpalette);

			for(int i = 1; i < this.blocks.size(); ++i) {
				list.add(new GottschTemplate2.BasicPalette());
			}

			ListNBT listnbt1 = new ListNBT();
			List<GottschTemplate2.BlockInfo> list1 = this.blocks.get(0);

			for(int j = 0; j < list1.size(); ++j) {
				GottschTemplate2.BlockInfo template$blockinfo = list1.get(j);
				CompoundNBT compoundnbt = new CompoundNBT();
				compoundnbt.put("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
				int k = template$basicpalette.idFor(template$blockinfo.state);
				compoundnbt.putInt("state", k);
				if (template$blockinfo.nbt != null) {
					compoundnbt.put("nbt", template$blockinfo.nbt);
				}

				listnbt1.add(compoundnbt);

				for(int l = 1; l < this.blocks.size(); ++l) {
					GottschTemplate2.BasicPalette template$basicpalette1 = list.get(l);
					template$basicpalette1.addMapping((this.blocks.get(l).get(j)).state, k);
				}
			}

			nbt.put("blocks", listnbt1);
			if (list.size() == 1) {
				ListNBT listnbt2 = new ListNBT();

				for(BlockState blockstate : template$basicpalette) {
					listnbt2.add(NBTUtil.writeBlockState(blockstate));
				}

				nbt.put("palette", listnbt2);
			} else {
				ListNBT listnbt3 = new ListNBT();

				for(GottschTemplate2.BasicPalette template$basicpalette2 : list) {
					ListNBT listnbt4 = new ListNBT();

					for(BlockState blockstate1 : template$basicpalette2) {
						listnbt4.add(NBTUtil.writeBlockState(blockstate1));
					}

					listnbt3.add(listnbt4);
				}

				nbt.put("palettes", listnbt3);
			}
		}

		ListNBT listnbt = new ListNBT();

		for(GottschTemplate2.EntityInfo template$entityinfo : this.entities) {
			CompoundNBT compoundnbt1 = new CompoundNBT();
			compoundnbt1.put("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
			compoundnbt1.put("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
			if (template$entityinfo.nbt != null) {
				compoundnbt1.put("nbt", template$entityinfo.nbt);
			}

			listnbt.add(compoundnbt1);
		}

		nbt.put("entities", listnbt);
		nbt.put("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
		nbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
		return nbt;
	}

	public void read(CompoundNBT compound) {
		this.blocks.clear();
		this.entities.clear();
		ListNBT listnbt = compound.getList("size", 3);
		this.size = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
		ListNBT listnbt1 = compound.getList("blocks", 10);
		if (compound.contains("palettes", 9)) {
			ListNBT listnbt2 = compound.getList("palettes", 9);

			for(int i = 0; i < listnbt2.size(); ++i) {
				this.readPalletesAndBlocks(listnbt2.getList(i), listnbt1);
			}
		} else {
			this.readPalletesAndBlocks(compound.getList("palette", 10), listnbt1);
		}

		ListNBT listnbt5 = compound.getList("entities", 10);

		for(int j = 0; j < listnbt5.size(); ++j) {
			CompoundNBT compoundnbt = listnbt5.getCompound(j);
			ListNBT listnbt3 = compoundnbt.getList("pos", 6);
			Vec3d vec3d = new Vec3d(listnbt3.getDouble(0), listnbt3.getDouble(1), listnbt3.getDouble(2));
			ListNBT listnbt4 = compoundnbt.getList("blockPos", 3);
			BlockPos blockpos = new BlockPos(listnbt4.getInt(0), listnbt4.getInt(1), listnbt4.getInt(2));
			if (compoundnbt.contains("nbt")) {
				CompoundNBT compoundnbt1 = compoundnbt.getCompound("nbt");
				this.entities.add(new GottschTemplate2.EntityInfo(vec3d, blockpos, compoundnbt1));
			}
		}

	}

	private void readPalletesAndBlocks(ListNBT palletesNBT, ListNBT blocksNBT) {
		GottschTemplate2.BasicPalette template$basicpalette = new GottschTemplate2.BasicPalette();
		List<GottschTemplate2.BlockInfo> list = Lists.newArrayList();

		for(int i = 0; i < palletesNBT.size(); ++i) {
			template$basicpalette.addMapping(NBTUtil.readBlockState(palletesNBT.getCompound(i)), i);
		}

		for(int j = 0; j < blocksNBT.size(); ++j) {
			CompoundNBT compoundnbt = blocksNBT.getCompound(j);
			ListNBT listnbt = compoundnbt.getList("pos", 3);
			BlockPos blockpos = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
			BlockState blockstate = template$basicpalette.stateFor(compoundnbt.getInt("state"));
			CompoundNBT compoundnbt1;
			if (compoundnbt.contains("nbt")) {
				compoundnbt1 = compoundnbt.getCompound("nbt");
			} else {
				compoundnbt1 = null;
			}

			list.add(new GottschTemplate2.BlockInfo(blockpos, blockstate, compoundnbt1));
		}

		list.sort(Comparator.comparingInt((p_215384_0_) -> {
			return p_215384_0_.pos.getY();
		}));
		this.blocks.add(list);
	}

	private ListNBT writeInts(int... values) {
		ListNBT listnbt = new ListNBT();

		for(int i : values) {
			listnbt.add(IntNBT.valueOf(i));
		}

		return listnbt;
	}

	private ListNBT writeDoubles(double... values) {
		ListNBT listnbt = new ListNBT();

		for(double d0 : values) {
			listnbt.add(DoubleNBT.valueOf(d0));
		}

		return listnbt;
	}

	static class BasicPalette implements Iterable<BlockState> {
		public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
		private final ObjectIntIdentityMap<BlockState> ids = new ObjectIntIdentityMap<>(16);
		private int lastId;

		private BasicPalette() {
		}

		public int idFor(BlockState state) {
			int i = this.ids.get(state);
			if (i == -1) {
				i = this.lastId++;
				this.ids.put(state, i);
			}

			return i;
		}

		@Nullable
		public BlockState stateFor(int id) {
			BlockState blockstate = this.ids.getByValue(id);
			return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
		}

		public Iterator<BlockState> iterator() {
			return this.ids.iterator();
		}

		public void addMapping(BlockState p_189956_1_, int p_189956_2_) {
			this.ids.put(p_189956_1_, p_189956_2_);
		}
	}

	public static class BlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		public final CompoundNBT nbt;

		public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundNBT nbt) {
			this.pos = pos;
			this.state = state;
			this.nbt = nbt;
		}

		public String toString() {
			return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
		}
	}

	public static class EntityInfo {
		public final Vec3d pos;
		public final BlockPos blockPos;
		public final CompoundNBT nbt;

		public EntityInfo(Vec3d vecIn, BlockPos posIn, CompoundNBT nbt) {
			this.pos = vecIn;
			this.blockPos = posIn;
			this.nbt = nbt;
		}
	}


}