package mod.gottsch.forge.gottschcore.bst;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;

/**
 * A 3D coordinate interval used as a node in {@link CoordsIntervalTree}.
 * <p>
 * v2.6: Added {@code dimension} field so the BST is dimension-aware.
 * Dimension is the primary sort key in {@link #compareTo} — intervals are
 * partitioned by dimension first, then by X, Z, Y coordinates.
 * A {@code null} dimension is treated as {@code "minecraft:overworld"} for
 * backward-compatibility with pre-v2.6 worlds whose NBT does not carry the field.
 * </p>
 *
 * @author Mark Gottschling on Sept 19, 2022
 */
public class CoordsInterval<D> implements IInterval<D> {
	public static final CoordsInterval<?> EMPTY = new CoordsInterval<>(new Coords(0, -999, 0), new Coords(0, -999, 0), null);

	/** Fallback dimension used when the stored dimension is {@code null}. */
	public static final String DEFAULT_DIMENSION = "minecraft:overworld";

	private ICoords coords1;
	private ICoords coords2;

	private Integer min;
	private Integer max;
	private IInterval<D> left;
	private IInterval<D> right;

	private D data;

	/**
	 * Dimension string in {@code "namespace:path"} format, e.g. {@code "minecraft:overworld"}.
	 * May be {@code null} for intervals loaded from pre-v2.6 NBT; treated as
	 * {@link #DEFAULT_DIMENSION} in all comparisons and queries.
	 */
	private String dimension;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CoordsInterval() { }

	public CoordsInterval(ICoords coords1, ICoords coords2) {
		this.coords1 = coords1;
		this.coords2 = coords2;
		this.min = Math.min(coords1.getX(), coords2.getX());
		this.max = Math.max(coords1.getX(), coords2.getX());
	}

	public CoordsInterval(ICoords coords1, ICoords coords2, D data) {
		this.coords1 = coords1;
		this.coords2 = coords2;
		this.min = Math.min(coords1.getX(), coords2.getX());
		this.max = Math.max(coords1.getX(), coords2.getX());
		this.data = data;
	}

	/**
	 * Constructs a dimension-aware interval.
	 *
	 * @param coords1   minimum bounding corner
	 * @param coords2   maximum bounding corner
	 * @param data      payload stored at this node
	 * @param dimension dimension resource-location string, e.g. {@code "minecraft:overworld"};
	 *                  may be {@code null} (treated as {@link #DEFAULT_DIMENSION})
	 */
	public CoordsInterval(ICoords coords1, ICoords coords2, D data, String dimension) {
		this(coords1, coords2, data);
		this.dimension = dimension;
	}

	public CoordsInterval(CoordsInterval<D> interval) {
		this.coords1   = interval.coords1;
		this.coords2   = interval.coords2;
		this.min       = interval.min;
		this.max       = interval.max;
		this.data      = interval.data;
		this.right     = interval.right;
		this.left      = interval.left;
		this.dimension = interval.dimension;
	}

	// -------------------------------------------------------------------------
	// Comparison — dimension is the primary sort key (first tiebreaker)
	// -------------------------------------------------------------------------

	/**
	 * Compares this interval to another for BST ordering.
	 * <p>
	 * Sort order: <b>dimension → X start → X end → Z start → Z end → Y start → Y end</b>.
	 * Dimension is first so the tree is partitioned by dimension at the top level,
	 * giving O(1) dimension pruning during traversal.
	 * A {@code null} dimension on either side is normalised to {@link #DEFAULT_DIMENSION}
	 * before the string comparison.
	 * </p>
	 */
	@Override
	public int compareTo(IInterval<D> intervalIn) {
		CoordsInterval<D> interval = (CoordsInterval<D>) intervalIn;

		// --- dimension (primary) ---
		String thisDim  = effectiveDimension(this.dimension);
		String otherDim = effectiveDimension(interval.dimension);
		int dimCmp = thisDim.compareTo(otherDim);
		if (dimCmp != 0) return dimCmp;

		// --- X start ---
		if (this.getStart() < interval.getStart()) return -1;
		if (this.getStart() > interval.getStart()) return 1;

		// --- X end ---
		if (this.getEnd() != interval.getEnd()) {
			return this.getEnd() < interval.getEnd() ? -1 : 1;
		}

		// --- Z start ---
		if (getStartZ() < interval.getStartZ()) return -1;
		if (getStartZ() > interval.getStartZ()) return 1;

		// --- Z end ---
		if (getEndZ() != interval.getEndZ()) {
			return this.getEndZ() < interval.getEndZ() ? -1 : 1;
		}

		// --- Y start ---
		if (getStartY() < interval.getStartY()) return -1;
		if (getStartY() > interval.getStartY()) return 1;

		// --- Y end ---
		if (getEndY() == interval.getEndY()) return 0;
		return this.getEndY() < interval.getEndY() ? -1 : 1;
	}

	/** Returns {@code dim} if non-null, otherwise {@link #DEFAULT_DIMENSION}. */
	public static String effectiveDimension(String dim) {
		return dim != null ? dim : DEFAULT_DIMENSION;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	public boolean isEmpty() {
		return this.coords1.equals(EMPTY.getCoords1())
				|| this.coords2.equals(EMPTY.getCoords2());
	}

	// -------------------------------------------------------------------------
	// IInterval<D> implementation
	// -------------------------------------------------------------------------

	@Override public int      getStart() { return coords1.getX(); }
	@Override public int      getEnd()   { return coords2.getX(); }
	@Override public Integer  getMin()   { return min; }
	@Override public void     setMin(Integer min) { this.min = min; }
	@Override public Integer  getMax()   { return max; }
	@Override public void     setMax(Integer max) { this.max = max; }
	@Override public IInterval<D> getLeft()  { return left; }
	@Override public void     setLeft(IInterval<D> left)   { this.left  = left; }
	@Override public IInterval<D> getRight() { return right; }
	@Override public void     setRight(IInterval<D> right) { this.right = right; }
	@Override public D        getData()  { return data; }
	@Override public void     setData(D data) { this.data = data; }

	public int getStartZ() { return coords1.getZ(); }
	public int getEndZ()   { return coords2.getZ(); }
	public int getStartY() { return coords1.getY(); }
	public int getEndY()   { return coords2.getY(); }

	public ICoords getCoords1() { return coords1; }
	public void    setCoords1(ICoords coords1) { this.coords1 = coords1; }
	public ICoords getCoords2() { return coords2; }
	public void    setCoords2(ICoords coords2) { this.coords2 = coords2; }

	public String getDimension() { return dimension; }
	public void   setDimension(String dimension) { this.dimension = dimension; }

	// -------------------------------------------------------------------------
	// equals / hashCode / toString
	// -------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords1   == null) ? 0 : coords1.hashCode());
		result = prime * result + ((coords2   == null) ? 0 : coords2.hashCode());
		result = prime * result + ((dimension == null) ? 0 : dimension.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CoordsInterval<?> other = (CoordsInterval<?>) obj;
		if (coords1 == null ? other.coords1 != null : !coords1.equals(other.coords1)) return false;
		if (coords2 == null ? other.coords2 != null : !coords2.equals(other.coords2)) return false;
		// dimension: null and DEFAULT_DIMENSION are equivalent
		return effectiveDimension(this.dimension).equals(effectiveDimension(other.dimension));
	}

	@Override
	public String toString() {
		return "CoordsInterval [coords1=" + coords1 + ", coords2=" + coords2
				+ ", dimension=" + effectiveDimension(dimension)
				+ ", min=" + min + ", max=" + max
				+ ", left=" + left + ", right=" + right + ", data=" + data + "]";
	}
}