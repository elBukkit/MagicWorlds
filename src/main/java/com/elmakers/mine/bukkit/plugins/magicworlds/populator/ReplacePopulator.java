package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.blocks.MaterialAndData;
import com.elmakers.mine.bukkit.blocks.MaterialBrush;

public class ReplacePopulator extends MagicBlockPopulator {
	private Map<Material, MaterialAndData> replaceMap = new HashMap<Material, MaterialAndData>();
	
	@Override
	public void onLoad(ConfigurationSection config) {
		replaceMap.clear();
		
		ConfigurationSection replaceSection = config.getConfigurationSection("replace");
		if (replaceSection == null) return;
		Map<String, Object> replaceNodes = replaceSection.getValues(false);
		for (Entry<String, Object> replaceNode : replaceNodes.entrySet()) {
			MaterialAndData fromMaterial = MaterialBrush.parseMaterialKey(replaceNode.getKey());
			if (fromMaterial == null) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getKey());
				continue;
			}
			MaterialAndData toMaterial = MaterialBrush.parseMaterialKey(replaceNode.getValue().toString());
			if (toMaterial == null) {
				controller.getLogger().warning("Invalid material key: " + replaceNode.getValue());
				continue;
			}
			replaceMap.put(fromMaterial.getMaterial(), toMaterial);
		}
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
	public void populate(World world, Random random, Chunk chunk) {
		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				for (int y = 0; y <= 255; y++) {
					replaceBlock(chunk.getBlock(x, y, z));
				}
			}
		}
	}
}
