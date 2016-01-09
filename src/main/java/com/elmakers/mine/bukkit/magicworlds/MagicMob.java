package com.elmakers.mine.bukkit.magicworlds;

import com.elmakers.mine.bukkit.block.MaterialAndData;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MagicMob {
    private final MagicWorldsController controller;

    protected EntityType        entityType;
    protected double 			health;
    protected String 			name;

    protected Horse.Variant     horseVariant;
    protected Skeleton.SkeletonType skeletonType;
    protected Ocelot.Type       ocelotType;

    protected MaterialAndData itemToHold;
    protected MaterialAndData helmet;
    protected MaterialAndData chestplate;
    protected MaterialAndData leggings;
    protected MaterialAndData boots;

    protected Integer xp;

    protected boolean defaultDrops;
    protected List<String> drops;

    public MagicMob(MagicWorldsController controller, ConfigurationSection parameters) {
        this.controller = controller;

        name = parameters.getString("name");
        health = parameters.getDouble("health", 0);

        String entityName = parameters.getString("type");
        if (entityName != null) {
            entityType = parseEntityType(entityName);
            if (entityType == null) {
                Thread.dumpStack();
                this.controller.getLogger().warning(" Invalid entity type: " + entityName);
            }
        }

        defaultDrops = parameters.getBoolean("default_drops", true);
        if (parameters.contains("xp")) {
            xp = parameters.getInt("xp");
        }
        drops = ConfigurationUtils.getStringList(parameters, "drops");

        String entitySubType = parameters.getString("sub_type");
        if (entitySubType != null && entitySubType.length() > 0) {
            try {
                switch (entityType) {
                    case HORSE:
                        horseVariant = Horse.Variant.valueOf(entitySubType.toUpperCase());
                        break;
                    case SKELETON:
                        skeletonType = Skeleton.SkeletonType.valueOf(entitySubType.toUpperCase());
                        break;
                    case OCELOT:
                        ocelotType = Ocelot.Type.valueOf(entitySubType.toUpperCase());
                        break;
                }
            } catch (Throwable ex) {
            }
        }

        itemToHold = ConfigurationUtils.getMaterialAndData(parameters, "item");
        helmet = ConfigurationUtils.getMaterialAndData(parameters, "helmet");
        chestplate = ConfigurationUtils.getMaterialAndData(parameters, "chestplate");
        leggings = ConfigurationUtils.getMaterialAndData(parameters, "leggings");
        boots = ConfigurationUtils.getMaterialAndData(parameters, "boots");
    }

    @SuppressWarnings("deprecation")
    public static EntityType parseEntityType(String typeString)
    {
        if (typeString == null) return null;

        if (typeString.equalsIgnoreCase("horse"))
        {
            return EntityType.HORSE;
        }
        EntityType returnType = null;
        try {
            returnType = EntityType.valueOf(typeString.toUpperCase());
        } catch (Exception ex) {
            returnType = null;
        }
        if (returnType == null) {
            returnType = EntityType.fromName(typeString);
        }
        return returnType;
    }

    public void copyEquipmentTo(LivingEntity entity) {
        if (itemToHold != null) {
            entity.getEquipment().setItemInHand(itemToHold.getItemStack(1));
        }
        if (helmet != null) {
            entity.getEquipment().setHelmet(helmet.getItemStack(1));
        }
        if (chestplate != null) {
            entity.getEquipment().setChestplate(chestplate.getItemStack(1));
        }
        if (leggings != null) {
            entity.getEquipment().setLeggings(leggings.getItemStack(1));
        }
        if (boots != null) {
            entity.getEquipment().setBoots(boots.getItemStack(1));
        }
    }

    public LivingEntity spawn(Location location) {
        if (entityType == null || location == null) return null;

        Entity result = location.getWorld().spawnEntity(location, entityType);
        switch (entityType) {
            case HORSE:
                if (horseVariant != null && result instanceof Horse) {
                    Horse horse = (Horse)result;
                    try {
                        horse.setVariant(horseVariant);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case SKELETON:
                if (skeletonType != null && result instanceof Skeleton) {
                    Skeleton skeleton = (Skeleton)result;
                    skeleton.setSkeletonType(skeletonType);
                }
                break;
            case OCELOT:
                if (ocelotType != null && result instanceof Ocelot) {
                    Ocelot ocelot = (Ocelot)result;
                    ocelot.setCatType(ocelotType);
                }
                break;
            default:
        }
        if (!(result instanceof LivingEntity)) {
            return null;
        }
        LivingEntity li = (LivingEntity)result;
        copyEquipmentTo(li);

        if (health > 0) {
            li.setMaxHealth(health);
            li.setHealth(health);
        }
        if (name != null && !name.isEmpty()) {
            li.setCustomName(name);
            li.setCustomNameVisible(true);
        }
        return li;
    }

    public String describe() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        if (entityType == null) return "Unknown";

        String name = entityType.name();
        if (skeletonType != null) {
            name += ":" + skeletonType;
        } else if (horseVariant != null) {
            name += ":" + horseVariant;
        } else if (ocelotType != null) {
            name += ":" + ocelotType;
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public void modifyDrops(EntityDeathEvent event) {
        if (xp != null) {
            event.setDroppedExp(xp);
        }

        List<ItemStack> dropList = event.getDrops();
        if (!defaultDrops) {
            dropList.clear();
        }
        if (drops != null) {
            for (String key : drops) {
                ItemStack item = controller.getMagic().createItem(key);
                if (item != null) {
                    dropList.add(item);
                }
            }
        }
    }
}
