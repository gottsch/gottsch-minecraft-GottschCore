/**
 * 
 */
package com.someguyssoftware.gottschcore.spatial;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.util.Direction;

/**
 * 
 * @author Mark Gottschling on Feb 26, 2020
 *
 */
public enum Heading {
	// @formatter:off
	UP(Direction.UP), 
	DOWN(Direction.DOWN), 
	NORTH(Direction.NORTH), 
	SOUTH(Direction.SOUTH), 
	WEST(Direction.WEST),
	EAST(Direction.EAST);
	// @formatter:on

	private static final Map<Direction, Heading> mapByDirection = new HashMap<Direction, Heading>();
	
	/*
	 * wrapped minecraft class
	 */
	private Direction direction;

	// setup reverse lookup
	static {
		for (Heading x : EnumSet.allOf(Heading.class)) {
			mapByDirection.put(x.getDirection(), x);
		}
	}
	/**
	 * 
	 * @param direction
	 */
	Heading(Direction direction) {
		this.direction = direction;
	}

	/**
	 * 
	 * @return
	 */
	public Direction toDirection() {
		return direction;
	}
	
	public Heading byDirection(Direction direction) {
		return mapByDirection.get(direction);
	}

	/**
	 * 
	 * @return
	 */
	public int getIndex() {
		return direction.get3DDataValue();
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public static Heading getByIndex(int index) {
		return mapByDirection.get(Direction.from3DDataValue(index % values().length));
	}

	/**
	 * Rotate the current heading by rotation amount.
	 * 
	 * @param r
	 * @return
	 */
	public Heading rotateY(Rotate r) {
		switch (r) {
		case NO_ROTATE:
			return this;
		case ROTATE_90:
			switch (this) {
			case NORTH:
				return Heading.EAST;
			case EAST:
				return Heading.SOUTH;
			case SOUTH:
				return Heading.WEST;
			case WEST:
				return Heading.NORTH;
			default:
				return this;
			}
		case ROTATE_180:
			switch (this) {
			case NORTH:
				return Heading.SOUTH;
			case EAST:
				return Heading.WEST;
			case SOUTH:
				return Heading.NORTH;
			case WEST:
				return Heading.EAST;
			default:
				return this;
			}
		case ROTATE_270:
			switch (this) {
			case NORTH:
				return Heading.WEST;
			case EAST:
				return Heading.NORTH;
			case SOUTH:
				return Heading.EAST;
			case WEST:
				return Heading.SOUTH;
			default:
				return this;
			}
		default:
			return this;
		}
	}

	/**
	 * 
	 * @param toHeading
	 * @return
	 */
	public Rotate getRotation(Heading toHeading) {
		switch (toHeading) {// <-- destination
		case NORTH:
			switch (this) {// <-- source
			case NORTH:
				return Rotate.NO_ROTATE;
			case EAST:
				return Rotate.ROTATE_270;
			case SOUTH:
				return Rotate.ROTATE_180;
			case WEST:
				return Rotate.ROTATE_90;
			default:
				return Rotate.NO_ROTATE;
			}
		case EAST:
			switch (this) {
			case NORTH:
				return Rotate.ROTATE_90;
			case EAST:
				return Rotate.NO_ROTATE;
			case SOUTH:
				return Rotate.ROTATE_270;
			case WEST:
				return Rotate.ROTATE_180;
			default:
				return Rotate.NO_ROTATE;
			}
		case SOUTH:
			switch (this) {
			case NORTH:
				return Rotate.ROTATE_180;
			case EAST:
				return Rotate.ROTATE_90;
			case SOUTH:
				return Rotate.NO_ROTATE;
			case WEST:
				return Rotate.ROTATE_270;
			default:
				return Rotate.NO_ROTATE;
			}
		case WEST:
			switch (this) {
			case NORTH:
				return Rotate.ROTATE_270;
			case EAST:
				return Rotate.ROTATE_180;
			case SOUTH:
				return Rotate.ROTATE_90;
			case WEST:
				return Rotate.NO_ROTATE;
			default:
				return Rotate.NO_ROTATE;
			}
		default:
			return Rotate.NO_ROTATE;
		}
	}

//	/**
//	 * TODO probably required by Dungeons2
//	 * @param plane
//	 * @return
//	 */
//	public boolean isSamePlane(Alignment plane) {
//		if (this.getAlignment() == plane) {
//			return true;
//		}
//		return false;
//	}

//

	/**
	 * 
	 * @param direction
	 * @return
	 */
	public static Heading fromDirection(Direction direction) {
		switch (direction) {
		case NORTH:
			return Heading.NORTH;
		case EAST:
			return Heading.EAST;
		case SOUTH:
			return Heading.SOUTH;
		case WEST:
			return Heading.WEST;
		case UP:
			return Heading.UP;
		case DOWN:
			return Heading.DOWN;
		default:
			return Heading.NORTH;
		}
	}

	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<String> getNames() {
		List<String> names = EnumSet.allOf(Heading.class).stream().map(x -> x.name()).collect(Collectors.toList());
		return names;
	}
}
