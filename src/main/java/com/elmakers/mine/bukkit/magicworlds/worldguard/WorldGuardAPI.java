package com.elmakers.mine.bukkit.magicworlds.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;

public class WorldGuardAPI {
    private final Plugin owningPlugin;
	private WorldGuardPlugin worldGuard = null;
    private WorldGuardFlags customFlags = null;

	public boolean isEnabled() {
		return worldGuard != null;
	}

    public WorldGuardAPI(Plugin plugin, Plugin owningPlugin) {
        this.owningPlugin = owningPlugin;
        if (plugin instanceof WorldGuardPlugin) {
            worldGuard = (WorldGuardPlugin)plugin;
            try {
                owningPlugin.getLogger().info("Pre-check for WorldGuard custom flag registration");
                customFlags = new WorldGuardFlagsManager(owningPlugin, worldGuard);
            } catch (NoSuchMethodError incompatible) {
                // Ignored, will follow up in checkFlagSupport
            } catch (Throwable ex) {
                owningPlugin.getLogger().log(Level.WARNING, "Unexpected error setting up custom flags, please make sure you are on WorldGuard 6.2 or above", ex);
            }
        }
    }

    public void checkFlagSupport() {
        if (customFlags == null) {
            try {
                Plugin customFlagsPlugin = owningPlugin.getServer().getPluginManager().getPlugin("WGCustomFlags");
                if (customFlagsPlugin != null) {
                    customFlags = new WGCustomFlagsManager(customFlagsPlugin);
                }
            } catch (Throwable ex) {
                owningPlugin.getLogger().log(Level.WARNING, "Error integration with WGCustomFlags", ex);
            }

            if (customFlags != null) {
                owningPlugin.getLogger().info("WGCustomFlags found, added custom flags");
            } else {
                owningPlugin.getLogger().log(Level.WARNING, "Failed to set up custom flags, please make sure you are on WorldGuard 6.2 or above, or use the WGCustomFlags plugin");
            }
        }
    }

    public boolean inTaggedRegion(Location location, Set<String> tags) {
        if (location != null && worldGuard != null && customFlags != null)
        {
            RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
            if (regionManager == null) {
                return false;
            }

            ApplicableRegionSet checkSet = regionManager.getApplicableRegions(location);
            if (checkSet == null) {
                return false;
            }

            return customFlags.inTaggedRegion(Associables.constant(Association.NON_MEMBER), checkSet, tags);
        }
        return false;
    }
}
