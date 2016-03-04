package com.elmakers.mine.bukkit.magicworlds;

import com.elmakers.mine.bukkit.api.event.LoadEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.magicworlds.listener.EntitySpawnListener;
import com.elmakers.mine.bukkit.magicworlds.listener.PlayerListener;
import com.elmakers.mine.bukkit.magicworlds.populator.builtin.MagicChestPopulator;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MagicWorldsController implements Listener
{
	public MagicWorldsController(final Plugin plugin)
	{
		this.logger = plugin.getLogger();
		this.plugin = plugin;
        worldGenerator = new MagicChunkGenerator();
		
		Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
		if (magicPlugin == null || !(magicPlugin instanceof MagicAPI)) {
			logger.warning("Magic plugin not found, spell casting and wand creation disabled");
			logger.warning("Make sure you have the MagicLib plugin instead, or MagicWorlds will probably break");
			this.magicAPI = null;
		} else {
			logger.info("Integrating with Magic");
			this.magicAPI = (MagicAPI)magicPlugin;
		}		
	}
	
	/*
	 * Saving and loading
	 */

	public void initialize()
	{
		plugin.saveDefaultConfig();
		load();
	}
	
	public void load()
	{
		File configFolder = plugin.getDataFolder();
		ConfigurationSection config = new MemoryConfiguration();
		if (configFolder.exists()) {
			File[] files = configFolder.listFiles();
			for (File file : files) {
				if (file.getName().startsWith(".")) continue;
				getLogger().info("  Loading " + file.getName());
				try {
					YamlConfiguration newConfig = new YamlConfiguration();
					newConfig.load(file);
					config = ConfigurationUtils.addConfigurations(config, newConfig, true);
				} catch (Exception ex) {
					getLogger().log(Level.WARNING, "Error loading file " + file.getName(), ex);
				}
			}
		}
		try {
			metricsLevel = config.getInt("metrics_level", metricsLevel);
            if (config.contains("terrain")) {
                worldGenerator.load(config.getConfigurationSection("terrain"), this);
            }
            PluginManager pm = Bukkit.getPluginManager();
            if (config.getBoolean("entity_spawn_listener", true)) {
                pm.registerEvents(new EntitySpawnListener(this), plugin);
            }
			if (config.getBoolean("player_listener", true)) {
				pm.registerEvents(new PlayerListener(this), plugin);
			}

			ConfigurationSection worlds = config.getConfigurationSection("worlds");
			if (worlds != null) {
				Set<String> worldKeys = worlds.getKeys(false);
				for (String worldName : worldKeys) {
					logger.info("Customizing world " + worldName);
					MagicWorld world = magicWorlds.get(worldName);
					if (world == null) world = new MagicWorld();
					world.load(worldName, worlds.getConfigurationSection(worldName), this);
					magicWorlds.put(worldName, world);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		metrics = null;
		if (metricsLevel > 0) {
			try {
			    metrics = new Metrics(plugin);
			    metrics.start();
			    plugin.getLogger().info("Activated MCStats");
			} catch (IOException e) {
			    plugin.getLogger().warning("Failed to load MCStats: " + e.getMessage());
			}
		}
	}
	
	public void finalizeLoad()
	{
		for (MagicWorld world : magicWorlds.values()) {
			world.finalizeLoad();
		}
	}

	public void save()
	{
	}

	protected void clear()
	{
	}
	
	@EventHandler
	public void onMagicLoad(LoadEvent loadEvent) {
		if (!initialLoad) {
			return;
		}
		initialLoad = false;
		load();
		finalizeLoad();
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        for (MagicWorld notifyWorld : magicWorlds.values()) {
            notifyWorld.onWorldInit(plugin, world);
        }
		MagicWorld magicWorld = magicWorlds.get(world.getName());
		if (magicWorld == null) return;
		
		logger.info("Initializing world " + world.getName());
		magicWorld.installPopulators(world);
	}

    public Logger getLogger() {
    	return logger;
    }
    
    public MagicAPI getMagic() {
    	if (magicAPI == null) return null;
    	return magicAPI;
    }
    
    public MagicChestPopulator getMagicChestPopulator(String worldName) {
    	MagicWorld magicWorld = magicWorlds.get(worldName);
    	if (magicWorld == null) return null;
    	
    	return magicWorld.getMagicChestPopulator();
    }
    
    public boolean isMagicEnabled()
    {
    	return magicAPI != null;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ChunkGenerator getWorldGenerator(String worldName, String id) {
        return worldGenerator;
    }

    public MagicWorld getWorld(String name) {
        return magicWorlds.get(name);
    }
	
	/*
	 * Private data
	 */

    private MagicAPI magicAPI = null;
	private boolean initialLoad = true;

    private final Map<String, MagicWorld> magicWorlds = new HashMap<String, MagicWorld>();
    private final MagicChunkGenerator worldGenerator;
    private final Plugin	plugin;
	private final Logger 	logger;

    private String                           googleAPIKey;

	private int								 metricsLevel					= 5;
	private Metrics							 metrics						= null;
}
