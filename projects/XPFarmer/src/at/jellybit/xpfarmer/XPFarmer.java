package at.jellybit.xpfarmer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_12_R1.EntityExperienceOrb;
import net.minecraft.server.v1_12_R1.World;

public class XPFarmer extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(this, this);

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@EventHandler
	public void onSugarPlace(BlockPlaceEvent e) {
		e.getBlock().setMetadata("PLACED", new FixedMetadataValue(this, "true"));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		if (e.getBlock().getType() == Material.SUGAR_CANE_BLOCK) {

			int hasTop = 1;
			if (e.getBlock().getRelative(0, 1, 0).getType() == Material.SUGAR_CANE_BLOCK) {
				e.getBlock().getRelative(0, 1, 0).setType(Material.AIR);
				hasTop = 2;

			}

			e.getBlock().setType(Material.AIR);

			if (e.getPlayer().getInventory().firstEmpty() == -1)
				e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(),
						new ItemStack(Material.SUGAR_CANE, 1 * hasTop));
			else
				e.getPlayer().getInventory().addItem(new ItemStack(Material.SUGAR_CANE, 1 * hasTop));

			if (!e.getBlock().hasMetadata("PLACED")) {
				spawnOrb(e.getBlock().getLocation(), 1 * hasTop, 1);

			}

		}else

		if (e.getBlock().getType() == Material.CROPS || e.getBlock().getType() == Material.CARROT
				|| e.getBlock().getType() == Material.POTATO) {


			if (e.getBlock().getData() == 0x7) {
				spawnOrb(e.getBlock().getLocation(), 1, 1);

			}
		}

	}

	public void spawnOrb(org.bukkit.Location l, int amount, int value) {

		if (Math.random() > 0.33)
			return;

		double x = l.getX(), y = l.getY(), z = l.getZ();
		World w = ((CraftWorld) l.getWorld()).getHandle();
		for (int i = 0; i < value; i++) {
			((World) w).addEntity(new EntityExperienceOrb((World) w, x, y, z, value));
		}
	}

	// Anti trample
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTrample(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() == Action.PHYSICAL) {
			Block block = event.getClickedBlock();
			if (block == null) {
				return;
			}
			int blockType = block.getTypeId();
			if (blockType == Material.getMaterial(59).getId()) {
				event.setUseInteractedBlock(Event.Result.DENY);
				event.setCancelled(true);

				block.setTypeId(blockType);
				block.setData(block.getData());
			}
		}
		if (event.getAction() == Action.PHYSICAL) {
			Block block = event.getClickedBlock();
			if (block == null) {
				return;
			}
			int blockType = block.getTypeId();
			if (blockType == Material.getMaterial(60).getId()) {
				event.setUseInteractedBlock(Event.Result.DENY);
				event.setCancelled(true);

				block.setType(Material.getMaterial(60));
				block.setData(block.getData());
			}
		}
	}

}
