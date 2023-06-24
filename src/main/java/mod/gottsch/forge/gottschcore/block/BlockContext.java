package mod.gottsch.forge.gottschcore.block;


import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public class BlockContext {
	private final ICoords coords;
	private final BlockState state;
	
	/**
	 * 
	 * @param world
	 * @param coords
	 */
	public BlockContext(CommonLevelAccessor level, ICoords coords) {
		this.coords = coords;
		this.state = level.getBlockState(coords.toPos());
	}
	
	public BlockContext(CommonLevelAccessor level, BlockPos pos) {
		this(level, new Coords(pos));
	}
	
	public BlockContext(ICoords coords, BlockState state) {
		this.coords = coords;
		this.state = state;
	}
	
	public boolean hasState() {
		if (state == null)
			return false;
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Block toBlock() {
		if (state != null) {
			return state.getBlock();
		}
		return null;
	}
	
	public static Block toBlock(final Level level, final ICoords coords) {
		BlockState blockState = level.getBlockState(coords.toPos());
		if (blockState != null)
			return blockState.getBlock();
		return null;
	}
	
	public boolean equalsBlock(Block block) {
		if (state.getBlock() == block)
			return true;
		return false;
	}
	
	public boolean equalsMaterial(Material material) {
		if (state.getMaterial() == material)
			return true;
		return false;
	}

	public boolean isAir() {
		return state.getMaterial() == Material.AIR;
	}

	/**
	 * Wrapper for Material.isReplaceable();
	 * 
	 * @return
	 */
	public boolean isReplaceable() {
		return state.getMaterial().isReplaceable();
	}

	/**
	 * Wrapper for Material.isSolid()
	 * 
	 * @return
	 */
	public boolean isSolid() {
		return state.getMaterial().isSolid();
	}
	
	public boolean isFluid() {
		return !state.getFluidState().isEmpty();
	}
	
	public boolean isBurning() {
		return state.isBurning((BlockGetter) null, this.coords.toPos());
	}
	
	public ICoords getCoords() {
		return coords;
	}

	public BlockState getState() {
		return state;
	}
}
