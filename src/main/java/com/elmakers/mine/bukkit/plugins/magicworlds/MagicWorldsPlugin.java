package com.elmakers.mine.bukkit.plugins.magicworlds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.plugins.magicworlds.populator.WandChestRunnable;
import com.elmakers.mine.bukkit.utility.RunnableJob;

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

	public boolean hasPermission(CommandSender sender, String pNode)
	{
		if (sender instanceof Player) {
			Player player = (Player)sender;
			return player.hasPermission(pNode);
		}
		
		return true;
	}
	
	protected void addIfPermissible(CommandSender sender, List<String> options, String permissionPrefix, String option)
	{
		if (hasPermission(sender, permissionPrefix + option))
		{
			options.add(option);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		String completeCommand = args.length > 0 ? args[args.length - 1] : "";
		List<String> options = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("magicw"))
		{
			if (args.length == 1) {
				addIfPermissible(sender, options, "Magic.commands.magicw.", "populate");
				addIfPermissible(sender, options, "Magic.commands.magicw.", "generate");
				addIfPermissible(sender, options, "Magic.commands.magicw.", "search");
				addIfPermissible(sender, options, "Magic.commands.magicw.", "cancel");
				addIfPermissible(sender, options, "Magic.commands.magicw.", "load");
			}
		}
		
		if (completeCommand.length() > 0) {
			completeCommand = completeCommand.toLowerCase();
			List<String> allOptions = options;
			options = new ArrayList<String>();
			for (String option : allOptions) {
				String lowercase = option.toLowerCase();
				if (lowercase.startsWith(completeCommand)) {
					options.add(option);
				}
			}
		}
		
		Collections.sort(options);
		
		return options;
	}
	
	protected void checkRunningTask()
	{
		if (runningTask != null && runningTask.isFinished()) {
			runningTask = null;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (commandLabel.equalsIgnoreCase("magicw") && args.length > 0)
		{
			String subCommand = args[0];
			if (sender instanceof Player)
			{
				if (!hasPermission((Player)sender, "Magic.commands.magicw." + subCommand)) return false;
			}
			
			if (subCommand.equalsIgnoreCase("load"))
			{		
				controller.load();
				sender.sendMessage("Configuration reloaded.");
				return true;
			}
			
			if (subCommand.equalsIgnoreCase("populate") || subCommand.equalsIgnoreCase("search") || subCommand.equalsIgnoreCase("generate"))
			{   
				checkRunningTask();
				if (runningTask != null) {
					sender.sendMessage("Cancel current job first");
					return true;
				}
				World world = null;
				int ymax = 50;
				if (sender instanceof Player) {
					world = ((Player)sender).getWorld();
					if (args.length > 1) {
						ymax = Integer.parseInt(args[1]);
					}
				} else {
					if (args.length > 1) {
						String worldName = args[1];
						world = Bukkit.getWorld(worldName);
					}
					if (args.length > 2) {
						ymax = Integer.parseInt(args[2]);
					}
				}
				if (world == null) {
					sender.sendMessage("Usage: magicw " + subCommand + " <world> <ymax>");
					return true;
				}
				WandChestRunnable chestRunnable = new WandChestRunnable(controller, world, ymax);
				runningTask = chestRunnable;
				if (subCommand.equalsIgnoreCase("search")) {
					ymax = 0;
					sender.sendMessage("Searching for wands in " + world.getName());
				} else if (subCommand.equalsIgnoreCase("generate")) {
					sender.sendMessage("Generating chunks, and adding wands in " + world.getName() + " below y=" + ymax);
					chestRunnable.setGenerate(true);
				} else {
					sender.sendMessage("Populating chests with wands in " + world.getName() + " below y=" + ymax);
				}
				runningTask.runTaskTimer(this, 5, 5);
				return true;
			}
			if (subCommand.equalsIgnoreCase("cancel"))
			{ 
				checkRunningTask();
				if (runningTask != null) {
					runningTask.cancel();
					runningTask = null;
					sender.sendMessage("Job cancelled");
				} else {
					sender.sendMessage("There is no job running");
				}
				return true;
			}
		}

		return false;
	}

	/*
	 * Private data
	 */	
	private MagicWorldsController controller = null;
	private RunnableJob runningTask = null;
}
