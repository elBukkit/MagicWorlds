package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public abstract class MagicBlockPopulator {
	protected MagicWorldsController controller;
	
	public void load(MagicWorldsController controller, ConfigurationSection configuration) {
		this.controller = controller;
		onLoad(configuration);
	}
	
	public void populate(Chunk chunk, Random random, int minY, int maxY, int maxAirY) {
		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				for (int y = minY; y <= maxY; y++) {
					Block block = chunk.getBlock(x,  y, z);
					if (y > maxAirY && block.getType() == Material.AIR) {
						break;
					}
					
					populate(block, random);
				}
			}
		}
	}
	
	public abstract void onLoad(ConfigurationSection configuration);
	public abstract void populate(Block block, Random random);
}
