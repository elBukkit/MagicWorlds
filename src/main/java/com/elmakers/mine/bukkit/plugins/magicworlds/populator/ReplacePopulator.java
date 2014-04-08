package com.elmakers.mine.bukkit.plugins.magicworlds.populator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class ReplacePopulator extends BlockPopulator {

	public ReplacePopulator() {
	}
	
	@SuppressWarnings("deprecation")
	protected void replaceBlock(Block block) {
		switch (block.getType()) {
		case GRASS:
			block.setType(Material.MYCEL);
			break;
		case DIRT:
			block.setType(Material.NETHERRACK);
			break;
		case STONE:
			block.setType(Material.ENDER_STONE);
			break;
		case LEAVES:
			block.setType(Material.GLOWSTONE);
			break;
		case LOG:
			block.setType(Material.QUARTZ_BLOCK);
			break;
		case WATER:
			block.setTypeId(95);
			block.setData((byte)9);
			break;
		case STATIONARY_WATER:
			block.setTypeId(95);
			block.setData((byte)3);
			break;
		case LAVA:
			block.setTypeId(95);
			block.setData((byte)0xE);
			break;
		case STATIONARY_LAVA:
			block.setTypeId(95);
			block.setData((byte)0xE);
			break;
		case SAND:
			block.setType(Material.SOUL_SAND);
			break;
		case GRAVEL:
			block.setType(Material.HUGE_MUSHROOM_2);
		case LONG_GRASS:
			block.setType(Material.RED_MUSHROOM);
			break;
		case YELLOW_FLOWER:
			block.setType(Material.BROWN_MUSHROOM);
			break;
		case RED_ROSE:
			block.setType(Material.BROWN_MUSHROOM);
			break;
		case WOOD:
			block.setType(Material.BRICK);
			break;
		default:
			break;
		}
		
		switch (block.getTypeId()) {
		case 161: 
			block.setType(Material.GLOWSTONE); 
			break;
		case 162:
			block.setType(Material.QUARTZ_BLOCK);
			block.setData((byte)1);
			break;
		case 175:
			block.setType(Material.HUGE_MUSHROOM_1);
			break;
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
