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
	private MagicWorldsController controller;
	private final Map<String, MagicBlockPopulator> blockPopulators = new HashMap<String, MagicBlockPopulator>();
	private final Map<String, MagicSpawnHandler> spawnHandlers = new HashMap<String, MagicSpawnHandler>();
	
	public void load(MagicWorldsController controller, ConfigurationSection config) {
		this.controller = controller;
		
		ConfigurationSection chunkConfig = config.getConfigurationSection("chunk_generate");
		if (chunkConfig != null) {
			for (String key : chunkConfig.getKeys(false)) {
				ConfigurationSection handlerConfig = chunkConfig.getConfigurationSection(key);
				String className = handlerConfig.getString("class");
				MagicBlockPopulator populator = blockPopulators.get(key);
				if (populator == null) {
					populator = createBlockPopulator(className);
				}
				if (populator != null) {
					populator.load(controller, handlerConfig);
					blockPopulators.put(key, populator);
				}
			}
		}
		
		ConfigurationSection entityConfig = config.getConfigurationSection("entity_spawn");
		if (entityConfig != null) {
			for (String key : entityConfig.getKeys(false)) {
				ConfigurationSection handlerConfig = entityConfig.getConfigurationSection(key);
				String className = handlerConfig.getString("class");
				MagicSpawnHandler handler = spawnHandlers.get(key);
				if (handler == null) {
					handler = createSpawnHandler(className);
				}
				if (handler != null) {
					handler.load(controller, handlerConfig);
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

	protected MagicSpawnHandler createSpawnHandler(String className)
	{
		String builtinClassPath = getSpawnHandlerBuiltinClasspath();

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

		return (MagicSpawnHandler)newObject;
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
