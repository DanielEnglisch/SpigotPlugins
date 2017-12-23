package at.jellybit.autocompressor;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoCompressor extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		super.onEnable();
		getCommand("compress").setExecutor(this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.isOp()) {
				
				
				for (World w : getServer().getWorlds()) {
					for (Chunk c : w.getLoadedChunks()) {
						for (BlockState bs : c.getTileEntities()) {
							if (bs instanceof Chest) {
								Chest ch = (Chest) bs;

								int coal = 0, iron = 0, gold = 0, emeralds = 0, dias = 0, wheat = 0, lapis = 0, redstone = 0;

								for (ItemStack s : ch.getInventory().getContents()) {

									if (s != null) {
										switch (s.getType()) {
										case COAL:
											coal += s.getAmount();
											break;
										case IRON_INGOT:
											iron += s.getAmount();
											break;
										case GOLD_INGOT:
											gold += s.getAmount();
											break;
										case EMERALD:
											emeralds += s.getAmount();
											break;
										case DIAMOND:
											dias += s.getAmount();
											break;
										case REDSTONE:
											redstone += s.getAmount();
											break;
										case WHEAT:
											wheat += s.getAmount();
											break;
										case INK_SACK:
											if (s.getDurability() == (byte) 4)
												lapis += s.getAmount();
											break;
										default:
											break;
										}

									}

								}
								
								
								
								compress(ch.getInventory(), Material.COAL, Material.COAL_BLOCK, 9, coal);
								compress(ch.getInventory(), Material.IRON_INGOT, Material.IRON_BLOCK, 9, iron);
								compress(ch.getInventory(), Material.GOLD_INGOT, Material.GOLD_BLOCK, 9, gold);
								compress(ch.getInventory(), Material.DIAMOND, Material.DIAMOND_BLOCK, 9, dias);
								compress(ch.getInventory(), Material.EMERALD, Material.EMERALD_BLOCK, 9, emeralds);
								compress(ch.getInventory(), Material.WHEAT, Material.HAY_BLOCK, 9, wheat);
								compress(ch.getInventory(), Material.REDSTONE, Material.REDSTONE_BLOCK, 9, redstone);

								int removed = 0;
								for (ItemStack s : ch.getInventory().getContents()) {
									if (s != null) {

										if (s.getType() == Material.INK_SACK && s.getDurability() == (byte) 4) {

											while (s.getAmount() - 1 >= 0 && removed != lapis) {
												s.setAmount(s.getAmount() - 1);
												removed++;
											}

										}

									}
								}
								
							
								ch.getInventory().addItem(new ItemStack(Material.INK_SACK, lapis % 9, (byte) 4));
								ch.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, lapis / 9));
							}
						}
					}
				}
				
				p.sendMessage("Compressed all chests!");

			}

		}

		return true;
	}

	void compress(Inventory i, Material item, Material block, int conversion, int ammount) {

		int removed = 0;
		for (ItemStack s : i.getContents()) {
			if (s != null) {

				if (s.getType() == item) {

					while (s.getAmount() - 1 >= 0 && removed != ammount) {
						s.setAmount(s.getAmount() - 1);
						removed++;
					}

				}

			}
		}

		i.addItem(new ItemStack(item, ammount % conversion));
		i.addItem(new ItemStack(block, ammount / conversion));
	}

}
