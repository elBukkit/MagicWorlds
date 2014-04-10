package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public abstract class SpawnRule implements Comparable<SpawnRule> {
	static private int autoRank = 0;
	
    private float        	percentChance;
    private int          	rank;
    
    protected static final Random rand = new Random();

    public SpawnRule()
    {
    	this(0, 1.0f);
    }
    
    public SpawnRule(int order, float percentChance)
    {
        this.rank = order > 0 ? order : --autoRank;
        this.percentChance = percentChance;
    }
    
    public SpawnRule(float percentChance)
    {
        this(0, percentChance);
    }

    public void setPercentChance(float percentChance)
    {
        this.percentChance = percentChance;
    }
    
    public float getPercentChange()
    {
    	return percentChance;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }
    
    public LivingEntity process(Plugin plugin, LivingEntity entity) {
    	if (entity.getType() == EntityType.HORSE) Bukkit.getLogger().info(" Processing spawn rule at " + percentChance);
    	
        if (percentChance < rand.nextFloat()) return null;
           	
    	return onProcess(plugin, entity);
    }

    public abstract LivingEntity onProcess(Plugin plugin, LivingEntity entity);
    
	@Override
	public int compareTo(SpawnRule other) {
		return this.rank - other.rank;
	}
}
