package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class ReplaceSpawnHandler extends MagicSpawnHandler {
	
	@SuppressWarnings("deprecation")
	public void onLoad(ConfigurationSection config) {
		ConfigurationSection replaceList = config.getConfigurationSection("replace");
		for (Entry<String, Object> replaceEntry : replaceList.getValues(false).entrySet()) {
			try {
				EntityType fromType = EntityType.fromName(replaceEntry.getKey());
				EntityType toType = EntityType.fromName(replaceEntry.getValue().toString());
				addRule(new SpawnReplaceRule(fromType, toType));
				controller.getLogger().info(" Replacing: " + fromType.name() + " with " + toType.name());
			} catch (Exception ex) {
				controller.getLogger().warning(" Invalid entity type: " + replaceEntry.getKey() + " => " + replaceEntry.getValue());
			}
		}
	}
}
