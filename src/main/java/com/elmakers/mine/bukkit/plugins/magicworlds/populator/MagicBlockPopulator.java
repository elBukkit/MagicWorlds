package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public abstract class MagicBlockPopulator extends BlockPopulator {
	protected MagicWorldsController controller;
	
	public void load(MagicWorldsController controller, ConfigurationSection configuration) {
		this.controller = controller;
		onLoad(configuration);
	}
	
	public abstract void onLoad(ConfigurationSection configuration);
}
