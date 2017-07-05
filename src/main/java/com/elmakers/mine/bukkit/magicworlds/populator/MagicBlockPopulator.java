package com.elmakers.mine.bukkit.magicworlds.populator;

import java.util.Random;

import com.elmakers.mine.bukkit.api.block.ModifyType;
import com.elmakers.mine.bukkit.block.MaterialAndData;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;

public abstract class MagicBlockPopulator extends MagicChunkPopulator {
    private int maxY = 255;
    private int minY = 0;
    private int maxAirY = 255;
    private ModifyType modifyType = ModifyType.NO_PHYSICS;

    public boolean load(ConfigurationSection config, MagicWorldsController controller) {
        maxY = config.getInt("max_y", maxY);
        minY = config.getInt("min_y", minY);
        maxAirY = config.getInt("max_air_y", maxAirY);
        String modifyType = config.getString("modifyType", null);
        if (modifyType != null && !modifyType.isEmpty()) {
            this.modifyType = ModifyType.valueOf(modifyType.toUpperCase());
        }
        return super.load(config, controller);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = chunk.getBlock(x,  y, z);
                    if (y > maxAirY && block.getType() == Material.AIR) {
                        break;
                    }

                    MaterialAndData newMaterial = populate(block, random);
                    if (newMaterial != null) {
                        newMaterial.modify(block, modifyType);
                    }
                }
            }
        }
    }

    public abstract MaterialAndData populate(Block block, Random random);
}
