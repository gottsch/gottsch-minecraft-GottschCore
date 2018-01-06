/**
 * 
 */
package com.someguyssoftware.gottschcore.version;

import static net.minecraftforge.common.ForgeVersion.Status.AHEAD;
import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.PENDING;
import static net.minecraftforge.common.ForgeVersion.Status.UP_TO_DATE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

/**
 * @author Mark Gottschling on Apr 30, 2015
 * @version 2.0
 */
public class VersionChecker {

	/**
	 * Empty constructor
	 */
	public VersionChecker() {}
	
	
	/**
	 * Loads a JSON file from a URL and returns the BuildVersion object for the specified minecraft BuildVersion.
	 * @param url
	 * @return
	 * @since 2.0
	 */
	public static BuildVersion getVersion(String url, BuildVersion mv) {
		InputStream in = null;
		String json = null;
		try {
			in = new URL(url).openStream();
			json = IOUtils.toString(in, Charset.forName("UTF-8"));			
			GottschCore.logger.info("Published Version Info: " + json);
		}
		catch(MalformedURLException e) {
			GottschCore.logger.warn("Bad URL: " + url);
			return BuildVersion.EMPTY_VERSION;
		}
		catch(IOException e) {
			GottschCore.logger.warn("IO Exception occurred: " + e.getMessage());
			return BuildVersion.EMPTY_VERSION;
		}
		catch(Exception e ) {
			GottschCore.logger.warn("Unexpected exception occurred: " + e.getMessage());
			return BuildVersion.EMPTY_VERSION;
		}
		
		try {
			// convert json string into Version object
			Gson gson = new Gson();
			Type collectionType = new TypeToken<Collection<VersionPackage>>(){}.getType();
			List<VersionPackage> packages = gson.fromJson(json, collectionType);
			
			// test if mod version is previous to latest published version
			for (VersionPackage pack : packages) {
				BuildVersion v = pack.getMinecraft();
				if (v.getMajor() == mv.getMajor() &&
						v.getMinor() == mv.getMinor() &&
						v.getBuild() == mv.getBuild()) {
					return pack.getMod();
				}
			}
		}
		catch(JsonSyntaxException e) {
			GottschCore.logger.warn("Bad JSON Syntax: " + json, e);
			return BuildVersion.EMPTY_VERSION;			
		}
		catch(Exception e) {
			GottschCore.logger.warn("Unexpected expection processing json: " + json, e);
			return BuildVersion.EMPTY_VERSION;
		}		
		return BuildVersion.EMPTY_VERSION;
	}
	
	/**
	 * 
	 * @param version the provided version to check against
	 * @param modVersion the mod's current version
	 * @return
	 * @since 2.0
	 */
	public static boolean checkVersion(BuildVersion version, BuildVersion modVersion) {
		
		if (version.getMajor() > modVersion.getMajor()) {
			return false;
		}
		else if (version.getMajor() == modVersion.getMajor()) {
			if (version.getMinor() > modVersion.getMinor()) {
				return false;
			}
			else if (version.getMinor() == modVersion.getMinor()) {
				if (version.getBuild() > modVersion.getBuild()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean checkVersionUsingForge(IMod mod) throws Exception {
        URL url = null;
		try {			
			url = new URL(mod.getUpdateURL());
		} catch (MalformedURLException e) {
			throw new Exception("Unable to open updateURL:", e);
		}
		
		GottschCore.logger.info("[{}] Starting version check at {}", mod.getId(), url.toString());
        boolean status = false;

        InputStream con = url.openStream();
        String data = new String(ByteStreams.toByteArray(con), "UTF-8");
        con.close();

        GottschCore.logger.debug("[{}] Received version check data:\n{}", mod.getId(), data);

        @SuppressWarnings("unchecked")
        Map<String, Object> json = new Gson().fromJson(data, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> promos = (Map<String, String>)json.get("promos");

        String rec = promos.get(MinecraftForge.MC_VERSION + "-recommended");
        String lat = promos.get(MinecraftForge.MC_VERSION + "-latest");
        ComparableVersion current = new ComparableVersion(mod.getVersion());
        
        if (rec != null) {
            ComparableVersion recommended = new ComparableVersion(rec);
            int diff = recommended.compareTo(current);

            if (diff == 0)
                status = true;
            else if (diff < 0) {
                status = true;
                if (lat != null) {
                    ComparableVersion latest = new ComparableVersion(lat);
                    if (current.compareTo(latest) < 0) {
                        status = false;
                    }
                }
            }
            else{
                status = false;
            }
        }
        else if (lat != null) {
            ComparableVersion latest = new ComparableVersion(lat);
            if (current.compareTo(latest) < 0) {
                status = false;
            }
            else
                status = true;
        }
        else
            status = true;
    
		return status;
	}
}
