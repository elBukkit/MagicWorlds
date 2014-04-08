package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.MagicWorldsController;

public abstract class MagicSpawnHandler {
	private final Set<SpawnRule> spawnRules = new TreeSet<SpawnRule>();
	protected MagicWorldsController controller;
	
	public void load(MagicWorldsController controller, ConfigurationSection configuration) {
		this.controller = controller;
		onLoad(configuration);
	}
	
	public LivingEntity process(Plugin plugin, LivingEntity entity) {
        for (SpawnRule rule : spawnRules) {
        	LivingEntity result = rule.process(plugin, entity);
        	if (entity != null) return result;
        }
		return null;
    }
	
	protected void addRule(SpawnRule rule) {
		spawnRules.add(rule);
	}
	
	public abstract void onLoad(ConfigurationSection configuration);
}
