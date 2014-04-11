package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class SpawnReplaceRule extends SpawnRule {
	protected final EntityType 	entityType;
    protected EntityType 	replaceWith;
    protected String		entitySubType;
    protected boolean		docile;

    public SpawnReplaceRule(int rank, float percentChance, EntityType mobType, EntityType replaceWith, String entitySubType)
    {
    	super(rank, percentChance);
        this.entityType = mobType;
        this.replaceWith = replaceWith;
        this.entitySubType = entitySubType;
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
        
        if (entitySubType.length() > 0) {
   			try {
	           	switch (entityType) {
	        	case HORSE:
	        		if (result instanceof Horse) {
	        			Horse horse = (Horse)result;
	        			try {
	        				horse.setVariant(Variant.valueOf(entitySubType.toUpperCase()));
	        			} catch (Throwable ex) {
	        				ex.printStackTrace();
	        			}
	        		}
	        		break;
		        case SKELETON:
		    		if (result instanceof Skeleton) {
		    			Skeleton skeleton = (Skeleton)result;
		    			skeleton.setSkeletonType(SkeletonType.valueOf(entitySubType.toUpperCase()));
		    		}
		    		break;
		        case OCELOT:
		    		if (result instanceof Ocelot) {
		    			Ocelot ocelot = (Ocelot)result;
		    			ocelot.setCatType(Type.valueOf(entitySubType.toUpperCase()));
		    		}
		    		break;
		    	default:
		    	}
			} catch (Throwable ex) {
			}
        }
        return result instanceof LivingEntity ? (LivingEntity)result : null;
    }
}
