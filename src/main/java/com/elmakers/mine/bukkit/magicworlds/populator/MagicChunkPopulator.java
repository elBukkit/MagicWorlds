package com.elmakers.mine.bukkit.magicworlds.populator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.magicworlds.populator.builtin.WandChestPopulator;

public abstract class MagicChunkPopulator extends BlockPopulator {
	public static final String BUILTIN_CLASSPATH = "com.elmakers.mine.bukkit.magicworlds.populator.builtin";
	
	protected MagicWorldsController controller;

    protected void initialize(MagicWorldsController controller) {
        this.controller = controller;
    }

	public boolean load(ConfigurationSection config, MagicWorldsController controller) {
        initialize(controller);
        return onLoad(config);
	}

    public abstract boolean onLoad(ConfigurationSection config);
}
