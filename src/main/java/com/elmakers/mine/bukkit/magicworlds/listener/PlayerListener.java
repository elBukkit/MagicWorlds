package com.elmakers.mine.bukkit.magicworlds.listener;

import com.elmakers.mine.bukkit.magicworlds.MagicWorld;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
    private final MagicWorldsController controller;
    private boolean enabled = true;

    public PlayerListener(final MagicWorldsController controller)
    {
        this.controller = controller;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!enabled) return;
        
        Player player = event.getPlayer();
        MagicWorld magicWorld = controller.getWorld(player.getWorld().getName());
        if (magicWorld == null) return;
        
        magicWorld.playerEntered(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        MagicWorld magicWorld = controller.getWorld(player.getWorld().getName());
        if (magicWorld == null) return;

        magicWorld.playerEntered(player);
    }
}
