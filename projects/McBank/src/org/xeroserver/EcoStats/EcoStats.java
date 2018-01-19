package org.xeroserver.EcoStats;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class EcoStats extends JavaPlugin {

	public CommandEco commandAtm;
	public Statistics statistics;

	@Override
	public void onEnable() {

		statistics = new Statistics(new File("plugins/EcoStats/statistics.json"), this);
		
		commandAtm = new CommandEco(statistics);

		getServer().getPluginManager().registerEvents(new org.xeroserver.EcoStats.EventManager(this), this);
		getCommand("eco").setExecutor(commandAtm);

	}

}
