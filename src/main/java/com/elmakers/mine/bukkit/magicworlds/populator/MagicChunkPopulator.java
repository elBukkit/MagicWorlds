package com.elmakers.mine.bukkit.magicworlds.populator;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;

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
