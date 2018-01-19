package org.xeroserver.EcoStats;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class CommandEco implements CommandExecutor {

	private Statistics statistics = null;

	public CommandEco(Statistics s) {
		statistics = s;
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// ATM setup command
		if (command.getName().equalsIgnoreCase("eco")) {
			if (args.length == 0) {
				printHelp(sender);
				return true;
			} else if (args.length == 1) {

				if (args[0].equals("countWorld")) {
					countWorldMoney((Player) sender);
				} else if (args[0].equals("info")) {
					if (!sender.hasPermission("eco.info"))
						return true;
					statistics.printKurs((Player) sender);
				}

				

				return true;
			}

		}

		return false;
	}

	private void printHelp(CommandSender s) {
		
		Player p = (Player) s;
		
		try {
			ItemStack[] is = p.getInventory().getContents();
			for (ItemStack i : is) {
				if (i.getType() == Material.MAP) {
					short d = i.getDurability();
					@SuppressWarnings("deprecation")
					MapView map = Bukkit.getServer().getMap(d);
					map.getRenderers().clear();
					map.addRenderer(statistics.getRenderer());
					p.sendMessage("Converted map into EcoStats Display!");
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		if (!s.hasPermission("eco.help"))
			return;

		if (!(s instanceof Player)) {
			System.err.println("The eco command can only be used by players!");
			return;
		}


		p.sendMessage(new String[] { "===EcoStats===", "/eco countWorld - Counts money in all inventories",
				"/eco info - Shows current prices", "=========", });
	}

	private void countWorldMoney(Player p) {

		if (!p.hasPermission("eco.countWorld"))
			return;

		int c = 0, d = 0, i = 0, g = 0;
		int c_ch = 0;

		Chunk[] chunks = Bukkit.getWorlds().get(0).getLoadedChunks();
		for (Chunk ch : chunks) {

			c_ch++;
			BlockState[] blocks = ch.getTileEntities();
			for (BlockState b : blocks) {
				if (b instanceof InventoryHolder) {
					ItemStack[] items = ((InventoryHolder) b).getInventory().getContents();

					for (ItemStack itemstack : items) {

						if (itemstack == null)
							break;

						switch (itemstack.getType()) {
						case COAL:
							c += itemstack.getAmount();
							break;
						case COAL_BLOCK:
							c += itemstack.getAmount() * 9;
							break;

						case IRON_INGOT:
							i += itemstack.getAmount();
							break;
						case IRON_BLOCK:
							i += itemstack.getAmount() * 9;
							break;

						case GOLD_INGOT:
							g += itemstack.getAmount();
							break;
						case GOLD_BLOCK:
							g += itemstack.getAmount() * 9;
							break;

						case GOLD_NUGGET:
							g += itemstack.getAmount();
							break;

						case DIAMOND:
							d += itemstack.getAmount();
							break;
						case DIAMOND_BLOCK:
							d += itemstack.getAmount() * 9;
							break;
						default:
							break;

						}
					}
				}
			}

		}
		p.sendMessage(new String[] { "===Total Inventory contents (" + c_ch + " Chunks)===", "coal: " + c, "iron: " + i,
				"gold: " + g, "dias: " + d });

	}

}
