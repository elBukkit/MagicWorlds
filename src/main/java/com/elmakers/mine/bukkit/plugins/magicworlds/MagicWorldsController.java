package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.populator.ReplacePopulator;

public class MagicWorldsController implements Listener 
{
	public MagicWorldsController(final Plugin plugin)
	{
		this.logger = plugin.getLogger();
	}
	
	/*
	 * Saving and loading
	 */

	public void initialize()
	{
		load();
	}
	
	public void load()
	{
		try {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save()
	{
	}

	protected void clear()
	{
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		// Install our block populator if configured to do so.
		/*
		 * 
		 * return new WandChestPopulator(this, blockPopulatorConfig);
		 * 
		if (blockPopulatorEnabled && blockPopulatorConfig == null) {
			logger.warning("Block populator is enabled, but missing config");
		}
		if (blockPopulatorEnabled && blockPopulatorConfig != null) {
			World world = event.getWorld();
			world.getPopulators().add(getWandChestPopulator());
			logger.info("Installing chest populator in " + world.getName());
		}
		*/
		
		World world = event.getWorld();
		world.getPopulators().add(new ReplacePopulator());
		
		logger.info("Installing replace populator in " + world.getName());
	}

	/*
	 * Private data
	 */

	private final Logger 	logger;
}
