package com.elmakers.mine.bukkit.plugins.magicworlds.spawn;

import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public abstract class SpawnRule implements Comparable<SpawnRule> {
	protected String					key;
	protected EntityType 				targetEntityType;
	protected float        				percentChance;
	protected int						minY;
	protected int						maxY;
    protected MagicWorldsController	controller;
    
    protected static final Random rand = new Random();

    public abstract LivingEntity onProcess(Plugin plugin, LivingEntity entity);

    public SpawnRule()
    {
    }
    
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
    	this.key = key;
    	this.controller = controller;
    	String entityTypeName = parameters.getString("target_type");
    	this.targetEntityType = parseEntityType(entityTypeName);
		if (targetEntityType == null) {
			this.controller.getLogger().warning(" Invalid entity type: " + entityTypeName);
			return false;
		}
		this.minY = parameters.getInt("min_y", 0);
		this.maxY = parameters.getInt("max_y", 255);
		this.percentChance = (float)parameters.getDouble("probability", 1.0);
		
		return true;
    }
	
	@SuppressWarnings("deprecation")
	public static EntityType parseEntityType(String typeString) 
	{
		if (typeString.equalsIgnoreCase("horse")) 
		{
			return EntityType.HORSE;
		}
		
		return EntityType.fromName(typeString);
	}

    public void setPercentChance(float percentChance)
    {
        this.percentChance = percentChance;
    }
    
    public float getPercentChance()
    {
    	return percentChance;
    }
    
    public EntityType getTargetType() 
    {
    	return targetEntityType;
    }
    
    public String getKey()
    {
    	return key;
    }
    
    public LivingEntity process(Plugin plugin, LivingEntity entity) 
    {
    	if (targetEntityType != entity.getType()) return null;
        if (percentChance < rand.nextFloat()) return null;
        int y = entity.getLocation().getBlockY();
        if (y < minY || y > maxY) return null;
           	
    	return onProcess(plugin, entity);
    }
    
	@Override
	public int compareTo(SpawnRule other) 
	{
		return this.key.compareTo(other.key);
	}
}
