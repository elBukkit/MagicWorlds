package com.elmakers.mine.bukkit.magicworlds.worldguard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class WorldGuardManager {
    private boolean enabled = false;
    private WorldGuardAPI worldGuard = null;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled && worldGuard != null && worldGuard.isEnabled();
    }

    public void initialize(Plugin plugin) {
        worldGuard = null;
        if (enabled) {
            try {
                Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
                if (wgPlugin != null) {
                    String[] versionPieces = StringUtils.split(wgPlugin.getDescription().getVersion(), '.');
                    int version = Integer.parseInt(versionPieces[0]);
                    if (version >= 6) {
                        worldGuard = new WorldGuardAPI(wgPlugin, plugin);
                    } else {
                        plugin.getLogger().warning("Only WorldGuard 6 and above are supported- please update! (WG version: " + wgPlugin.getDescription().getVersion() + ")");
                    }
                }
            } catch (Throwable ex) {
            }

            if (worldGuard != null) {
                plugin.getLogger().info("WorldGuard found, region-based spawning enabled");
            }
        } else {
            plugin.getLogger().info("WorldGuard integration disabled");
        }
    }

    public boolean inTaggedRegion(Location location, Set<String> tags) {
        if (enabled && worldGuard != null) {
            return worldGuard.inTaggedRegion(location, tags);
        }
        return false;
    }
}
