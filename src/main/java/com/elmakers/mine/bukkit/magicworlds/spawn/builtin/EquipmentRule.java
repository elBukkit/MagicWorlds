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
    protected MaterialAndData helmet;
    protected MaterialAndData chestplate;
    protected MaterialAndData leggings;
    protected MaterialAndData boots;

    @Override
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
    	if (!super.load(key, parameters, controller)) return false;
		itemToHold = ConfigurationUtils.getMaterialAndData(parameters, "item");
        helmet = ConfigurationUtils.getMaterialAndData(parameters, "helmet");
        chestplate = ConfigurationUtils.getMaterialAndData(parameters, "chestplate");
        leggings = ConfigurationUtils.getMaterialAndData(parameters, "leggings");
        boots = ConfigurationUtils.getMaterialAndData(parameters, "boots");
		String itemDescription = itemToHold == null ? "(Nothing)" : itemToHold.getName();
    	controller.getLogger().info(" Replacing held item of : " + targetEntityType.name() + " at y > " + minY
				+ " with " + itemDescription + " with a " + (percentChance * 100) + "% chance");
    	return true;
    }

    protected void setEquipment(LivingEntity entity) {
        if (itemToHold != null) {
            entity.getEquipment().setItemInHand(itemToHold.getItemStack(1));
        }
        if (helmet != null) {
            entity.getEquipment().setHelmet(helmet.getItemStack(1));
        }
        if (chestplate != null) {
            entity.getEquipment().setChestplate(chestplate.getItemStack(1));
        }
        if (leggings != null) {
            entity.getEquipment().setLeggings(leggings.getItemStack(1));
        }
        if (boots != null) {
            entity.getEquipment().setBoots(boots.getItemStack(1));
        }
    }

    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
        setEquipment(entity);
        return null;
    }
}
