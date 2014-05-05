package com.elmakers.mine.bukkit.magicworlds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.magicworlds.populator.builtin.WandChestPopulator;

public class MagicWorldsController implements Listener 
{
	public MagicWorldsController(final Plugin plugin)
	{
		this.logger = plugin.getLogger();
		this.plugin = plugin;
		
		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
		if (magicPlugin == null || !(magicPlugin instanceof MagicAPI)) {
			logger.warning("Magic plugin not found, spell casting and wand creation disabled");
			logger.warning("Make sure you have the MagicLib plugin instead, or MagicWorlds will probably break");
			this.magicAPI = null;
		} else {
			logger.info("Integrating with Magic");
			this.magicAPI = (MagicAPI)magicPlugin;
		}		
	}
	
	/*
	 * Saving and loading
	 */

	public void initialize()
	{
		plugin.saveDefaultConfig();	
		load();
	}
	
	public void load()
	{
		try {
			plugin.reloadConfig();
			Configuration config = plugin.getConfig();
			metricsLevel = config.getInt("metrics_level", metricsLevel);
			ConfigurationSection worlds = config.getConfigurationSection("worlds");
			if (worlds != null) {
				Set<String> worldKeys = worlds.getKeys(false);
				for (String worldName : worldKeys) {
					logger.info("Customizing world " + worldName);
					MagicWorld world = magicWorlds.get(worldName);
					if (world == null) world = new MagicWorld();
					world.load(worldName, worlds.getConfigurationSection(worldName), this);
					magicWorlds.put(worldName, world);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		metrics = null;
		if (metricsLevel > 0) {
			try {
			    metrics = new Metrics(plugin);
			    metrics.start();
			    plugin.getLogger().info("Activated MCStats");
			} catch (IOException e) {
			    plugin.getLogger().warning("Failed to load MCStats: " + e.getMessage());
			}
		}
	}

	public void save()
	{
	}

	protected void clear()
	{
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		World world = event.getWorld();
		MagicWorld magicWorld = magicWorlds.get(world.getName());
		if (magicWorld == null) return;
		
		logger.info("Initializing world " + world.getName());
		magicWorld.installPopulators(world);
	}

    @EventHandler(priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.DEFAULT) return;
        
        MagicWorld magicWorld = magicWorlds.get(event.getLocation().getWorld().getName());
        if (magicWorld == null) return;
        
        LivingEntity entity = event.getEntity();
        LivingEntity replace =  magicWorld.processEntitySpawn(plugin, entity);
        if (replace != null) {
        	entity.setHealth(0);
            event.setCancelled(true);
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled() || !event.getEntity().hasMetadata("docile")) {
            return;
        }

        if (event.getReason() == TargetReason.CLOSEST_PLAYER ) {
            event.setCancelled(true);
        }
    }

    public Logger getLogger() {
    	return logger;
    }
    
    public MagicAPI getMagic() {
    	if (magicAPI == null) return null;
    	return magicAPI;
    }
    
    public WandChestPopulator getWandChestPopulator(String worldName) {
    	MagicWorld magicWorld = magicWorlds.get(worldName);
    	if (magicWorld == null) return null;
    	
    	return magicWorld.getWandChestPopulator();
    }
    
    public boolean isMagicEnabled()
    {
    	return magicAPI != null;
    }
    
	/*
	 * Private data
	 */

    private MagicAPI magicAPI = null;
    
    private final Map<String, MagicWorld> magicWorlds = new HashMap<String, MagicWorld>();
    private final Plugin	plugin;
	private final Logger 	logger;
	 
	private int								 metricsLevel					= 5;
	private Metrics							 metrics						= null;
}
