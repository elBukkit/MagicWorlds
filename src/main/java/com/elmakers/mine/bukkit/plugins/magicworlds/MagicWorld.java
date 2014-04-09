package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.entities.MagicSpawnHandler;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.MagicChunkPopulator;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.WandChestPopulator;

public class MagicWorld {
	private MagicWorldsController controller;
	private MagicChunkPopulator chunkPopulator;
	private final Map<String, MagicSpawnHandler> spawnHandlers = new HashMap<String, MagicSpawnHandler>();
	
	public void load(MagicWorldsController controller, String worldName, ConfigurationSection config) {
		this.controller = controller;
		
		if (chunkPopulator == null) {
			chunkPopulator = new MagicChunkPopulator();
		}
		chunkPopulator.clear();
		
		ConfigurationSection chunkConfig = config.getConfigurationSection("chunk_generate");
		if (chunkConfig != null) {
			chunkPopulator.load(controller, worldName, chunkConfig);
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
		if (chunkPopulator.isEmpty()) return;
		controller.getLogger().info("Installing Populators in " + world.getName());
		world.getPopulators().add(chunkPopulator);
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
		return chunkPopulator.getWandChestPopulator();
	}
}
