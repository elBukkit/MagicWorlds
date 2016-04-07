package com.elmakers.mine.bukkit.magicworlds.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class WorldGuardAPI {
	private WorldGuardPlugin worldGuard = null;
    private WGCustomFlagsManager customFlags = null;

	public boolean isEnabled() {
		return worldGuard != null;
	}
	
	public WorldGuardAPI(Plugin plugin, Plugin owningPlugin) {
        if (plugin instanceof WorldGuardPlugin) {
            worldGuard = (WorldGuardPlugin)plugin;
            try {
                Plugin customFlagsPlugin = plugin.getServer().getPluginManager().getPlugin("WGCustomFlags");
                if (customFlagsPlugin != null) {
                    customFlags = new WGCustomFlagsManager(customFlagsPlugin);
                }
            } catch (Throwable ex) {
            }

            if (customFlags != null) {
                owningPlugin.getLogger().info("WGCustomFlags found, added custom flags");
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
