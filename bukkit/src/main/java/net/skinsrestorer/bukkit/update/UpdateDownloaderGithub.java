/*
 * SkinsRestorer
 * Copyright (C) 2024  SkinsRestorer Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.skinsrestorer.bukkit.update;

import lombok.RequiredArgsConstructor;
import net.skinsrestorer.bukkit.utils.PluginJarProvider;
import net.skinsrestorer.shared.exception.UpdateException;
import net.skinsrestorer.shared.log.SRLogger;
import net.skinsrestorer.shared.plugin.SRPlugin;
import net.skinsrestorer.shared.update.UpdateDownloader;
import net.skinsrestorer.shared.utils.SRHelpers;
import org.bukkit.Server;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

/**
 * Parts taken from <a href="https://github.com/InventivetalentDev/SpigetUpdater">SpigetUpdater</a>
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UpdateDownloaderGithub implements UpdateDownloader {
    private final SRPlugin plugin;
    private final SRLogger logger;
    private final Server server;
    private final PluginJarProvider jarProvider;

    private void download(String downloadUrl, Path targetFile) throws UpdateException {
        try {
            // We don't use HttpClient because this writes to a file directly
            HttpsURLConnection connection = (HttpsURLConnection) new URL(downloadUrl).openConnection();
            connection.setRequestProperty("User-Agent", plugin.getUserAgent());
            if (connection.getResponseCode() != 200) {
                throw new UpdateException("Download returned status code %d".formatted(connection.getResponseCode()));
            }

            byte[] fileData;
            try (InputStream is = connection.getInputStream()) {
                if (is == null) {
                    throw new IOException("Failed to open input stream");
                }

                fileData = is.readAllBytes();
                String hash = connection.getHeaderField("content-md5");
                if (hash != null && !Arrays.equals(Base64.getDecoder().decode(hash), SRHelpers.md5(fileData))) {
                    throw new UpdateException("Downloaded file is corrupted");
                } else if (hash == null) {
                    logger.warning("[GitHubUpdate] MD5 header not found, cannot verify integrity");
                } else {
                    logger.debug("[GitHubUpdate] MD5 hash successfully verified");
                }
            }

            Files.write(targetFile, fileData);
        } catch (IOException e) {
            throw new UpdateException("Download failed", e);
        }
    }

    @Override
    public boolean downloadUpdate(String downloadUrl) {
        Path pluginFile = jarProvider.get(); // /plugins/XXX.jar
        Path updateFolder = server.getUpdateFolderFile().toPath();
        try {
            Files.createDirectories(updateFolder);
        } catch (IOException e) {
            logger.warning("[GitHubUpdate] Could not create update folder", e);
            return false;
        }

        Path updateFile = updateFolder.resolve(pluginFile.getFileName()); // /plugins/update/XXX.jar

        logger.info("[GitHubUpdate] Downloading update...");
        try {
            long start = System.currentTimeMillis();
            download(downloadUrl, updateFile);

            logger.info(String.format("[GitHubUpdate] Downloaded update in %dms", System.currentTimeMillis() - start));
            logger.info(String.format("[GitHubUpdate] Update saved as %s", updateFile.getFileName()));
            logger.info("[GitHubUpdate] The update will be loaded on the next server restart");
        } catch (UpdateException e) {
            logger.warning("[GitHubUpdate] Could not download update", e);
            return false;
        }

        return true;
    }
}
