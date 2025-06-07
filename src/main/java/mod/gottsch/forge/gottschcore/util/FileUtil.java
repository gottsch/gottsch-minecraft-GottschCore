/*
 * This file is part of  GottschCore.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.nio.file.Path;

/**
 * @author by Mark Gottschling on 6/3/2025
 */
public class FileUtil {
    /**
     * attempts to retrieve the File object representing the current world's save folder.
     * this method works for both integrated (single-player) and dedicated servers.
     *
     * @return a File object pointing to the world save directory, or null if not in a world.
     */
    public static File getServerWorldSaveFolder() {
        Minecraft minecraft = Minecraft.getInstance();

        // server side (including integrated server in single-player)
        MinecraftServer server = minecraft.getSingleplayerServer(); // Gets the integrated server if in single-player, or null if on a dedicated server

        if (server != null) {
            // This is the most reliable way to get the world folder for the currently loaded server.
            // On a dedicated server, this is the main server instance.
            // In single-player, this is the integrated server.
            return server.getServerDirectory();
        }

        // if not in a world or an unexpected state
        return null;
    }

    /**
     * gets the current world's save folder path as a Path object.
     * useful for modern Java NIO file operations.
     *
     * @return a Path object pointing to the world save directory, or null if not in a world.
     */
    public static Path getServerWorldSavePath() {
        File folder = getServerWorldSaveFolder();
        return (folder != null) ? folder.toPath() : null;
    }
}
