# CHANGELOG

# 1.8

 - Fix autoloading new worlds
 - Add WildStacker integration
 - Add "biomes" and "not_biomes" options to spawn rules
 - Add support for "" or "*" target entity types (wildcard, will match all entity types)
 - Add "target_class" option, e.g. "LivingEntity", to match a variety of mobs
 - Hide cast log messages by default, re-enable with "verbose: true"
 - Add replace_biomes option

# 1.7

 - Citizens NPCs are ignored by spawn rules (by default, use target_npc: true to change)
 - Update for 1.13+
 - Chunk generator removed (wasn't really ever used)
 - Fix error when using allow_indoors

# 1.6

 - Now reacts to all spawn reasons by default. You can change this behavior in configs.
 - Adds quiet=true parameter to all spell casts.
 - Requires Magic 7.6.6 or higher (to support WG7 and MC 1.13)

# 1.5

 - Fixes to chest populator to work with recent spigot versions (1.11+)
   Will not work with previous spigot versions.
 - Fix replaced mobs dropping default loot.

# 1.4 

 - Config files can now be placed in the plugins/MagicWorlds folder, and will be merged.
   See: https://github.com/elBukkit/MagicWorlds/tree/master/src/main/resources/examples/mixins for some fun examples!
 - Add per-world resource pack feature
 - Add WGCustomFlags integration, "spawn-tags" custom flag and tags on spawn rules
 - WorldGuard custom flag support added, requires latest builds of WorldGuard, replaces WGCustomFlags
 - The default config is now empty! See the "mixins" folder for the chest populators
 - Removed mcstats

# 1.3

 - Now requires the Magic plugin, MagicLib is no more
 - Add EquipmentRule for changing items held by specific mobs
 - Add custom mob spawning via Magic

# 1.2

 - Minor update for Magic 4.5.5

# 1.1

 - Allow adding any magic item (spells, etc) to chests
   NOTE: Configuration has changed, please see example configs
   WandChestPopulator -> MagicChestPopulator
   wand_probability -> item_probability

# 1.0

 - Some fixes to the world copying function

# 0.9

 - Some major optimizations to chest population. (Block replacement is still slow!)
 - The Purge spell won't kill NPCs
 - Will auto-create/copy worlds if configured to do so

# 0.8

 - Fix magicw load command
 - The Purge spell is undoable

# 0.7

 - Fix shading of mcstats to avoid conflicting with other plugins that use it.
 - Can now be used without Magic, using MagicLib: http://dev.bukkit.org/plugins/magiclib/

# 0.6

 - Integrate MCStats for plugin metrics. This can be turned off in config.yml
 - Support variant data type replacement (e.g. stained clay -> stained glass of the same color)
 - Lots of "Other Side" related tweaks

# 0.5

 - Add "Purge" example Magic spell.
 - Requires Magic 3.0 (RC1 or later)
 - Re-worked the configuration for the spawn handlers to be more intuitive.

# 0.3.0

 - Add configurable minimum y height for casting and replacement.
 - Autonoma drop XP on death.

# 0.2.0

 - Allow replacing with variants of horses, skeletons and ocelots
 - Allow probabilities and rankings for entity replace rules
 - Allow randomly casting spells (useful for spawning Autonoma)

# 0.1.0

 - First release
 - Config-driven block and entity replacement.
 - /magicw populate, search, generate, load and cancel commands and permissions

