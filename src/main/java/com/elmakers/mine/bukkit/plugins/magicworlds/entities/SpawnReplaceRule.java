package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class SpawnReplaceRule extends SpawnRule {
	protected final EntityType 	entityType;
    protected EntityType 	replaceWith;
    protected boolean		docile;
    
    protected static final Random rand = new Random();

    public SpawnReplaceRule(EntityType mobType, EntityType replaceWith)
    {
        this.entityType = mobType;
        this.replaceWith = replaceWith;
    }

    public EntityType getType()
    {
        return entityType;
    }
    
    public EntityType getReplaceWith()
    {
        return replaceWith;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
    	if (entity.getType() != entityType) return null;
        Entity result = entity.getWorld().spawnEntity(entity.getLocation(), replaceWith);
        result.setMetadata("docile", new FixedMetadataValue(plugin, true));
        return result instanceof LivingEntity ? (LivingEntity)result : null;
    }
}
