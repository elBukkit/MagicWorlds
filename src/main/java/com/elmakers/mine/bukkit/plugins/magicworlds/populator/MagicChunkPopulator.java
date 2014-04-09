package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public class MagicChunkPopulator extends BlockPopulator {
	private MagicWorldsController controller;
	private final Map<String, MagicBlockPopulator> blockPopulators = new HashMap<String, MagicBlockPopulator>();
	private int maxY = 128;
	private int minY = 3;
	private int maxAirY = 70;
	
	public void load(MagicWorldsController controller, String worldName, ConfigurationSection config) {
		this.controller = controller;
		maxY = config.getInt("max_y");
		if (maxY == 0) {
			maxY = 128;
		}
		minY = config.getInt("min_y");
		if (minY == 0) {
			minY = 3;
		}
		maxAirY = config.getInt("max_air_y");
		if (maxAirY == 0) {
			maxAirY = 70;
		}
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection handlerConfig = config.getConfigurationSection(key);
			if (handlerConfig == null) continue;
			
			String className = handlerConfig.getString("class");
			MagicBlockPopulator populator = blockPopulators.get(key);
			if (populator == null) {
				populator = createBlockPopulator(className);
			}
			if (populator != null) {
				populator.load(controller, handlerConfig);
				blockPopulators.put(key, populator);
				controller.getLogger().info("Adding " + key + " populator to " + worldName);
			}
		}
	}
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				for (int y = minY; y <= maxY; y++) {
					Block block = chunk.getBlock(x,  y, z);
					if (y > maxAirY && block.getType() == Material.AIR) {
						break;
					}
					
					for (MagicBlockPopulator populator : blockPopulators.values()) {
						populator.populate(block, random);
					}
				}
			}
		}
	}

	public void clear() {
		blockPopulators.clear();
	}
	
	public void addPopulator(String name, MagicBlockPopulator populator) {
		blockPopulators.put(name, populator);
	}

	
	protected static String getPopulatorBuiltinClasspath()
	{
		String baseClass = MagicBlockPopulator.class.getName();
		return baseClass.substring(0, baseClass.lastIndexOf('.'));
	}

	protected MagicBlockPopulator createBlockPopulator(String className)
	{
		String builtinClassPath = getPopulatorBuiltinClasspath();

		if (className == null) return null;

		if (className.indexOf('.') <= 0)
		{
			className = builtinClassPath + "." + className;
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

		if (newObject == null || !(newObject instanceof MagicBlockPopulator))
		{
			controller.getLogger().warning("Error loading handler: " + className + ", does it extend MagicBlockPopulator?");
			return null;
		}

		return (MagicBlockPopulator)newObject;
	}
	
	public WandChestPopulator getWandChestPopulator() {
		for (MagicBlockPopulator populator : blockPopulators.values()) {
			if (populator instanceof WandChestPopulator) {
				return (WandChestPopulator)populator;
			}
		}
		return null;
	}
	
	public boolean isEmpty() {
		return blockPopulators.size() == 0;
	}
}
