package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.magicworlds.MagicMob;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.magicworlds.spawn.SpawnRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class ReplaceRule extends SpawnRule {
    protected MagicMob replaceWith;

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

		replaceWith = controller.getMob(parameters.getString("type"));
		if (replaceWith == null) {
			replaceWith = new MagicMob(controller, parameters);
		}
    	
    	controller.getLogger().info(" Replacing: " + targetEntityType.name() + " at y > " + minY
				+ " with " + replaceWith.describe() + " at a " + (percentChance * 100) + "% chance");
    	return true;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
		return replaceWith.spawn(entity.getLocation());
    }
}
