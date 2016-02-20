package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.magicworlds.spawn.SpawnRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class ReplaceRule extends SpawnRule {
    protected EntityData replaceWith;

    @Override
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
    	if (!super.load(key, parameters, controller)) return false;

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
    	
    	controller.getLogger().info(" Replacing: " + targetEntityType.name() + " at y > " + minY
				+ " with " + replaceWith.describe() + " at a " + (percentChance * 100) + "% chance");
    	return true;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
		Entity spawned = replaceWith.spawn(entity.getLocation());
		return spawned instanceof LivingEntity ? (LivingEntity)spawned : null;
    }
}
