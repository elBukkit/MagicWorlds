package com.elmakers.mine.bukkit.magicworlds;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MagicChunkGenerator extends ChunkGenerator {
    private static final int WORLD_HEIGHT = 256;

    public boolean load(ConfigurationSection config, MagicWorldsController controller) {
        return true;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
        //return Arrays.asList((BlockPopulator)terrainGenerator);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        short[][] result = new short[WORLD_HEIGHT / 16][];
        short[] floor = new short[4096];
        result[0] = floor;
        for (int y = 0; y < 4; y++) {
            Material mat = Material.BEDROCK;
            if (y > 0 && y < 2) mat = Material.STONE;
            else if (y == 2) mat = Material.DIRT;
            else if (y == 3) mat = Material.GRASS;

            for (int cx = 0; cx < 16; cx++) {
                for (int cz = 0; cz < 16; cz++) {
                    floor[cx * 16 + cz + y * 256] = (short)mat.getId();
                }
            }
        }
        return result;
    }
}
