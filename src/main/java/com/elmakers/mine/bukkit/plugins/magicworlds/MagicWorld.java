package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.entities.MagicSpawnHandler;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.MagicBlockPopulator;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.WandChestPopulator;

public class MagicWorld {
	private final MagicWorldsController controller;
	private final Map<String, MagicBlockPopulator> blockPopulators = new HashMap<String, MagicBlockPopulator>();
	private final Map<String, MagicSpawnHandler> spawnHandlers = new HashMap<String, MagicSpawnHandler>();
	
	public MagicWorld(MagicWorldsController controller, ConfigurationSection config) {
		this.controller = controller;
		
		ConfigurationSection chunkConfig = config.getConfigurationSection("chunk_generate");
		if (chunkConfig != null) {
			for (String key : chunkConfig.getKeys(false)) {
				ConfigurationSection handlerConfig = chunkConfig.getConfigurationSection(key);
				MagicBlockPopulator populator = loadBlockPopulator(handlerConfig);
				if (populator != null) {
					blockPopulators.put(key, populator);
				}
			}
		}
		
		ConfigurationSection entityConfig = config.getConfigurationSection("entity_spawn");
		if (entityConfig != null) {
			for (String key : entityConfig.getKeys(false)) {
				ConfigurationSection handlerConfig = entityConfig.getConfigurationSection(key);
				MagicSpawnHandler handler = loadSpawnHandler(handlerConfig);
				if (handler != null) {
					spawnHandlers.put(key, handler);
				}
			}
		}
	}
	
	public void installPopulators(World world) {
		for (Entry<String, MagicBlockPopulator> populator : blockPopulators.entrySet()) {
			controller.getLogger().info(" Installing Populator " + populator.getKey() + " in " + world.getName());
			world.getPopulators().add(populator.getValue());
		}
	}
	
	public LivingEntity processEntitySpawn(Plugin plugin, LivingEntity entity) {
        for (MagicSpawnHandler handler : spawnHandlers.values()) {
        	LivingEntity result = handler.process(plugin, entity);
        	if (entity != null) return result;
        }
		return null;
    }
	
	protected static String getSpawnHandlerBuiltinClasspath()
	{
		String baseClass = MagicSpawnHandler.class.getName();
		return baseClass.substring(0, baseClass.lastIndexOf('.'));
	}
	
	protected static String getPopulatorBuiltinClasspath()
	{
		String baseClass = MagicBlockPopulator.class.getName();
		return baseClass.substring(0, baseClass.lastIndexOf('.'));
	}

	protected MagicBlockPopulator loadBlockPopulator(ConfigurationSection configuration)
	{
		String builtinClassPath = getPopulatorBuiltinClasspath();

		String className = configuration.getString("class");
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

		MagicBlockPopulator newPopulator = (MagicBlockPopulator)newObject;
		newPopulator.load(controller, configuration);

		return newPopulator;
	}

	protected MagicSpawnHandler loadSpawnHandler(ConfigurationSection configuration)
	{
		String builtinClassPath = getSpawnHandlerBuiltinClasspath();

		String className = configuration.getString("class");
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
			controller.getLogger().warning("Error loading handler: " + className + ", " + ex.getMessage());
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

		if (newObject == null || !(newObject instanceof MagicSpawnHandler))
		{
			controller.getLogger().warning("Error loading handler: " + className + ", does it extend MagicSpawnHandler?");
			return null;
		}

		MagicSpawnHandler newHandler = (MagicSpawnHandler)newObject;
		newHandler.load(controller, configuration);

		return newHandler;
	}
	
	public WandChestPopulator getWandChestPopulator() {
		for (MagicBlockPopulator populator : blockPopulators.values()) {
			if (populator instanceof WandChestPopulator) {
				return (WandChestPopulator)populator;
			}
		}
		return null;
	}
}
