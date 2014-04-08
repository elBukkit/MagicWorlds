package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magic.MagicController;

public class MagicWorldsController implements Listener 
{
	public MagicWorldsController(final Plugin plugin)
	{
		this.logger = plugin.getLogger();
		this.plugin = plugin;
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
			Configuration config = plugin.getConfig();
			ConfigurationSection worlds = config.getConfigurationSection("worlds");
			if (worlds != null) {
				Set<String> worldKeys = worlds.getKeys(false);
				for (String worldName : worldKeys) {
					logger.info("Customizing world " + worldName);
					MagicWorld world = new MagicWorld(this, worlds.getConfigurationSection(worldName));
					magicWorlds.put(worldName, world);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
    
    public MagicController getMagicController() {
    	return magicController;
    }
    
	/*
	 * Private data
	 */

    // TODO: Magic integration
    private MagicController magicController = null;
    
    private final Map<String, MagicWorld> magicWorlds = new HashMap<String, MagicWorld>();
    private final Plugin	plugin;
	private final Logger 	logger;
}
