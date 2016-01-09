package com.elmakers.mine.bukkit.magicworlds.listener;

import com.elmakers.mine.bukkit.magicworlds.MagicMob;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener
{
    private final MagicWorldsController controller;

	public EntityDeathListener(final MagicWorldsController controller)
	{
        this.controller = controller;
	}

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        org.bukkit.Bukkit.getLogger().info("DIED: " + entity.getType());
        if (!(entity instanceof LivingEntity))
        {
            return;
        }

        LivingEntity died = (LivingEntity)entity;
        String name = died.getCustomName();
        if (name == null || name.isEmpty())
        {
            return;
        }

        MagicMob mob = controller.getMobByName(name);
        if (mob == null) return;

        org.bukkit.Bukkit.getLogger().info("MODIFYING DROPS!");
        mob.modifyDrops(event);
    }
}
