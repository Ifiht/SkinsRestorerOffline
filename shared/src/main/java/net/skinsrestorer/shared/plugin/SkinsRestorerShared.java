/*
 * SkinsRestorer
 *
 * Copyright (C) 2022 SkinsRestorer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package net.skinsrestorer.shared.plugin;

import co.aikar.locales.LocaleManager;
import lombok.Getter;
import net.skinsrestorer.shared.SkinsRestorerAPIShared;
import net.skinsrestorer.shared.interfaces.ISRForeign;
import net.skinsrestorer.shared.interfaces.ISRLogger;
import net.skinsrestorer.shared.interfaces.ISRPlugin;
import net.skinsrestorer.shared.storage.CooldownStorage;
import net.skinsrestorer.shared.storage.SkinStorage;
import net.skinsrestorer.shared.update.UpdateChecker;
import net.skinsrestorer.shared.update.UpdateCheckerGitHub;
import net.skinsrestorer.shared.utils.MetricsCounter;
import net.skinsrestorer.shared.utils.connections.MineSkinAPI;
import net.skinsrestorer.shared.utils.connections.MojangAPI;
import net.skinsrestorer.shared.utils.log.SRLogger;

import java.nio.file.Path;

@Getter
public abstract class SkinsRestorerShared implements ISRPlugin {
    protected final MetricsCounter metricsCounter = new MetricsCounter();
    protected final CooldownStorage cooldownStorage = new CooldownStorage();
    protected final SRLogger logger;
    protected final MojangAPI mojangAPI;
    protected final MineSkinAPI mineSkinAPI;
    protected final SkinStorage skinStorage;
    protected final LocaleManager<ISRForeign> localeManager;
    protected final UpdateChecker updateChecker;
    protected final Path dataFolder;
    protected final String version;

    protected SkinsRestorerShared(ISRLogger isrLogger, boolean loggerColor, String version, String updateCheckerAgent, Path dataFolder) {
        this.logger = new SRLogger(isrLogger, loggerColor);
        this.mojangAPI = new MojangAPI(metricsCounter);
        this.mineSkinAPI = new MineSkinAPI(logger, metricsCounter);
        this.skinStorage = new SkinStorage(logger, mojangAPI, mineSkinAPI);
        this.localeManager = LocaleManager.create(ISRForeign::getLocale, SkinsRestorerAPIShared.getDefaultForeign().getLocale());
        this.version = version;
        this.updateChecker = new UpdateCheckerGitHub(2124, version, logger, updateCheckerAgent);
        this.dataFolder = dataFolder;
    }
}
