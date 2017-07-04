package com.elmakers.mine.bukkit.magicworlds.populator;

import java.util.Random;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import com.elmakers.mine.bukkit.utility.CompatibilityUtils;
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

    public boolean load(ConfigurationSection config, MagicWorldsController controller) {
        maxY = config.getInt("max_y", maxY);
        minY = config.getInt("min_y", minY);
        maxAirY = config.getInt("max_air_y", maxAirY);
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
                        Short data = newMaterial.getData();
                        Material material = newMaterial.getMaterial();
                        material = (material == null) ? block.getType() : material;
                        data = (data == null) ? block.getData() : data;
                        if (material != block.getType() || data != block.getData()) {
                            CompatibilityUtils.setBlockFast(chunk, block.getX(), block.getY(), block.getZ(), material, data);
                        }
                    }
                }
            }
        }
    }

    public abstract MaterialAndData populate(Block block, Random random);
}
