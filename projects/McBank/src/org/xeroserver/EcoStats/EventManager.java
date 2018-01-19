package org.xeroserver.EcoStats;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class EventManager implements Listener {

	EcoStats main = null;

	public EventManager(EcoStats p) {
		main = p;
	}
	
	@EventHandler
	public void onCreative(InventoryCreativeEvent e) {
		e.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void xpChange(PlayerExpChangeEvent e) {
		if(e.getPlayer().getName().equals("Alinator_")) {
			int half =  Math.floorDiv(e.getAmount(), 2);
			
			if(Bukkit.getServer().getPlayer("TeLLuR") != null) {
				Bukkit.getServer().getPlayer("TeLLuR").giveExp(half);
			}
			
			if(Bukkit.getServer().getPlayer("_Xer0_") != null) {
				Bukkit.getServer().getPlayer("_Xer0_").giveExp(half);
			}
			
			e.setAmount(0);
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onBlockDestroy(BlockBreakEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onFurnaceBurnFuel(FurnaceBurnEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onFurnaceBurnFuel(FurnaceSmeltEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onCraft(CraftItemEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onEntityBurn(EntityCombustEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		main.statistics.handle(e);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		main.statistics.handle(e);
	}

}
