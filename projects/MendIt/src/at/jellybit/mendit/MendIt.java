package at.jellybit.mendit;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MendIt extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {

		getCommand("mend").setExecutor(this);

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {

			if (args.length > 1)
				return false;

			Player p = (Player) sender;
			ItemStack s = p.getInventory().getItemInMainHand();
			if (s != null && s.hasItemMeta()) {
				if (s.getItemMeta().hasEnchant(Enchantment.MENDING)) {
					int lvl = p.getLevel();
					if (s.getDurability() == 0) {
						p.sendMessage("Nothing to repair!");
						return true;
					}
					int needed = (s.getDurability()) / 10;
					if (needed == 0)
						needed = 1;

					if (args.length == 1 && args[0].equalsIgnoreCase("it")) {
						if (needed <= lvl) {
							s.setDurability((short) 0);
							p.setLevel(lvl - needed);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 1);

						} else {
							p.sendMessage("You need " + needed + " levels to repair this item!");

						}
					} else if (args.length == 0) {
						p.sendMessage("You need " + needed + " levels to repair this item!");
						p.sendMessage("Type \"/mend it\" to repair it!");

					} else
						return false;

				} else {
					p.sendMessage("You need Mending in order to repair your item!");
				}
			} else {
				p.sendMessage("Can't repair that!");
			}

		} else {
			getLogger().info("This command can only be run by players!");
		}

		return true;

	}

}
