package com.elmakers.mine.bukkit.magicworlds.worldguard;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Set;

public class WGCustomFlagsManager {

    private final WGCustomFlagsPlugin customFlags;

    public static SetFlag<String> SPAWN_TAGS = new SetFlag<String>("spawn-tags", RegionGroup.ALL, new StringFlag(null));

    public WGCustomFlagsManager(Plugin wgCustomFlags) {
        customFlags = (WGCustomFlagsPlugin)wgCustomFlags;
        customFlags.addCustomFlag(SPAWN_TAGS);
    }

    public boolean inTaggedRegion(RegionAssociable source, ApplicableRegionSet checkSet, Set<String> tags) {
        Set<String> regionTags = checkSet.queryValue(source, SPAWN_TAGS);
        if (regionTags == null) {
            return false;
        }
        return regionTags.contains("*") || !Collections.disjoint(regionTags, tags);
    }
}
