package at.jellybit.blockclip;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockClip extends JavaPlugin implements Listener {

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
	public void onRighClick(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getPlayer().isSneaking()){
				if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.FEATHER){
					Location[] loc = new Location[2];
					Material[] mat = new Material[2];
					loc[0] = e.getClickedBlock().getLocation();
					loc[1] = e.getClickedBlock().getRelative(0, -1, 0).getLocation();
					mat[0] = e.getClickedBlock().getType();
					mat[1] = e.getClickedBlock().getRelative(0, -1, 0).getType();

					e.getClickedBlock().getLocation().getBlock().setType(Material.AIR);
					e.getClickedBlock().getRelative(0, -1, 0).setType(Material.AIR);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, ()->{
						
						e.getPlayer().getWorld().getBlockAt(loc[0]).setType(mat[0]);
						e.getPlayer().getWorld().getBlockAt(loc[1]).setType(mat[1]);

					}, 30L);
				}
			}
		}
	}

	

}
