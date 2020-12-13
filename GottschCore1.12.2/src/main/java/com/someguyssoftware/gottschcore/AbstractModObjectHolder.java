/**
 * 
 */
package com.someguyssoftware.gottschcore;

import java.lang.reflect.Field;

import com.someguyssoftware.gottschcore.GottschCore;

/**
 * @author Mark Gottschling on Jul 23, 2017
 * @deprecated use com.someguyssoftware.gottschcore.mod.AbstractModObjectHolder
 */
@Deprecated
public abstract class AbstractModObjectHolder {
	
	/**
	 * 
	 * @param classToSet
	 * @param fieldName
	 * @param value
	 */
	public static void setPropertyWithReflection(Class classToSet, String fieldName, Object value) {
		// get the field by reflection
		Field f = null;
		try {
			f = classToSet.getField(fieldName);
		} catch (NoSuchFieldException e) {
			GottschCore.logger.warn(String.format("No such field [%s] for class", fieldName, classToSet.getSimpleName()));
		} catch (SecurityException e) {
			GottschCore.logger.warn("Security violation: ", e);
		}
		
		// set the field property
		try {
			if (f != null) {
				f.set(null, value);
			}
		} catch (IllegalArgumentException e) {
			GottschCore.logger.warn(String.format("IllegalArguementException for field [%s] using argument [%s]", f.getName(), value));
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			GottschCore.logger.warn(String.format("IllegalAccessException for field [%s]", f.getName(), value));
		}
	}
}
