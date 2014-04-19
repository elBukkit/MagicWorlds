package com.elmakers.mine.bukkit.plugins.magicworlds.populator.builtin;

import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.Wand;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.MagicBlockPopulator;
import com.elmakers.mine.bukkit.utility.RandomUtils;
import com.elmakers.mine.bukkit.utility.WeightedPair;

public class WandChestPopulator extends MagicBlockPopulator {
	private final LinkedList<WeightedPair<Integer>> baseProbability = new LinkedList<WeightedPair<Integer>>();
	private final LinkedList<WeightedPair<String>> wandProbability = new LinkedList<WeightedPair<String>>();
	private int maxY = 60;
	private int minY = 10;
	
	public void onLoad(ConfigurationSection config) {
		baseProbability.clear();
		wandProbability.clear();
		
		maxY = config.getInt("max_y");
		if (maxY == 0) {
			maxY = 60;
		}
		minY = config.getInt("min_y");
		if (minY == 0) {
			minY = 10;
		}
		
		// Fetch base probabilities
		Float currentThreshold = 0.0f;
		ConfigurationSection base = config.getConfigurationSection("base_probability");
		if (base != null) {
			Set<String> keys = base.getKeys(false);
			for (String key : keys) {
				Integer wandCount = Integer.parseInt(key);
				Float threshold = (float)base.getDouble(key, 0);
				currentThreshold += threshold;
				baseProbability.add(new WeightedPair<Integer>(currentThreshold, wandCount));
			}
		}
		
		// Fetch wand probabilities
		currentThreshold = 0.0f;
		ConfigurationSection wands = config.getConfigurationSection("wand_probability");
		if (wands != null) {
			Set<String> keys = wands.getKeys(false);
			for (String key : keys) {
				String wandName = key;
				Float threshold = (float)wands.getDouble(key, 0);
				currentThreshold += threshold;
				wandProbability.add(new WeightedPair<String>(currentThreshold, wandName));
			}
		}
	}
	
	protected String[] populateChest(Chest chest) {
		// First determine how many wands to add
		MagicAPI magic = controller.getMagic();
		if (magic == null) {
			controller.getLogger().info("Tried to populate chest, but don't have a reference to Magic");
			return new String[0];
		}
		Integer wandCount = RandomUtils.weightedRandom(baseProbability);
		String[] wandNames = new String[wandCount];
		for (int i = 0; i < wandCount; i++) {
			String wandName = RandomUtils.weightedRandom(wandProbability);
			Wand wand = magic.createWand(wandName);
			if (wand != null) {
				chest.getInventory().addItem(wand.getItem());
			} else {
				wandName = "*" + wandName;
			}
			wandNames[i] = wandName;
		}
		
		return wandNames;
	}
	
	public void setMaxY(int maxy) {
		this.maxY = maxy;
	}
	
	@Override
	public void populate(Block block, Random random) {
		if (block.getY() < minY || block.getY() > maxY) return;
		if (block.getType() == Material.CHEST) {
			Chest chest = (Chest)block.getState();
			String[] wandNames = populateChest(chest);
			if (wandNames.length > 0 && controller != null) {
				controller.getLogger().info("Added wands to chest: " + StringUtils.join(wandNames, ", ") + " at " + block.getLocation());
			}
		}
	}

}
