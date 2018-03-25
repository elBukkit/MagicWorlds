package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.magicworlds.spawn.SpawnRule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class ReplaceRule extends SpawnRule {
    protected EntityData replaceWith;

    @Override
    public void finalizeLoad(String worldName)
    {
        // Legacy support
        if (!parameters.contains("type")) {
            parameters.set("type", parameters.get("replace_type"));
        }
        if (!parameters.contains("sub_type")) {
            parameters.set("sub_type", parameters.get("replace_sub_type"));
        }

        MageController mageController = controller.getMagic().getController();
        replaceWith = mageController.getMob(parameters.getString("type"));
        if (replaceWith == null) {
            replaceWith = mageController.loadMob(parameters);
        }

        if (replaceWith == null) {
            controller.getLogger().warning("Error reading in configuration for custom mob in " + worldName);
            return;
        }
        String message = " Replacing: " + targetEntityType.name() + " in " + worldName + " at y > " + minY
                + " with " + replaceWith.describe() + " at a " + (percentChance * 100) + "% chance";

        if (tags != null) {
            message = message + " in regions tagged with any of " + tags.toString();
        }
        controller.getLogger().info(message);
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
        if (replaceWith == null) return null;

        // This makes replacing the same type of mob have better balance,
        // particularly with mob spawners
        if (entity.getType() == replaceWith.getType()) {
            replaceWith.modify(controller.getMagic().getController(), entity);
            return entity;
        }

        Entity spawned = replaceWith.spawn(controller.getMagic().getController(), entity.getLocation());
        return spawned instanceof LivingEntity ? (LivingEntity)spawned : null;
    }
}
