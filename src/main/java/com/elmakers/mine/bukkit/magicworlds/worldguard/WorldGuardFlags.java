package com.elmakers.mine.bukkit.magicworlds.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;

import java.util.Set;

public interface WorldGuardFlags {
    boolean inTaggedRegion(RegionAssociable source, ApplicableRegionSet checkSet, Set<String> tags);
}
