package com.elmakers.mine.bukkit.magicworlds.populator.builtin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import com.elmakers.mine.bukkit.magicworlds.populator.MagicBlockPopulator;

public class ReplacePopulator extends MagicBlockPopulator {
	private Map<Material, MaterialAndData> replaceMap = new HashMap<Material, MaterialAndData>();
	private int maxY = 128;
	private int minY = 3;
	
	@Override
	public boolean onLoad(ConfigurationSection config) {
		replaceMap.clear();
		
		maxY = config.getInt("max_y");
		if (maxY == 0) {
			maxY = 128;
		}
		minY = config.getInt("min_y");
		if (minY == 0) {
			minY = 3;
		}
		
		ConfigurationSection replaceSection = config.getConfigurationSection("replace");
		if (replaceSection == null) return false;
		Map<String, Object> replaceNodes = replaceSection.getValues(false);
		for (Entry<String, Object> replaceNode : replaceNodes.entrySet()) {
			MaterialAndData fromMaterial = new MaterialAndData(replaceNode.getKey());
			if (!fromMaterial.isValid()) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getKey());
				continue;
			}
			MaterialAndData toMaterial = new MaterialAndData(replaceNode.getValue().toString());
			if (!toMaterial.isValid()) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getValue());
				continue;
			}
			replaceMap.put(fromMaterial.getMaterial(), toMaterial);
		}
		
		return replaceMap.size() > 0;
	}
	
	protected void replaceBlock(Block block) {
		MaterialAndData replaceType = replaceMap.get(block.getType());
		if (replaceType != null) {
			try {
				replaceType.modify(block);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void populate(Block block, Random random) {
		if (block.getY() < minY || block.getY() > maxY) return;
		replaceBlock(block);
	}
}
