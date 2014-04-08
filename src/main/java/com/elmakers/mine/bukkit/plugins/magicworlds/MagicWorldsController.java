package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.populator.ReplacePopulator;

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
		load();
	}
	
	public void load()
	{
		try {
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
		// Install our block populator if configured to do so.
		/*
		 * 
		 * return new WandChestPopulator(this, blockPopulatorConfig);
		 * 
		if (blockPopulatorEnabled && blockPopulatorConfig == null) {
			logger.warning("Block populator is enabled, but missing config");
		}
		if (blockPopulatorEnabled && blockPopulatorConfig != null) {
			World world = event.getWorld();
			world.getPopulators().add(getWandChestPopulator());
			logger.info("Installing chest populator in " + world.getName());
		}
		*/
		
		World world = event.getWorld();
		world.getPopulators().add(new ReplacePopulator());
		
		logger.info("Installing replace populator in " + world.getName());
		

		List<SpawnRule> rules = new ArrayList<SpawnRule>();
		rules.add(new SpawnRule(EntityType.SHEEP, EntityType.ENDERMAN));
		rules.add(new SpawnRule(EntityType.COW, EntityType.ENDERMAN));
		rules.add(new SpawnRule(EntityType.PIG, EntityType.ENDERMAN));
		rules.add(new SpawnRule(EntityType.CREEPER, EntityType.ENDERMAN));
		rules.add(new SpawnRule(EntityType.SPIDER, EntityType.ENDERMAN));
		rules.add(new SpawnRule(EntityType.SKELETON, EntityType.ENDERMAN));

		logger.info("Overriding mob spawning in " + world.getName());
		
		worldSpawnRules.put(world.getName(), rules);
	}

    @EventHandler(priority = EventPriority.LOW)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        List<SpawnRule> rules = worldSpawnRules.get(event.getLocation().getWorld().getName());
        if (rules == null) return;
        
        LivingEntity entity = (LivingEntity)event.getEntity();
        for (SpawnRule rule : rules)
        {
        	LivingEntity replace = rule.replace(plugin, entity);
        	if (replace != null) {
                entity.setHealth(0);
                event.setCancelled(true);
                break;
        	}
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

	/*
	 * Private data
	 */

    private final Plugin	plugin;
	private final Logger 	logger;
	private final Map<String, List<SpawnRule>> worldSpawnRules = new HashMap<String, List<SpawnRule>>();
}
