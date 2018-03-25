package com.elmakers.mine.bukkit.magicworlds.spawn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.magicworlds.MagicWorldsController;

public class MagicSpawnHandler {
    public final static String BUILTIN_CLASSPATH = "com.elmakers.mine.bukkit.magicworlds.spawn.builtin";

    private final Map<EntityType, Set<SpawnRule>> entityTypeMap = new HashMap<EntityType, Set<SpawnRule>>();
    private final Map<String, SpawnRule> spawnRules = new TreeMap<String, SpawnRule>();
    protected MagicWorldsController controller;
    protected String worldName;
    protected ConfigurationSection configuration;

    public void clear() {
        entityTypeMap.clear();
        spawnRules.clear();
    }

    public LivingEntity process(Plugin plugin, LivingEntity entity) {
        Set<SpawnRule> entityRules = entityTypeMap.get(entity.getType());
        if (entityRules == null) return null;
        for (SpawnRule rule : entityRules) {
            LivingEntity result = rule.process(plugin, entity);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected void addRule(SpawnRule rule) {
        spawnRules.put(rule.getKey(), rule);
        Set<SpawnRule> entityRules = entityTypeMap.get(rule.getTargetType());
        if (entityRules == null) {
            entityRules = new TreeSet<SpawnRule>();
            entityTypeMap.put(rule.getTargetType(), entityRules);
        }
        entityRules.add(rule);
    }

    public void load(String worldName, ConfigurationSection config, MagicWorldsController controller) {
        this.controller = controller;
        this.worldName = worldName;
        this.configuration = config;
        for (String key : config.getKeys(false)) {
            ConfigurationSection handlerConfig = config.getConfigurationSection(key);
            String className = handlerConfig.getString("class");
            SpawnRule handler = spawnRules.get(key);
            spawnRules.remove(key);
            if (handler == null) {
                handler = createSpawnRule(className);
            } else {
                Set<SpawnRule> entityRules = entityTypeMap.get(handler.getTargetType());
                if (entityRules != null) {
                    entityRules.remove(handler);
                }
            }
            if (handler != null) {
                if (handler.load(key, handlerConfig, controller)) {
                    addRule(handler);
                }
            }
        }
    }

    public void finalizeLoad() {
        for (SpawnRule rule : spawnRules.values()) {
            rule.finalizeLoad(worldName);
        }
    }

    protected SpawnRule createSpawnRule(String className)
    {
        if (className == null) return null;

        if (className.indexOf('.') <= 0)
        {
            className = BUILTIN_CLASSPATH + "." + className;
        }

        Class<?> handlerClass = null;
        try
        {
            handlerClass = Class.forName(className);
        }
        catch (Throwable ex)
        {
            controller.getLogger().warning("Error loading handler: " + className + ", " + ex.getMessage());
            return null;
        }

        Object newObject;
        try
        {
            newObject = handlerClass.newInstance();
        }
        catch (Throwable ex)
        {
            controller.getLogger().warning("Error loading handler: " + className);
            ex.printStackTrace();
            return null;
        }

        if (newObject == null || !(newObject instanceof SpawnRule))
        {
            controller.getLogger().warning("Error loading handler: " + className + ", does it extend SpawnRule?");
            return null;
        }

        return (SpawnRule)newObject;
    }

}
