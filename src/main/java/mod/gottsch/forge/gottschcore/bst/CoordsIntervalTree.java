package mod.gottsch.forge.gottschcore.bst;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import mod.gottsch.forge.gottschcore.GottschCore;
import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 3-D coordinate interval tree backed by a binary search tree.
 * <p>
 * v2.6: Dimension-aware. Each {@link CoordsInterval} carries a {@code dimension}
 * field. {@link #checkOverlap} and {@link #checkOverlapNoBorder} now perform a
 * dimension equality check <em>before</em> the XYZ axis checks so that intervals
 * from different dimensions are never returned as results.
 * A {@code null} dimension on either the stored interval or the query interval is
 * normalised to {@code "minecraft:overworld"} via
 * {@link CoordsInterval#effectiveDimension(String)}.
 * </p>
 * <p>
 * Also contains the inner {@link NBTSerializer} class (previously a separate file
 * {@code CoordsIntervalTreeNBTSerializer}). The serializer persists and restores the
 * {@code dimension} field and corrects a pre-existing bug where the RIGHT child was
 * incorrectly wired to the LEFT slot during deserialization.
 * </p>
 *
 * @author Mark Gottschling on September 20, 2022
 */
public class CoordsIntervalTree<D> implements IIntervalTree<D> {

	private IInterval<D> root;

	// =========================================================================
	// IIntervalTree<D> — public API
	// =========================================================================

	@Override
	public void clear() {
		setRoot(null);
	}

	@Override
	public synchronized IInterval<D> insert(IInterval<D> interval) {
		root = insert(root, interval);
		return root;
	}

	@Override
	public synchronized IInterval<D> delete(IInterval<D> target) {
		root = delete(root, target);
		return root;
	}

	/** Convenience overload — defaults to findFast=true, includeBorder=true. */
	public List<IInterval<D>> getOverlapping(IInterval<D> interval, IInterval<D> testInterval) {
		return getOverlapping(interval, testInterval, true, true);
	}

	@Override
	public synchronized List<IInterval<D>> getOverlapping(IInterval<D> interval,
														  IInterval<D> testInterval,
														  boolean findFast,
														  boolean includeBorder) {
		List<IInterval<D>> results = new ArrayList<>();
		if (includeBorder) {
			checkOverlap(interval, testInterval, results, findFast);
		} else {
			checkOverlapNoBorder(interval, testInterval, results, findFast);
		}
		return results;
	}

	@Override
	public synchronized IInterval<D> getRoot() { return root; }

	public synchronized void setRoot(IInterval<D> root) { this.root = root; }

	// =========================================================================
	// Private — insert / delete
	// =========================================================================

	private IInterval<D> insert(IInterval<D> interval, IInterval<D> newInterval) {
		if (interval == null) {
			return newInterval;
		}
		if (interval.getMax() == null || newInterval.getEnd() > interval.getMax()) {
			interval.setMax(newInterval.getEnd());
		}
		if (interval.getMin() == null || newInterval.getStart() < interval.getMin()) {
			interval.setMin(newInterval.getStart());
		}
		if (interval.compareTo(newInterval) <= 0) {
			if (interval.getRight() == null) {
				interval.setRight(newInterval);
			} else {
				insert(interval.getRight(), newInterval);
			}
		} else {
			if (interval.getLeft() == null) {
				interval.setLeft(newInterval);
			} else {
				insert(interval.getLeft(), newInterval);
			}
		}
		return interval;
	}

	private IInterval<D> delete(IInterval<D> interval, IInterval<D> target) {
		GottschCore.LOGGER.debug("delete interval -> {}, target -> {}", interval, target);
		if (interval == null) {
			return null;
		}
		if (interval.compareTo(target) < 0) {
			interval.setRight(delete(interval.getRight(), target));
		} else if (interval.compareTo(target) > 0) {
			interval.setLeft(delete(interval.getLeft(), target));
		} else {
			if (interval.getLeft() == null && interval.getRight() == null) {
				return null;
			} else if (interval.getLeft() == null) {
				return interval.getRight();
			} else if (interval.getRight() == null) {
				return interval.getLeft();
			} else {
				insert(interval.getLeft(), interval.getRight());
				return interval.getLeft();
			}
		}
		return interval;
	}

	// =========================================================================
	// Private — overlap checks (dimension-aware)
	// =========================================================================

	private boolean checkOverlap(IInterval<D> interval, IInterval<D> testInterval,
								 List<IInterval<D>> results, boolean findFast) {
		return checkOverlap((CoordsInterval<D>) interval, (CoordsInterval<D>) testInterval,
				results, findFast);
	}

	/**
	 * Recursive overlap check with border inclusion.
	 * <p>
	 * Check order (most-significant first for early exit):
	 * <ol>
	 *   <li>X min/max pruning (BST invariant — skips entire subtrees)</li>
	 *   <li><b>Dimension equality</b> — mismatched dimension = not a result</li>
	 *   <li>X axis overlap</li>
	 *   <li>Z axis overlap</li>
	 *   <li>Y axis overlap</li>
	 * </ol>
	 * </p>
	 */
	private boolean checkOverlap(CoordsInterval<D> interval, CoordsInterval<D> testInterval,
								 List<IInterval<D>> results, boolean findFast) {
		if (interval == null) return false;

		// BST pruning — skip this subtree entirely if X ranges cannot overlap
		if (testInterval.getStart() > interval.getMax() || testInterval.getEnd() < interval.getMin()) {
			return false;
		}

		// Dimension check — must match before XYZ checks (most significant filter)
		String intervalDim = CoordsInterval.effectiveDimension(interval.getDimension());
		String testDim     = CoordsInterval.effectiveDimension(testInterval.getDimension());
		boolean dimensionMatch = intervalDim.equals(testDim);

		if (dimensionMatch
				&& !((interval.getStart()  > testInterval.getEnd())   || (interval.getEnd()  < testInterval.getStart()))
				&& !((interval.getStartZ() > testInterval.getEndZ())  || (interval.getEndZ() < testInterval.getStartZ()))
				&& !((interval.getStartY() > testInterval.getEndY())  || (interval.getEndY() < testInterval.getStartY()))) {
			results.add(interval);
			if (findFast) return true;
		}

		// Recurse left
		if (interval.getLeft() != null && interval.getLeft().getMax() >= testInterval.getStart()) {
			if (checkOverlap(interval.getLeft(), testInterval, results, findFast) && findFast) return true;
		}

		// Recurse right
		return checkOverlap(interval.getRight(), testInterval, results, findFast) && findFast;
	}

	private boolean checkOverlapNoBorder(IInterval<D> interval, IInterval<D> testInterval,
										 List<IInterval<D>> results, boolean findFast) {
		return checkOverlapNoBorder((CoordsInterval<D>) interval, (CoordsInterval<D>) testInterval,
				results, findFast);
	}

	/**
	 * Recursive overlap check without border inclusion (strict interior overlap).
	 * Same dimension-first ordering as {@link #checkOverlap}.
	 */
	private boolean checkOverlapNoBorder(CoordsInterval<D> interval, CoordsInterval<D> testInterval,
										 List<IInterval<D>> results, boolean findFast) {
		if (interval == null) return false;

		// BST pruning
		if (testInterval.getStart() > interval.getMax() || testInterval.getEnd() < interval.getMin()) {
			return false;
		}

		// Dimension check (same logic as includeBorder variant)
		String intervalDim = CoordsInterval.effectiveDimension(interval.getDimension());
		String testDim     = CoordsInterval.effectiveDimension(testInterval.getDimension());
		boolean dimensionMatch = intervalDim.equals(testDim);

		if (dimensionMatch
				&& !((interval.getStart()  >= testInterval.getEnd())   || (interval.getEnd()  <= testInterval.getStart()))
				&& !((interval.getStartZ() >= testInterval.getEndZ())  || (interval.getEndZ() <= testInterval.getStartZ()))
				&& !((interval.getStartY() >= testInterval.getEndY())  || (interval.getEndY() <= testInterval.getStartY()))) {
			results.add(interval);
			if (findFast) return true;
		}

		// Recurse left
		if (interval.getLeft() != null && interval.getLeft().getMax() > testInterval.getStart()) {
			if (checkOverlapNoBorder(interval.getLeft(), testInterval, results, findFast) && findFast) return true;
		}

		// Recurse right
		return checkOverlapNoBorder(interval.getRight(), testInterval, results, findFast) && findFast;
	}

	// =========================================================================
	// Inner class — NBTSerializer
	// =========================================================================

	/**
	 * Serializes and deserializes a {@link CoordsIntervalTree} to/from NBT.
	 * <p>
	 * v2.6 changes:
	 * <ul>
	 *   <li>Persists {@code dimension} on each node under key {@code "dim"}.</li>
	 *   <li>On load, missing {@code "dim"} key defaults to {@code null} (which is
	 *       normalised to {@code "minecraft:overworld"} at query time — no migration
	 *       step required for pre-v2.6 worlds).</li>
	 *   <li>Fixes pre-existing bug: the RIGHT child deserialization branch previously
	 *       called {@code setLeft(right)} unconditionally instead of
	 *       {@code setRight(right)}.</li>
	 * </ul>
	 * </p>
	 *
	 * @param <D> payload type, must implement {@link INBTSerializable}
	 */
	public static class NBTSerializer<D extends INBTSerializable<Tag>> {

		private static final String START_KEY = "coords1";
		private static final String END_KEY   = "coords2";
		private static final String LEFT_KEY  = "left";
		private static final String RIGHT_KEY = "right";
		private static final String MIN_KEY   = "min";
		private static final String MAX_KEY   = "max";
		private static final String DATA_KEY  = "data";
		private static final String DIM_KEY   = "dim";

		private final Supplier<D> dataSupplier;

		public NBTSerializer(Supplier<D> dataSupplier) {
			this.dataSupplier = dataSupplier;
		}

		// ----- save ----------------------------------------------------------

		public void save(CoordsIntervalTree<D> tree, CompoundTag tag, String tagName) {
			if (tree.getRoot() == null) return;
			CompoundTag rootNbt = new CompoundTag();
			save((CoordsInterval<D>) tree.getRoot(), rootNbt);
			tag.put(tagName, rootNbt);
		}

		private void save(CoordsInterval<D> interval, CompoundTag tag) {
			CompoundTag c1Nbt = new CompoundTag();
			CompoundTag c2Nbt = new CompoundTag();
			interval.getCoords1().save(c1Nbt);
			interval.getCoords2().save(c2Nbt);
			tag.put(START_KEY, c1Nbt);
			tag.put(END_KEY,   c2Nbt);
			tag.putInt(MIN_KEY, interval.getMin());
			tag.putInt(MAX_KEY, interval.getMax());

			// Persist dimension (may be null for legacy entries — skip key rather
			// than writing "null"; absence is equivalent to DEFAULT_DIMENSION on load)
			if (interval.getDimension() != null) {
				tag.putString(DIM_KEY, interval.getDimension());
			}

			if (interval.getData() != null) {
				tag.put(DATA_KEY, interval.getData().serializeNBT());
			}
			if (interval.getLeft() != null) {
				CompoundTag leftNbt = new CompoundTag();
				save((CoordsInterval<D>) interval.getLeft(), leftNbt);
				tag.put(LEFT_KEY, leftNbt);
			}
			if (interval.getRight() != null) {
				CompoundTag rightNbt = new CompoundTag();
				save((CoordsInterval<D>) interval.getRight(), rightNbt);
				tag.put(RIGHT_KEY, rightNbt);
			}
		}

		// ----- load ----------------------------------------------------------

		public synchronized CoordsIntervalTree<D> load(CompoundTag tag, String tagName) {
			CoordsIntervalTree<D> tree = new CoordsIntervalTree<>();
			CompoundTag rootNbt = tag.getCompound(tagName);
			CoordsInterval<D> root = load(rootNbt);
			if (root != null && !root.isEmpty()) {
				tree.setRoot(root);
			}
			return tree;
		}

		private synchronized CoordsInterval<D> load(CompoundTag tag) {
			CoordsInterval<D> interval = new CoordsInterval<>();

			ICoords c1 = CoordsInterval.EMPTY.getCoords1();
			ICoords c2 = CoordsInterval.EMPTY.getCoords2();
			if (tag.contains(START_KEY)) {
				c1 = Coords.EMPTY.load(tag.getCompound(START_KEY));
			}
			if (tag.contains(END_KEY)) {
				c2 = Coords.EMPTY.load(tag.getCompound(END_KEY));
			}
			interval.setCoords1(c1);
			interval.setCoords2(c2);

			if (tag.contains(MIN_KEY)) interval.setMin(tag.getInt(MIN_KEY));
			if (tag.contains(MAX_KEY)) interval.setMax(tag.getInt(MAX_KEY));

			// Dimension: absent key → null → normalised to DEFAULT_DIMENSION at query time
			if (tag.contains(DIM_KEY)) {
				interval.setDimension(tag.getString(DIM_KEY));
			}

			if (tag.contains(DATA_KEY) && dataSupplier != null) {
				D data = dataSupplier.get();
				data.deserializeNBT(tag);
				interval.setData(data);
			}

			if (tag.contains(LEFT_KEY)) {
				CoordsInterval<D> left = load((CompoundTag) tag.get(LEFT_KEY));
				if (!left.isEmpty()) {
					interval.setLeft(left);
				}
			}
			if (tag.contains(RIGHT_KEY)) {
				CoordsInterval<D> right = load((CompoundTag) tag.get(RIGHT_KEY));
				if (!right.isEmpty()) {
					// BUG FIX (pre-v2.6): was incorrectly calling setLeft(right)
					interval.setRight(right);
				}
			}
			return interval;
		}

		public Supplier<D> getDataSupplier() { return dataSupplier; }
	}
}