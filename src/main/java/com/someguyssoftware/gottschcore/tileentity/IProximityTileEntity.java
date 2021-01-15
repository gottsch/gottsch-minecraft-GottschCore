/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import java.util.Random;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public interface IProximityTileEntity {

	void execute(World world, Random random, ICoords blockCoords, ICoords playerCoords);

	double getProximity();

	void setProximity(double proximity);

}
