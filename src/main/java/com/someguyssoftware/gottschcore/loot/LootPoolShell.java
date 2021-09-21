/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.gottschcore.loot;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

/**
 * @author Mark Gottschling on Dec 1, 2020
 *
 */
public class LootPoolShell {
    private NumberProvider rolls;
    private NumberProvider bonusRolls;
    private String name;
    
    public LootPoolShell() {}
    
	public NumberProvider getRolls() {
		return rolls;
	}
	public void setRolls(NumberProvider rolls) {
		this.rolls = rolls;
	}
	public NumberProvider getBonusRolls() {
		return bonusRolls;
	}
	public void setBonusRolls(NumberProvider bonusRolls) {
		this.bonusRolls = bonusRolls;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "LootPoolShell [rolls=" + rolls + ", bonusRolls=" + bonusRolls + ", name=" + name + "]";
	}
}
