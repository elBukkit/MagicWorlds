package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public abstract class SpawnRule implements Comparable<SpawnRule> {
	static private int autoRank = 0;
	
    private float        	percentChance;
    private int          	rank;
    private int				minY;
    
    protected static final Random rand = new Random();

    public SpawnRule()
    {
    	this(0, 1.0f, 0);
    }
    
    public SpawnRule(int order, float percentChance, int minY)
    {
        this.minY = minY;
        this.rank = order > 0 ? order : --autoRank;
        this.percentChance = percentChance;
    }
    
    public SpawnRule(float percentChance)
    {
        this(0, percentChance, 0);
    }

    public void setPercentChance(float percentChance)
    {
        this.percentChance = percentChance;
    }
    
    public float getPercentChance()
    {
    	return percentChance;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }
    
    public LivingEntity process(Plugin plugin, LivingEntity entity) {
        if (percentChance < rand.nextFloat()) return null;
        if (entity.getLocation().getY() < minY) return null;
           	
    	return onProcess(plugin, entity);
    }

    public abstract LivingEntity onProcess(Plugin plugin, LivingEntity entity);
    
	@Override
	public int compareTo(SpawnRule other) {
		return this.rank - other.rank;
	}
}
