package com.elmakers.mine.bukkit.magicworlds.populator.builtin;

import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicChunkPopulator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.utility.RandomUtils;
import com.elmakers.mine.bukkit.utility.WeightedPair;
import org.bukkit.inventory.ItemStack;

public class MagicChestPopulator extends MagicChunkPopulator {
    private final LinkedList<WeightedPair<Integer>> baseProbability = new LinkedList<WeightedPair<Integer>>();
    private final LinkedList<WeightedPair<String>> itemProbability = new LinkedList<WeightedPair<String>>();
    private int maxY = 255;
    private int minY = 0;

    public boolean onLoad(ConfigurationSection config) {
        if (!controller.isMagicEnabled()) return false;

        baseProbability.clear();
        itemProbability.clear();

        maxY = config.getInt("max_y", maxY);
        minY = config.getInt("min_y", minY);

        // Fetch base probabilities
        Float currentThreshold = 0.0f;
        ConfigurationSection base = config.getConfigurationSection("base_probability");
        if (base != null) {
            Set<String> keys = base.getKeys(false);
            for (String key : keys) {
                Integer wandCount = Integer.parseInt(key);
                Float threshold = (float)base.getDouble(key, 0);
                currentThreshold += threshold;
                baseProbability.add(new WeightedPair<Integer>(currentThreshold, wandCount));
            }
        }

        // Fetch wand probabilities
        currentThreshold = 0.0f;
        ConfigurationSection wands = config.getConfigurationSection("item_probability");
        if (wands != null) {
            Set<String> keys = wands.getKeys(false);
            for (String key : keys) {
                Float threshold = (float)wands.getDouble(key, 0);
                currentThreshold += threshold;
                itemProbability.add(new WeightedPair<String>(currentThreshold, key));
            }
        }

        return baseProbability.size() > 0 && itemProbability.size() > 0;
    }

    protected String[] populateChest(Chest chest) {
        // First determine how many wands to add
        MagicAPI magic = controller.getMagic();
        if (magic == null) {
            controller.getLogger().info("Tried to populate chest, but don't have a reference to Magic");
            return new String[0];
        }
        Integer wandCount = RandomUtils.weightedRandom(baseProbability);
        String[] wandNames = new String[wandCount];
        for (int i = 0; i < wandCount; i++) {
            String wandName = RandomUtils.weightedRandom(itemProbability);
            ItemStack item = magic.createItem(wandName);
            if (item != null) {
                chest.getInventory().addItem(item);
            } else {
                wandName = "*" + wandName;
            }
            wandNames[i] = wandName;
        }

        return wandNames;
    }

    public void setMaxY(int maxy) {
        this.maxY = maxy;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {

        BlockState[] tiles = chunk.getTileEntities();
        for (BlockState block : tiles) {
            if (block.getType() != Material.CHEST || !(block instanceof Chest)) continue;
            if (block.getY() < minY || block.getY() > maxY) continue;

            Chest chest = (Chest)block;
            if (block.getType() == Material.CHEST) {
                String[] wandNames = populateChest(chest);
                if (wandNames.length > 0 && controller != null) {
                    Location location = block.getLocation();
                    controller.getLogger().info("Added items to chest: " + StringUtils.join(wandNames, ", ") + " at "
                            + location.getWorld().getName() + "," + location.toVector());
                }
            }
        }
    }
}
