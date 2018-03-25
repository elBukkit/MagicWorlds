package com.elmakers.mine.bukkit.magicworlds.populator;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.magicworlds.populator.builtin.MagicChestPopulator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MagicChunkHandler extends BlockPopulator {
    public static final String BUILTIN_CLASSPATH = "com.elmakers.mine.bukkit.magicworlds.populator.builtin";

    private MagicWorldsController controller;
    private final Map<String, MagicChunkPopulator> chunkPopulators = new HashMap<String, MagicChunkPopulator>();

    public void load(String worldName, ConfigurationSection config, MagicWorldsController controller) {
        this.controller = controller;
        for (String key : config.getKeys(false)) {
            ConfigurationSection handlerConfig = config.getConfigurationSection(key);
            if (handlerConfig == null) continue;

            String className = handlerConfig.getString("class");
            MagicChunkPopulator populator = chunkPopulators.get(key);
            if (populator == null) {
                populator = createChunkPopulator(className);
            }
            if (populator != null) {
                if (populator.load(handlerConfig, controller)) {
                    chunkPopulators.put(key, populator);
                    controller.getLogger().info("Adding " + key + " populator to " + worldName);
                } else {
                    controller.getLogger().info("Skipping invalid " + key + " populator for " + worldName);
                }
            }
        }
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (MagicChunkPopulator populator : chunkPopulators.values()) {
            populator.populate(world, random, chunk);
        }
    }

    public void clear() {
        chunkPopulators.clear();
    }

    public void addPopulator(String name, MagicChunkPopulator populator) {
        chunkPopulators.put(name, populator);
    }

    protected MagicChunkPopulator createChunkPopulator(String className)
    {
        if (className == null) return null;

        if (className.indexOf('.') <= 0)
        {
            className = BUILTIN_CLASSPATH + "." + className;
        }

        Class<?> handlerClass = null;
        try
        {
            handlerClass = Class.forName(className);
        }
        catch (Throwable ex)
        {
            controller.getLogger().warning("Error loading handler: " + className);
            ex.printStackTrace();
            return null;
        }

        Object newObject;
        try
        {
            newObject = handlerClass.newInstance();
        }
        catch (Throwable ex)
        {
            controller.getLogger().warning("Error loading handler: " + className);
            ex.printStackTrace();
            return null;
        }

        if (newObject == null || !(newObject instanceof MagicChunkPopulator))
        {
            controller.getLogger().warning("Error loading handler: " + className + ", does it extend MagicBlockPopulator?");
            return null;
        }

        return (MagicChunkPopulator)newObject;
    }

    public MagicChestPopulator getMagicChestPopulator() {
        for (MagicChunkPopulator populator : chunkPopulators.values()) {
            if (populator instanceof MagicChestPopulator) {
                return (MagicChestPopulator)populator;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return chunkPopulators.size() == 0;
    }
}
