package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magic.MagicController;
import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public class CastRule extends SpawnRule {
	protected final EntityType 	entityType;
	protected final MagicWorldsController controller;
	protected final String			spellName;
    protected final String[] 		parameters;

    public CastRule(int rank, float percentChance, EntityType mobType, MagicWorldsController controller, String spellName, String[] parameters, int minY)
    {
    	super(rank, percentChance, minY);
    	this.entityType = mobType;
    	this.spellName = spellName;
    	this.controller = controller;
    	this.parameters = parameters;
    }
    
    @Override
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
    	if (entity.getType() != entityType) return null;
    	
    	controller.getLogger().info(" Spawn rule casting: " + spellName + " at " + entity.getLocation().toVector());
    	
    	String[] standardParameters = {
        	"pworld", entity.getLocation().getWorld().getName(), 
    		"px", Integer.toString(entity.getLocation().getBlockX()), 
    		"py", Integer.toString(entity.getLocation().getBlockY()), 
    		"pz", Integer.toString(entity.getLocation().getBlockZ()), 
    		"target", "self"
    	};
    	String[] fullParameters = new String[parameters.length + standardParameters.length];
    	for (int index = 0; index < standardParameters.length; index++) {
    		fullParameters[index] = standardParameters[index];
		
    	}
    	for (int index = 0; index < parameters.length; index++) {
    		fullParameters[index  + standardParameters.length] = parameters[index];
    	}
    	
    	MagicController magicController = controller.getMagicController();
    	if (magicController != null) {
    		magicController.cast(null, spellName, fullParameters, null, null);
    	}
    	
    	// This will always end rule processing, so make sure to put them last.
    	return entity;
    }
}
