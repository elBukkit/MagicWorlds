package com.elmakers.mine.bukkit.magicworlds;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicChunkHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.magicworlds.populator.builtin.WandChestPopulator;
import com.elmakers.mine.bukkit.magicworlds.spawn.MagicSpawnHandler;

import java.util.Random;

public class MagicWorld {
    private enum WorldState { UNLOADED, LOADING, LOADED };
	private MagicWorldsController controller;
	private MagicChunkHandler chunkHandler;
	private MagicSpawnHandler spawnHandler;
    private String copyFrom;
    private boolean autoLoad = false;
    private World.Environment worldEnvironment = World.Environment.NORMAL;
    private WorldType worldType = WorldType.NORMAL;
    private String worldName;
	private long seed;
    private static Random random = new Random();
    private WorldState state = WorldState.UNLOADED;

	public void load(String name, ConfigurationSection config, MagicWorldsController controller) {
        worldName = name;
        copyFrom = config.getString("copy", "");
        if (config.contains("environment")) {
            String typeString = config.getString("type");
            try {
                worldEnvironment = World.Environment.valueOf(typeString.toUpperCase());
            } catch (Exception ex) {
                controller.getLogger().warning("Invalid world environment: " + typeString);
            }
        }
        if (config.contains("type")) {
            String typeString = config.getString("type");
            try {
                worldType = WorldType.valueOf(typeString.toUpperCase());
            } catch (Exception ex) {
                controller.getLogger().warning("Invalid world type: " + typeString);
            }
        }
        seed = config.getLong("seed", random.nextLong());
        this.controller = controller;
        autoLoad = config.getBoolean("autoload", false);
		
		if (chunkHandler == null) {
            chunkHandler = new MagicChunkHandler();
		}
		if (spawnHandler == null) {
			spawnHandler = new MagicSpawnHandler();
		}
        chunkHandler.clear();
		ConfigurationSection chunkConfig = config.getConfigurationSection("chunk_generate");
		if (chunkConfig != null) {
            chunkHandler.load(worldName, chunkConfig, controller);
		}
		
		spawnHandler.clear();
		ConfigurationSection entityConfig = config.getConfigurationSection("entity_spawn");
		if (entityConfig != null) {
			spawnHandler.load(worldName, entityConfig, controller);
		}

        // Autoload worlds
        if (autoLoad && copyFrom.isEmpty()) {
            state = WorldState.LOADING;
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                controller.getLogger().info("Loading " + worldName + " as " + worldEnvironment);
                WorldCreator worldCreator = WorldCreator.name(worldName);
                worldCreator.seed(seed);
                worldCreator.environment(worldEnvironment);
                worldCreator.type(worldType);
                try {
                    world = Bukkit.createWorld(worldCreator);
                } catch (Exception ex) {
                    world = null;
                    ex.printStackTrace();
                }
                if (world == null) {
                    controller.getLogger().warning("Failed to create world: " + worldName);
                }
            }
        }
	}
	
	public void installPopulators(World world) {
		if (chunkHandler.isEmpty()) return;
		controller.getLogger().info("Installing Populators in " + world.getName());
		world.getPopulators().add(chunkHandler);
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
        // Loaded check
        if (state != WorldState.UNLOADED) {
            return;
        }

        // Flag loaded worlds
        if (initWorld.getName().equals(worldName)) {
            state = WorldState.LOADED;
            return;
        }

        if (copyFrom.length() == 0 || !initWorld.getName().equals(copyFrom)) return;

        state = WorldState.LOADING;

        // Wait a few ticks to do this, to avoid errros during initializatoin
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
           @Override
           public void run()
           {
               // Create this world if it doesn't exist
               World world = Bukkit.getWorld(worldName);
               if (world == null) {
                   controller.getLogger().info("Loading " + worldName + " using settings copied from " + initWorld.getName());
                   world = Bukkit.createWorld(new WorldCreator(worldName).copy(initWorld));
                   if (world == null) {
                       controller.getLogger().warning("Failed to create world: " + worldName);
                   }
               }
           }
        }, 1);
    }

	public WandChestPopulator getWandChestPopulator() {
		return chunkHandler.getWandChestPopulator();
	}
}
