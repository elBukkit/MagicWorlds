package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class SpawnRule {
	protected final EntityType 	entityType;
    protected EntityType 	replaceWith;
    protected float        	percentChance;
    protected int          	rank;
    protected boolean		docile;
    
    protected static final Random rand = new Random();

    public SpawnRule(int order, EntityType mobType, EntityType replaceWith, float percentChance)
    {
        this.rank = order;
        this.entityType = mobType;
        this.replaceWith = replaceWith;
        this.percentChance = percentChance;
    }

    public SpawnRule(EntityType mobType, EntityType replaceWith)
    {
        this(0, mobType, replaceWith, 1);
    }

    public SpawnRule(EntityType mobType, EntityType replaceWith, float percentChance)
    {
        this(0, mobType, replaceWith, percentChance);
    }

    public EntityType getType()
    {
        return entityType;
    }

    public float getPercentChance()
    {
        return percentChance;
    }

    public int getRank()
    {
        return rank;
    }
    
    public EntityType getReplaceWith()
    {
        return replaceWith;
    }

    public void setPercentChance(float percentChance)
    {
        this.percentChance = percentChance;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }
    
    public LivingEntity replace(Plugin plugin, LivingEntity entity) {
    	if (entity.getType() != entityType) return null;
        if (percentChance < rand.nextFloat()) return null;
        Entity result = entity.getWorld().spawnEntity(entity.getLocation(), replaceWith);
        entity.setMetadata("docile", new FixedMetadataValue(plugin, true));
        return result instanceof LivingEntity ? (LivingEntity)result : null;
    }
}
