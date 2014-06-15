package com.elmakers.mine.bukkit.magicworlds;

import com.elmakers.mine.bukkit.magicworlds.populator.builtin.RealTerrainGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MagicChunkGenerator extends ChunkGenerator {
    private RealTerrainGenerator terrainGenerator;
    private static final int WORLD_HEIGHT = 256;

    public MagicChunkGenerator() {
        terrainGenerator = new RealTerrainGenerator();
    }

    public boolean load(ConfigurationSection config, MagicWorldsController controller) {
        return terrainGenerator.load(config, controller);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator)terrainGenerator);
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
        for (int cx = 0; cx < 16; cx++) {
            for (int cz = 0; cz < 16; cz++) {
                floor[cz * 16 * cz] = (short)Material.BEDROCK.getId();
            }
        }
        return result;
    }
}
