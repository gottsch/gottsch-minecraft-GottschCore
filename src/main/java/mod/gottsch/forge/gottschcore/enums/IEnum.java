/*
 * This file is part of  GottschCore.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.enums;

import java.util.Map;

/**
 * @author Mark Gottschling on Aug 31, 2014
 *
 */
public interface IEnum {

	public Integer getCode();
	public void setCode(Integer code);
	public String getValue();
	public void setValue(String value);
	
	public Map<Integer, IEnum> getCodes();
	public Map<String, IEnum> getValues();
	
	public String getName();
}
