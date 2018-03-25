package com.elmakers.mine.bukkit.magicworlds.listener;

import com.elmakers.mine.bukkit.magicworlds.MagicWorld;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class EntitySpawnListener implements Listener
{
    private final MagicWorldsController controller;
    private boolean enabled = true;
    private boolean spawning = false;
    private Set<SpawnReason> ignoreReasons = new HashSet<SpawnReason>();

    public EntitySpawnListener(final MagicWorldsController controller, ConfigurationSection config)
    {
        this.controller = controller;
        List<String> reasonList = ConfigurationUtils.getStringList(config, "ignore_reasons");
        ignoreReasons.clear();
        if (reasonList != null) {
            for (String reason : reasonList) {
                try {
                    SpawnReason ignoreReason = SpawnReason.valueOf(reason.toUpperCase());
                    ignoreReasons.add(ignoreReason);
                } catch (Exception ex) {
                    controller.getLogger().warning("Invalid spawn reason in ignore_reasons: " + reason);
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (!enabled || spawning || ignoreReasons.contains(event.getSpawnReason())) return;
        
        MagicWorld magicWorld = controller.getWorld(event.getLocation().getWorld().getName());
        if (magicWorld == null) return;
        
        LivingEntity entity = event.getEntity();
        Plugin plugin = controller.getPlugin();
        spawning = true;
        try {
            LivingEntity replace =  magicWorld.processEntitySpawn(plugin, entity);
            if (replace != null && replace != entity) {
                entity.remove();
                event.setCancelled(true);
            }
        } catch(Exception ex) {
            controller.getLogger().log(Level.SEVERE, "Error replacing mob", ex);
        }
        spawning = false;
    }
}
