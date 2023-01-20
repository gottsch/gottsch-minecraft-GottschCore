/*
 * This file is part of  GottschCore.
 * Copyright (c) 2017 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.random;

import java.util.Random;

/**
 * @author Mark Gottschling on May 6, 2017
 *
 */
public final class RandomHelper {
	private static final int INT_MAX_PROB = 100;
	private static final double DOUBLE_MAX_PROB = 100.0D;
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(final int min, final int max) {
		final Random random = new Random();
		return randomInt(random, min, max);
	}
	
	/**
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(final Random random,int min, int max) {
		if (min < 0) {
			min = 0;
		}
		
		if (max <= 0) {
			return 0;
		}
		
		if (min == max) {
			return min;
		}
		
		if (min > max) {
			return random.nextInt(min) + 1;
		}
		return random.nextInt(max - min  + 1) + min;
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static double randomDouble(final double min, final double max) {
		final Random random = new Random();
		return randomDouble(random, min, max);
	}
	
	/**
	 * 
	 * @param random
	 * @param min
	 * @param max
	 * @return
	 */
	public static double randomDouble(final Random random, double min, double max) {
		if (min < 0) {
			min = 0;
		}
		
		if (max <= 0) {
			return 0;
		}
		
		if (min == max) {
			return min;
		}
		
		if (min > max) {
			return random.nextDouble()*min;
		}
		return random.nextDouble()*(max - min) + min;
	}
	
	/**
	 * 
	 * @param random
	 * @param probability
	 * @return
	 */
	public static boolean checkProbability(final Random random, final int probability) {
		Random r = null;
		r = (random == null) ? new Random() : random;
		if (r.nextInt(INT_MAX_PROB) < probability) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param random
	 * @param probability
	 * @return
	 */
	public static boolean checkProbability(final Random random, final double probability) {
		Random r = null;
		r = (random == null) ? new Random() : random;
		if (r.nextDouble() * DOUBLE_MAX_PROB < probability) {
			return true;
		}
		return false;
	}
}
