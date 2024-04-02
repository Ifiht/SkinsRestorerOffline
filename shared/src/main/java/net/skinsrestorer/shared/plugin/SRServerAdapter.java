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
package net.skinsrestorer.shared.plugin;

import net.skinsrestorer.shared.subjects.SRPlayer;

import java.util.Map;
import java.util.Optional;

public interface SRServerAdapter<P, C> extends SRPlatformAdapter<P, C> {
    void runSync(Runnable runnable);

    void runSyncToPlayer(SRPlayer player, Runnable runnable);

    boolean determineProxy();

    void openServerGUI(SRPlayer player, int page);

    void openProxyGUI(SRPlayer player, int page, Map<String, String> skinList);

    Optional<SRPlayer> getPlayer(String name);
}
