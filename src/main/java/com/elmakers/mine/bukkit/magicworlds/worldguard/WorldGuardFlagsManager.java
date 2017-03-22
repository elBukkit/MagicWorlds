package com.elmakers.mine.bukkit.magicworlds.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Set;

public class WorldGuardFlagsManager implements WorldGuardFlags {

    private final WorldGuardPlugin customFlags;

    public static SetFlag<String> SPAWN_TAGS = new SetFlag<String>("spawn-tags", RegionGroup.ALL, new StringFlag(null));

    public WorldGuardFlagsManager(Plugin callingPlugin, Plugin wgCustomFlags) {
        customFlags = (WorldGuardPlugin)wgCustomFlags;
        FlagRegistry registry = customFlags.getFlagRegistry();
        registry.register(SPAWN_TAGS);
        callingPlugin.getLogger().info("Registered custom WorldGuard flags: spawn-tags");
    }

    public boolean inTaggedRegion(RegionAssociable source, ApplicableRegionSet checkSet, Set<String> tags) {
        Set<String> regionTags = checkSet.queryValue(source, SPAWN_TAGS);
        if (regionTags == null) {
            return false;
        }
        return regionTags.contains("*") || !Collections.disjoint(regionTags, tags);
    }
}
