package com.elmakers.mine.bukkit.plugins.magicworlds.entities;

import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class ReplaceSpawnHandler extends MagicSpawnHandler {
	
	public void onLoad(ConfigurationSection config) {
		ConfigurationSection replaceList = config.getConfigurationSection("replace");
		for (Entry<String, Object> replaceEntry : replaceList.getValues(false).entrySet()) {
			try {
				int priority = 0;
				float percentChance = 1.0f;
				
				String entitySubType = "";
				String entityName = replaceEntry.getValue().toString();
				if (entityName.contains("@")) {
					String[] pieces = StringUtils.split(entityName, "@");
					entityName = pieces[0];
					if (pieces.length > 0) {
						String probabilityString = pieces[1];
						if (probabilityString.contains(",")) {
							pieces = StringUtils.split(probabilityString, ",");
							probabilityString = pieces[0];
							if (pieces.length > 0) {
								try {
									priority = Integer.parseInt(pieces[1]);
								} catch (Exception ex) {
									
								}
							}
						}
						try {
							percentChance = Float.parseFloat(probabilityString);
						} catch (Exception ex) {
							
						}
					}
				}
				
				if (entityName.contains(":")) {
					String[] pieces = StringUtils.split(entityName, ":");
					entityName = pieces[0];
					if (pieces.length > 1) {
						entitySubType = pieces[1];
					}
				} else if (entityName.contains("|")) {
					String[] pieces = StringUtils.split(entityName, "|");
					entityName = pieces[0];
					if (pieces.length > 1) {
						entitySubType = pieces[1];
					}
				}
				
				String fromEntityName = replaceEntry.getKey();
				String[] pieces = StringUtils.split(entityName, ",");
				if (pieces != null && pieces.length > 1) {
					fromEntityName = pieces[0];
					entityName = pieces[1];
				}
				
				int minY = 0;
				if (fromEntityName.contains(">")) {
					pieces = StringUtils.split(fromEntityName, ">");
					if (pieces != null && pieces.length > 1) {
						fromEntityName = pieces[0];
						try {
							minY = Integer.parseInt(pieces[1]);
						} catch (Exception ex) {
							
						}
					}
				}
				
				EntityType fromType = parseEntityType(fromEntityName);
				if (fromType == null) {
					controller.getLogger().warning(" Invalid entity type: " + fromEntityName);
					return;
				}
				
				if (entityName.equals("spell")) {
					String spellName = entitySubType;
					String[] parameters = new String[0];
					if (spellName.contains(" ")) {
						pieces = spellName.split(" ");
						spellName = pieces[0];
						parameters = new String[pieces.length - 1];
						for (int i = 1; i < pieces.length; i++) {
							parameters[i - 1] = pieces[i];
						}
					}
					
					addRule(new CastRule(priority, percentChance, fromType, controller, spellName, parameters, minY));
					controller.getLogger().info(" Casting: " + entitySubType + " on " + fromType.name() + " at y > " + minY  
						+ " with a " + (percentChance * 100) + "% chance");
				} else {
					EntityType toType = parseEntityType(entityName);
					if (toType == null) {
						controller.getLogger().warning(" Invalid entity type: " + entityName);
						return;
					}
					addRule(new SpawnReplaceRule(priority, percentChance, fromType, toType, entitySubType, minY));
					controller.getLogger().info(" Replacing: " + fromType.name() + " at y > " + minY
							+ " with " + toType.name() + ":" + entitySubType + " with a " + (percentChance * 100) + "% chance");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
