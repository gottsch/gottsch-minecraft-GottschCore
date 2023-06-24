/**
 * 
 */
package com.someguyssoftware.gottschcore.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(RUNTIME)
@Target(TYPE)
/**
 * @author Mark Gottschling on Nov 16, 2019
 *
 */
@Deprecated
public @interface ModInfo {
	String modid();
	String name();
	String version();
	String minecraftVersion();
	String forgeVersion();
	String updateJsonUrl();
}
