package com.elmakers.mine.bukkit.magicworlds.spawn;

import java.util.Random;

import com.elmakers.mine.bukkit.entity.EntityData;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;

public abstract class SpawnRule implements Comparable<SpawnRule> {
	protected String					key;
	protected EntityType 				targetEntityType;
	protected float        				percentChance;
	protected int						minY;
	protected int						maxY;
    protected int                       cooldown;
    protected long                      lastSpawn;
	protected boolean					allowIndoors;
    protected MagicWorldsController	    controller;
    protected ConfigurationSection      parameters;
    
    protected static final Random rand = new Random();

    public abstract LivingEntity onProcess(Plugin plugin, LivingEntity entity);

    public SpawnRule()
    {
    }
    
    public void finalizeLoad(String worldName)
    {
        
    }
    
    public boolean load(String key, ConfigurationSection parameters, MagicWorldsController controller)
    {
        this.parameters = parameters;
    	this.key = key;
    	this.controller = controller;
    	String entityTypeName = parameters.getString("target_type");
    	this.targetEntityType = EntityData.parseEntityType(entityTypeName);
		if (targetEntityType == null) {
			this.controller.getLogger().warning(" Invalid entity type: " + entityTypeName);
			return false;
		}
		this.allowIndoors = parameters.getBoolean("allow_indoors", true);
		this.minY = parameters.getInt("min_y", 0);
		this.maxY = parameters.getInt("max_y", 255);
		this.percentChance = (float)parameters.getDouble("probability", 1.0);
        this.cooldown = parameters.getInt("cooldown", 0);
		
		return true;
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
        long now = System.currentTimeMillis();
        if (cooldown > 0 && lastSpawn != 0 && now < lastSpawn + cooldown) return null;
        lastSpawn = now;
        Location entityLocation = entity.getLocation();
        int y = entityLocation.getBlockY();
        if (y < minY || y > maxY) return null;
        if (!this.allowIndoors) {
        	// Bump it up two to miss things like tall grass
            y += 3;
            int x = entityLocation.getBlockX();
            int z = entityLocation.getBlockZ();
            Chunk chunk = entityLocation.getChunk();
            int maxY = entityLocation.getWorld().getMaxHeight();
            while (y <= maxY)
            {
                Block block = chunk.getBlock(x, y, z);
                if (block != null && block.getType() != Material.AIR) {
                    return null;
                }
                y++;
            }
        }
           	
    	return onProcess(plugin, entity);
    }
    
	@Override
	public int compareTo(SpawnRule other) 
	{
		return this.key.compareTo(other.key);
	}
}
