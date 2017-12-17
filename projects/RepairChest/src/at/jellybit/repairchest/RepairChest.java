package at.jellybit.repairchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RepairChest extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers()) {
					Inventory ec = p.getEnderChest();

					for (ItemStack s : ec.getContents()) {
						if (s != null && s.hasItemMeta()) {
							if (s.getType() == Material.DIAMOND_PICKAXE) {
								if (s.getItemMeta().hasEnchant(Enchantment.MENDING)) {
									s.setDurability((short) (s.getDurability() + 1));
									p.sendMessage("Repairing Pickaxe: " + s.getDurability() + "/1500");
								}
							}
						}

					}
				}

			}
		}, 20l, 20l);

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	public void onSignRightClick(PlayerInteractEvent e) {
		try {
			Block b = e.getClickedBlock();
			if (b.getState() instanceof Sign) {

				Sign s = (Sign) b.getState();
				if (s.getLine(0).equalsIgnoreCase("[Repair]")) {
					double xp = 0;
					try {
						xp = Double.parseDouble(s.getLine(1));
					} catch (Exception e2) {
						xp = 0;
					}

					if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						s.setLine(1, xp - 1 + "");
						e.getPlayer().setLevel(e.getPlayer().getLevel() - 1);
					} else {
						s.setLine(1, xp + 1 + "");
						e.getPlayer().setLevel(e.getPlayer().getLevel() + 1);
					}

					s.update();
				}
			}
		} catch (Exception e2) {
			// TODO: handle exception
		}

	}

}
