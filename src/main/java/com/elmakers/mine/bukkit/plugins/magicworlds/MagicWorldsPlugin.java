package com.elmakers.mine.bukkit.plugins.magicworlds;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicWorldsPlugin extends JavaPlugin
{	
	/*
	 * Public API
	 */
	public MagicWorldsController getController()
	{
		return controller;
	}

	/*
	 * Plugin interface
	 */

	public void onEnable() 
	{
		if (controller == null) {
			controller = new MagicWorldsController(this);
		}
		initialize();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(controller, this);
	}

	protected void initialize()
	{
		controller.initialize();
	}

	public void onDisable() 
	{
		controller.save();
		controller.clear();
	}

	/*
	 * Private data
	 */	
	private MagicWorldsController controller = null;
}
