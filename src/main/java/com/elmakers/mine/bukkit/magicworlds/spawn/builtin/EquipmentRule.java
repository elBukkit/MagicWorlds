package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import com.elmakers.mine.bukkit.magicworlds.spawn.SpawnRule;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class EquipmentRule extends SpawnRule {
    protected MaterialAndData itemToHold;

    @Override
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
    	if (!super.load(key, parameters, controller)) return false;
		itemToHold = ConfigurationUtils.getMaterialAndData(parameters, "item");
		String itemDescription = itemToHold == null ? "(Nothing)" : itemToHold.getName();
    	controller.getLogger().info(" Replacing held item of : " + targetEntityType.name() + " at y > " + minY
				+ " with " + itemDescription + " with a " + (percentChance * 100) + "% chance");
    	return true;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
		entity.getEquipment().setItemInHand(itemToHold.getItemStack(1));
        return null;
    }
}
