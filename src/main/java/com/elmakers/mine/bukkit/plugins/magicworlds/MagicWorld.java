package com.elmakers.mine.bukkit.plugins.magicworlds;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.populator.MagicChunkPopulator;
import com.elmakers.mine.bukkit.plugins.magicworlds.populator.builtin.WandChestPopulator;
import com.elmakers.mine.bukkit.plugins.magicworlds.spawn.MagicSpawnHandler;

public class MagicWorld {
	private MagicWorldsController controller;
	private MagicChunkPopulator chunkPopulator;
	private MagicSpawnHandler spawnHandler;
	
	public void load(String worldName, ConfigurationSection config, MagicWorldsController controller) {
		this.controller = controller;
		
		if (chunkPopulator == null) {
			chunkPopulator = new MagicChunkPopulator();
		}
		if (spawnHandler == null) {
			spawnHandler = new MagicSpawnHandler();
		}
		chunkPopulator.clear();
		
		ConfigurationSection chunkConfig = config.getConfigurationSection("chunk_generate");
		if (chunkConfig != null) {
			chunkPopulator.load(worldName, chunkConfig, controller);
		}
		
		spawnHandler.clear();
		ConfigurationSection entityConfig = config.getConfigurationSection("entity_spawn");
		if (entityConfig != null) {
			spawnHandler.load(worldName, entityConfig, controller);
		}
	}
	
	public void installPopulators(World world) {
		if (chunkPopulator.isEmpty()) return;
		controller.getLogger().info("Installing Populators in " + world.getName());
		world.getPopulators().add(chunkPopulator);
	}
	
	public LivingEntity processEntitySpawn(Plugin plugin, LivingEntity entity) {
		return spawnHandler.process(plugin, entity);
    }
	
	protected static String getSpawnHandlerBuiltinClasspath()
	{
		String baseClass = MagicSpawnHandler.class.getName();
		return baseClass.substring(0, baseClass.lastIndexOf('.'));
	}
	
	public WandChestPopulator getWandChestPopulator() {
		return chunkPopulator.getWandChestPopulator();
	}
}
