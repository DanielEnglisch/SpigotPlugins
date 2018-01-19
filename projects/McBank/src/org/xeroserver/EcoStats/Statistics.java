package org.xeroserver.EcoStats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xeroserver.EcoStats.StatisticsChangeEvent.Cause;
import org.xeroserver.EcoStats.StatisticsChangeEvent.Currency;

public class Statistics {

	private EcoStats main;
	private ArrayList<Sign> signs = new ArrayList<Sign>();
	private MapRenderer mapRenderer;
	private File f;

	public Statistics(File f, EcoStats ec) {
		this.f = f;
		this.main = ec;
		mapRenderer = new MyRenderer();

		// Detecting itemframes

		for (Chunk k : Bukkit.getWorld("world").getLoadedChunks()) {
			for (Entity e : k.getEntities()) {

				if (e instanceof ItemFrame) {
					ItemFrame frame = (ItemFrame) e;
					ItemStack i = frame.getItem();
					if (i.getItemMeta() != null) {
						if (i.getItemMeta().getDisplayName() != null) {
							if (i.getItemMeta().getDisplayName().equalsIgnoreCase("Eco")) {

								if (i.getType() == Material.MAP) {
									short d = i.getDurability();
									@SuppressWarnings("deprecation")
									MapView map = Bukkit.getServer().getMap(d);
									map.getRenderers().clear();
									map.addRenderer(mapRenderer);
									System.out.println("Converted map into EcoStats Display!");
								}
							}
						}
					}

				}
			}
		}

		// Create/Load file
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				ec.getLogger().warning(("Failed to create " + f.getAbsolutePath()));
				e.printStackTrace();
			}
			ec.getLogger().info("Created " + f.getAbsolutePath());
			save();

		} else
			load();

	}

	public int coal = 0, dias = 0, gold = 0, iron = 0;

	private final int price_coal = 1;
	private final int price_iron = 5;
	private final int price_gold = 25;
	private final int price_dias = 250;
	private final int price_sum = price_coal + price_iron + price_gold + price_dias;

	private final double multiplayer = 10;

	private final double ver_coal = ((double) price_coal / (double) price_sum) * multiplayer;
	private final double ver_iron = ((double) price_iron / (double) price_sum) * multiplayer;
	private final double ver_gold = ((double) price_gold / (double) price_sum) * multiplayer;
	private final double ver_dias = ((double) price_dias / (double) price_sum) * multiplayer;

	private int price_ak_coal;
	private int price_ak_iron;
	private int price_ak_gold;
	private int price_ak_dias;
	private int price_ak_sum;

	private double ver_ak_coal;
	private double ver_ak_iron;
	private double ver_ak_gold;
	private double ver_ak_dias;

	private double diff_ver_coal;
	private double diff_ver_iron;
	private double diff_ver_gold;
	private double diff_ver_dias;

	private double kurs_coal;
	private double kurs_iron;
	private double kurs_gold;
	private double kurs_dias;

	public void recalculate() {

		price_ak_coal = coal * price_coal;
		price_ak_iron = iron * price_iron;
		price_ak_gold = gold * price_gold;
		price_ak_dias = dias * price_dias;

		price_ak_sum = price_ak_coal + price_ak_iron + price_ak_gold + price_ak_dias;

		ver_ak_coal = (double) ((double) price_ak_coal / (double) price_ak_sum) * multiplayer;
		ver_ak_iron = (double) ((double) price_ak_iron / (double) price_ak_sum) * multiplayer;
		ver_ak_gold = (double) ((double) price_ak_gold / (double) price_ak_sum) * multiplayer;
		ver_ak_dias = (double) ((double) price_ak_dias / (double) price_ak_sum) * multiplayer;

		diff_ver_coal = ver_coal - ver_ak_coal;
		diff_ver_iron = ver_iron - ver_ak_iron;
		diff_ver_gold = ver_gold - ver_ak_gold;
		diff_ver_dias = ver_dias - ver_ak_dias;

		kurs_coal = (double) ((double) (price_ak_coal + price_ak_coal * diff_ver_coal) / (double) coal);
		kurs_iron = (double) ((double) (price_ak_iron + price_ak_iron * diff_ver_iron) / (double) iron);
		kurs_gold = (double) ((double) (price_ak_gold + price_ak_gold * diff_ver_gold) / (double) gold);
		kurs_dias = (double) ((double) (price_ak_dias + price_ak_dias * diff_ver_dias) / (double) dias);
		updateSigns();
		save();
	}

	public class MyRenderer extends MapRenderer {
		public void render(MapView map, MapCanvas canvas, Player player) {
			// Clear:
			for (int x = 0; x <= 128; x++) {
				for (int y = 0; y <= 128; y++) {
					canvas.setPixel(x, y, (byte) 75);
				}
			}

			int sum = coal + iron + gold + dias;

			float c = (float) coal / (float) sum;
			float i = (float) iron / (float) sum;
			float g = (float) gold / (float) sum;
			float d = (float) dias / (float) sum;

			int cC = Math.round(c * 128);
			int iC = Math.round(i * 128);
			int gC = Math.round(g * 128);
			int dC = Math.round(d * 128);

			for (int x = 0; x <= 31; x++) {
				for (int y = 0; y <= cC; y++) {
					canvas.setPixel(x, 128 - y, (byte) 50);
				}
			}

			for (int x = 32; x <= 63; x++) {
				for (int y = 0; y <= iC; y++) {
					canvas.setPixel(x, 128 - y, (byte) 100);
				}
			}

			for (int x = 64; x <= 95; x++) {
				for (int y = 0; y <= gC; y++) {
					canvas.setPixel(x, 128 - y, (byte) 150);
				}
			}

			for (int x = 96; x <= 128; x++) {
				for (int y = 0; y <= dC; y++) {
					canvas.setPixel(x, 128 - y, (byte) 200);
				}
			}

		}
	}

	public MapRenderer getRenderer() {
		return mapRenderer;
	}

	public void handle(SignChangeEvent e) {
		Sign s = (Sign) e.getBlock().getState();
		if (e.getLine(0).equals("[STATS]")) {
			e.getPlayer().sendMessage("Added sign!");
			signs.add(s);
			recalculate();
		}
	}

	private void updateSigns() {
		for (Sign s : signs) {
			s.setLine(0, "updated: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
					+ LocalDateTime.now().getSecond());
			s.setLine(1, "c" + coal + "i" + iron + "g" + gold + "d" + dias);
			s.setLine(2, "c: " + String.format("%.2f", kurs_coal) + " i:" + String.format("%.2f", kurs_iron));
			s.setLine(3, "g: " + String.format("%.2f", kurs_gold) + " d:" + String.format("%.2f", kurs_dias));
			s.update();
		}
	}

	private void load() {
		try {
			FileReader in = new FileReader(f);
			JSONObject main = (JSONObject) new JSONParser().parse(in);
			JSONObject o = (JSONObject) main.get("stats");

			coal = Integer.parseInt(o.get("coal").toString());
			iron = Integer.parseInt(o.get("iron").toString());
			gold = Integer.parseInt(o.get("gold").toString());
			dias = Integer.parseInt(o.get("dias").toString());

			JSONArray sgnArr = (JSONArray) main.get("signs");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = sgnArr.iterator();
			while (iterator.hasNext()) {
				JSONObject block = iterator.next();
				Location l = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(block.get("x").toString()),
						Double.parseDouble(block.get("y").toString()), Double.parseDouble(block.get("z").toString()));
				if (l.getBlock().getState() instanceof Sign) {
					signs.add((Sign) l.getBlock().getState());
				} else
					this.main.getLogger().info("Omitted sign " + l.toString());
			}

		} catch (Exception e) {
			main.getLogger().warning("Failed to load file " + f.getAbsolutePath());
			e.printStackTrace();
			return;
		}

		main.getLogger().warning("Loaded statistics: " + this.toString());

	}

	private void save() {
		try {
			FileWriter out = new FileWriter(f);
			String stats = "{" + "\"coal\": " + coal + ", " + "\"iron\": " + iron + ", " + "\"gold\": " + gold + ", "
					+ "\"dias\": " + dias + "}";
			String signStr = "[";

			for (Sign s : signs) {
				signStr += "{";
				signStr += "\"x\":" + s.getX() + ",";
				signStr += "\"y\":" + s.getY() + ",";
				signStr += "\"z\":" + s.getZ() + "}";
				if (!s.equals(signs.get(signs.size() - 1)))
					signStr += ",";
			}
			signStr += "]";

			String outStr = "{\"stats\": " + stats + ", \"signs\": " + signStr + "}";
			out.write(outStr);
			out.flush();
			out.close();

		} catch (IOException e) {
			main.getLogger().warning("Failed to save file " + f.getAbsolutePath());
			e.printStackTrace();
			return;
		}
		main.getLogger().warning("Saved statistics!");

	}

	public void printKurs(Player p) {
		p.sendMessage("===Kurs:===");
		p.sendMessage("Kohle: " + String.format("%.2f", kurs_coal));
		p.sendMessage("Eisen: " + String.format("%.2f", kurs_iron));
		p.sendMessage("Gold: " + String.format("%.2f", kurs_gold));
		p.sendMessage("Dias: " + String.format("%.2f", kurs_dias));
	}

	private void process(StatisticsChangeEvent e) {
		switch (e.getCurrency()) {
		case COAL:
			coal += e.getAmount();
			break;
		case IRON:
			iron += e.getAmount();
			break;
		case GOLD:
			gold += e.getAmount();
			break;
		case DIAS:
			dias += e.getAmount();
			break;

		default:
			break;
		}

		// Bukkit.broadcastMessage("Eco: " + e.getCause().toString() + " " +
		// e.getCurrency().toString() + " " + e.getAmount());
		recalculate();
	}

	// MobDrops (Iron Golem, Zombies,...)
	public void handle(EntityDeathEvent e) {

		if (e.getEntity() instanceof Player) {
			return;
		}

		for (ItemStack s : e.getDrops()) {
			StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.MOB_DROP);

			switch (s.getType()) {
			case COAL:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(s.getAmount());
				break; // WitherSkelly
			case IRON_INGOT:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(s.getAmount());
				break; // Zombie, Golem
			case GOLD_NUGGET:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(s.getAmount());
				break; // Zombie Pigman
			default:
				break;
			}

			if (sce.valid())
				process(sce);
		}

	}

	// Item in Lava
	public void handle(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.LAVA) {
			Item i;
			try {
				i = (Item) e.getEntity();
			} catch (Exception e2) {
				return;
			}

			ItemStack s = i.getItemStack();

			StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.ENTITY_DESTROY);

			// All types possible
			switch (s.getType()) {
			case COAL:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-s.getAmount());
				break;
			case IRON_INGOT:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-s.getAmount());
				break;
			case GOLD_INGOT:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount());
				break;
			case DIAMOND:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-s.getAmount());
				break;

			case COAL_BLOCK:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case IRON_BLOCK:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case GOLD_BLOCK:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case DIAMOND_BLOCK:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-s.getAmount() * 9);
				break;

			case GOLD_NUGGET:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount());
				break;
			default:
				break;
			}

			if (sce.valid())
				process(sce);

		}
	}

	// Item verbrennt
	public void handle(EntityCombustEvent e) {
		if (e.getEntityType() == EntityType.DROPPED_ITEM) {
			Item i = (Item) e.getEntity();

			ItemStack s = i.getItemStack();

			StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.FIRE_BURN);

			// All types possible
			switch (s.getType()) {
			case COAL:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-s.getAmount());
				break;
			case IRON_INGOT:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-s.getAmount());
				break;
			case GOLD_INGOT:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount());
				break;
			case DIAMOND:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-s.getAmount());
				break;

			case COAL_BLOCK:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case IRON_BLOCK:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case GOLD_BLOCK:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount() * 9);
				break;
			case DIAMOND_BLOCK:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-s.getAmount() * 9);
				break;

			case GOLD_NUGGET:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-s.getAmount());
				break;
			default:
				break;
			}

			if (sce.valid())
				process(sce);

		}
	}

	// Verbrauchen von Items
	public void handle(CraftItemEvent e) {

		// Ignorieren vom Craften equivalenter Materialien (ingot->block, etc..)
		Material type = e.getRecipe().getResult().getType();
		if (type == Material.GOLD_BLOCK || type == Material.GOLD_NUGGET || type == Material.COAL_BLOCK
				|| type == Material.DIAMOND_BLOCK || type == Material.COAL || type == Material.GOLD_INGOT
				|| type == Material.IRON_INGOT || type == Material.DIAMOND || type == Material.IRON_BLOCK)
			return;

		StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.CRAFT_USAGE);

		for (ItemStack s : e.getInventory().getContents()) {
			switch (s.getType()) {
			case COAL:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-1);
				break;
			case IRON_INGOT:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-1);
				break;
			case GOLD_INGOT:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-1);
				break;
			case DIAMOND:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-1);
				break;

			case COAL_BLOCK:
				sce.setCurrency(Currency.COAL);
				sce.setAmount(-1 * 9);
				break;
			case IRON_BLOCK:
				sce.setCurrency(Currency.IRON);
				sce.setAmount(-1 * 9);
				break;
			case GOLD_BLOCK:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-1 * 9);
				break;
			case DIAMOND_BLOCK:
				sce.setCurrency(Currency.DIAS);
				sce.setAmount(-1 * 9);
				break;

			case GOLD_NUGGET:
				sce.setCurrency(Currency.GOLD);
				sce.setAmount(-1);
				break;
			default:
				break;
			}

			if (sce.valid()) {
				process(sce);
				sce.setAmount(0);
			}
		}

	}

	// Erz / Rüstung schnelzen
	public void handle(FurnaceSmeltEvent e) {

		ItemStack s = e.getResult();

		StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.FURNACE_RESULT);

		switch (s.getType()) {

		case IRON_INGOT:
			sce.setCurrency(Currency.IRON);
			sce.setAmount(s.getAmount());
			break; // Erz
		case GOLD_INGOT:
			sce.setCurrency(Currency.GOLD);
			sce.setAmount(s.getAmount());
			break; // Erz
		case GOLD_NUGGET:
			sce.setCurrency(Currency.GOLD);
			sce.setAmount(s.getAmount());
			break; // Rüssi

		default:
			break;
		}

		if (sce.valid())
			process(sce);
	}

	// Verbrauch von Kohle
	public void handle(FurnaceBurnEvent e) {

		StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.FURNACE_FUEL);
		sce.setCurrency(Currency.COAL);

		if (e.getFuel().getType() == Material.COAL) {
			sce.setAmount(-1);
		} else if (e.getFuel().getType() == Material.COAL_BLOCK) {
			sce.setAmount(-9);
		}

		if (sce.valid())
			process(sce);

	}

	// Special: Abbauen von Kohle/Dias mit Fortune
	public void handle(BlockBreakEvent m) {
		Material type = m.getBlock().getType();

		// If Sign Break
		if (m.getBlock().getState() instanceof Sign) {
			Sign s = ((Sign) m.getBlock().getState());
			if (signs.contains(s)) {
				m.getPlayer().sendMessage("Destroyed Sign!");
				signs.remove((Sign) m.getBlock().getState());
				save();
			}

		}
		//
		StatisticsChangeEvent sce = new StatisticsChangeEvent(Cause.BLOCK_BREAK);

		switch (type) {
		case COAL_ORE: {
			sce.setCurrency(Currency.COAL);
			if (m.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
				if (m.getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS).intValue() == 1)
					sce.setAmount(2);
				else if (m.getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS)
						.intValue() == 2)
					sce.setAmount(3);
				else
					sce.setAmount(4);

			} else
				sce.setAmount(1);

		}
			break;
		case DIAMOND_ORE: {
			sce.setCurrency(Currency.DIAS);
			if (m.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
				if (m.getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS).intValue() == 1)
					sce.setAmount(2);
				else if (m.getPlayer().getItemInHand().getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS)
						.intValue() == 2)
					sce.setAmount(3);
				else
					sce.setAmount(4);

			} else
				sce.setAmount(1);

		}
			break;
		default:
			return;
		}

		if (sce.valid())
			process(sce);

	}

	@Override
	public String toString() {
		return "Statistics [coal=" + coal + ", iron=" + iron + ", gold=" + gold + ", dias=" + dias + "]";
	}

}
