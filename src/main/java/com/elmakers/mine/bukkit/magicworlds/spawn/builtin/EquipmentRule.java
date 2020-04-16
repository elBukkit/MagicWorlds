package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.entity.EntityData;
import com.elmakers.mine.bukkit.magicworlds.spawn.SpawnRule;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public class EquipmentRule extends SpawnRule {
    protected EntityData equipment;

    @Override
    public void finalizeLoad(String worldName)
    {
        equipment = new EntityData(controller.getMagic().getController(), parameters);
        controller.getLogger().info(" Replacing equipment of : " + getTargetEntityTypeName() + " at y > " + minY
                + " at a " + (percentChance * 100) + "% chance");
    }

    protected void setEquipment(LivingEntity entity) {
        equipment.copyEquipmentTo(entity);
    }

    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
        setEquipment(entity);
        return null;
    }
}
