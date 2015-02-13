package com.elmakers.mine.bukkit.magicworlds.listener;

import com.elmakers.mine.bukkit.magicworlds.MagicWorld;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.Plugin;

public class EntitySpawnListener implements Listener
{
    private final MagicWorldsController controller;

	public EntitySpawnListener(final MagicWorldsController controller)
	{
        this.controller = controller;
	}

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.DEFAULT) return;
        
        MagicWorld magicWorld = controller.getWorld(event.getLocation().getWorld().getName());
        if (magicWorld == null) return;
        
        LivingEntity entity = event.getEntity();
        Plugin plugin = controller.getPlugin();
        LivingEntity replace =  magicWorld.processEntitySpawn(plugin, entity);
        if (replace != null) {
        	entity.setHealth(0);
            event.setCancelled(true);
    	}
    }
}
