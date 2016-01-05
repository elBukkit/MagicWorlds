package com.elmakers.mine.bukkit.magicworlds.spawn.builtin;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;
import org.bukkit.configuration.ConfigurationSection;
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

public class ReplaceRule extends EquipmentRule {
    protected EntityType 		replaceWith;
    protected String			entitySubType;
    protected boolean			docile;

    @Override
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
    	if (!super.load(key, parameters, controller)) return false;
    	String entityName = parameters.getString("replace_type");
    	entitySubType = parameters.getString("replace_sub_type");

    	replaceWith = parseEntityType(entityName);
		if (replaceWith == null) {
			this.controller.getLogger().warning(" Invalid entity type: " + entityName);
			return false;
		}
    	
		String subTypeDescription = entitySubType == null || entitySubType.length() == 0 ? "" : ":" + entitySubType;
    	controller.getLogger().info(" Replacing: " + targetEntityType.name() + " at y > " + minY
				+ " with " + replaceWith.name() + subTypeDescription + " with a " + (percentChance * 100) + "% chance");
    	return true;
    }
    
    public EntityType getReplaceWith()
    {
        return replaceWith;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
        Entity result = entity.getWorld().spawnEntity(entity.getLocation(), replaceWith);
        result.setMetadata("docile", new FixedMetadataValue(plugin, true));
        
        if (entitySubType != null && entitySubType.length() > 0) {
   			try {
	           	switch (replaceWith) {
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
		if (!(result instanceof LivingEntity)) {
			return null;
		}
		LivingEntity li = (LivingEntity)result;
		setEquipment(li);
        return li;
    }
}
