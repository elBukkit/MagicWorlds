package com.elmakers.mine.bukkit.magicworlds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicChunkPopulator;
import com.elmakers.mine.bukkit.magicworlds.populator.builtin.WandChestPopulator;
import com.elmakers.mine.bukkit.magicworlds.spawn.MagicSpawnHandler;

public class MagicWorld {
	private MagicWorldsController controller;
	private MagicChunkPopulator chunkPopulator;
	private MagicSpawnHandler spawnHandler;
    private String copyFrom;
    private String worldName;
	
	public void load(String world, ConfigurationSection config, MagicWorldsController controller) {
        worldName = world;
        copyFrom = config.getString("copy", "");
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

    public void onWorldInit(final Plugin plugin, final World initWorld)
    {
        if (copyFrom.length() == 0 || !initWorld.getName().equals(copyFrom)) return;

        // Wait a few ticks to do this, creating worlds inside of world
        // initialization seems bad!
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
           @Override
           public void run()
           {
               // Create this world if it doesn't exist
               World world = Bukkit.getWorld(worldName);
               if (world == null) {
                   Bukkit.getLogger().info("Creating " + worldName + " using settings copied from " + initWorld.getName());
                   world = Bukkit.createWorld(new WorldCreator(worldName).copy(initWorld));
                   if (world == null) {
                       Bukkit.getLogger().warning("Failed to create world: " + worldName);
                   }
               }
           }
        }, 8);
    }

	public WandChestPopulator getWandChestPopulator() {
		return chunkPopulator.getWandChestPopulator();
	}
}
