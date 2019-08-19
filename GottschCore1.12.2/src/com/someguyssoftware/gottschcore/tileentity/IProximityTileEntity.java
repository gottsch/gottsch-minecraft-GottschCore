/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import java.util.Random;

import com.someguyssoftware.gottschcore.positional.Coords;

import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public interface IProximityTileEntity {

	void execute(World world, Random random, Coords blockCoords, Coords playerCoords);

	double getProximity();

	void setProximity(double proximity);

}
