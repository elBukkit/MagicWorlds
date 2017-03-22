package com.elmakers.mine.bukkit.magicworlds.worldguard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;

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
        if (enabled) {
            if (worldGuard == null) {
                plugin.getLogger().info("WorldGuard not found, add WorldGuard 6 or higher for region-based spawning");
            } else {
                plugin.getLogger().info("WorldGuard found, spawn-tags can be used for region-based spawning");
                worldGuard.checkFlagSupport();
            }
        } else {
            worldGuard = null;
            plugin.getLogger().info("WorldGuard integration disabled.");
        }
    }

    public void initializeFlags(Plugin plugin) {
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
            plugin.getLogger().log(Level.WARNING, "Error setting up custom WorldGuard flags", ex);
        }
    }

    public boolean inTaggedRegion(Location location, Set<String> tags) {
        if (enabled && worldGuard != null) {
            return worldGuard.inTaggedRegion(location, tags);
        }
        return false;
    }
}
