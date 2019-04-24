/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraft.util.datafix.DataFixer;

/**
 * @author Mark Gottschling on Feb 3, 2019
 *
 */
public class TemplateManager {
	private final IMod mod;
	private final String baseFolder;
	private final DataFixer fixer;
	
	/**
	 * 
	 * @param mod
	 * @param baseFolder
	 * @param fixer
	 */
	public TemplateManager(IMod mod, String baseFolder, DataFixer fixer) {
		this.mod = mod;
		this.baseFolder = baseFolder;
		this.fixer = fixer;
	}

	/**
	 * @return the mod
	 */
	public IMod getMod() {
		return mod;
	}

	/**
	 * @return the baseFolder
	 */
	public String getBaseFolder() {
		return baseFolder;
	}

	/**
	 * @return the fixer
	 */
	public DataFixer getFixer() {
		return fixer;
	}

}
