package com.elmakers.mine.bukkit.magicworlds.populator.builtin;

import java.util.Random;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicBlockPopulator;

public class VoidBlockPopulator extends MagicBlockPopulator {
    private static MaterialAndData air = new MaterialAndData(Material.AIR);

    @Override
    public boolean onLoad(ConfigurationSection configuration) {
        return true;
    }

    @Override
    public MaterialAndData populate(Block block, Random random) {
        return air;
    }
}
