/**
 * 
 */
package com.someguyssoftware.gottschcore.version;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.maven.artifact.versioning.ComparableVersion;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.versions.mcp.MCPVersion;

/**
 * @author Mark Gottschling on Apr 30, 2015
 * @version 2.0
 */
@Deprecated
public class VersionChecker {

	/**
	 * Empty constructor
	 */
	public VersionChecker() {
	}

	/**
	 * TODO instead of having 2 methods, should have 2 implementations of
	 * IVersionRetriever
	 * 
	 * @param updateURL
	 * @return
	 */
	public static BuildVersion getVersionUsingForge(IMod mod) {
		BuildVersion buildVersion = BuildVersion.EMPTY_VERSION;

		URL url = null;
		try {
			url = new URL(mod.getUpdateURL());
		} catch (MalformedURLException e) {
			GottschCore.LOGGER.warn("Unable to open updateURL:" + mod.getUpdateURL(), e);
			return buildVersion;
		}

		GottschCore.LOGGER.info("[{}] Starting version check at {}", mod.getId(), url.toString());

		String data = null;
		try {
			InputStream con = url.openStream();
			data = new String(ByteStreams.toByteArray(con), "UTF-8");
			con.close();
		} catch (Exception e) {
			GottschCore.LOGGER.warn("Unexpected expection in data stream: ", e);
			return buildVersion;
		}
		GottschCore.LOGGER.debug("[{}] Received version check data:\n{}", mod.getId(), data);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> json = new Gson().fromJson(data, Map.class);
			@SuppressWarnings("unchecked")
			Map<String, String> promos = (Map<String, String>) json.get("promos");

			String rec = promos.get(MCPVersion.getMCVersion() + "-recommended");
			String lat = promos.get(MCPVersion.getMCVersion() + "-latest");

			if (rec != null) {
				buildVersion = new BuildVersion(rec);
			} else if (lat != null) {
				buildVersion = new BuildVersion(lat);
			}
		} catch (JsonSyntaxException e) {
			GottschCore.LOGGER.warn("Bad JSON Syntax: " + data, e);
		} catch (Exception e) {
			GottschCore.LOGGER.warn("Unexpected expection processing json: " + data, e);
		}

		return buildVersion;
	}

	/**
	 * 
	 * @param version
	 * @param modVersion
	 * @return
	 * @throws Exception
	 * @since 2.0
	 */
	public static boolean checkVersionUsingForge(BuildVersion version, BuildVersion modVersion) throws Exception {
		if (version == null || modVersion == null)
			return true;
		ComparableVersion current = new ComparableVersion(modVersion.toString());
		ComparableVersion recommended = new ComparableVersion(version.toString());

		int diff = recommended.compareTo(current);

		if (diff > 0)
			return false;

		return true;
	}
}
