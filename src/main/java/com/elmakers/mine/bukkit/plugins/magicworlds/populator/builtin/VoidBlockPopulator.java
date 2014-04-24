package com.elmakers.mine.bukkit.plugins.magicworlds.populator.builtin;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.plugins.magicworlds.populator.MagicBlockPopulator;

public class VoidBlockPopulator extends MagicBlockPopulator {

	@Override
	public boolean onLoad(ConfigurationSection configuration) {
		return true;
	}

	@Override
	public void populate(Block block, Random random) {
		block.setType(Material.AIR);
	}
}
