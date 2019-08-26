package io.github.fhadiel.NoPortalDestroy;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	ArrayList<Material> preventBlockBreak = new ArrayList<>();
	ArrayList<Material> preventItemDispense = new ArrayList<>();
	FileConfiguration config = getConfig();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		preventBlockBreak = updateMaterials(config.getStringList("preventbreak"));
		preventItemDispense = updateMaterials(config.getStringList("preventdispensing"));
		
		log("Preventing these blocks from breaking: " + preventBlockBreak.toString());
		log("Preventing these items from dispensing: " + preventItemDispense.toString());
	}

	@EventHandler
	public void onPlaceWater(PlayerBucketEmptyEvent e) {
		Block clickedBlock = e.getBlockClicked();
		Block replacedBlock = clickedBlock.getRelative(e.getBlockFace());
		if (isPreventBreakBlock(replacedBlock)) {
			e.setCancelled(true);
			if(config.getBoolean("logging")) log("Prevented " + e.getPlayer().getName() + " from breaking " + replacedBlock.getType().name() + " using a bucket at " + replacedBlock.getLocation().getBlockX() + " " + replacedBlock.getLocation().getBlockY() + " " + replacedBlock.getLocation().getBlockZ());
		}
	}

	@EventHandler
	public void onWaterDispenser(BlockDispenseEvent e) {
		if (e.getBlock().getType().equals(Material.DISPENSER)) {
			Dispenser dispenser = (Dispenser) e.getBlock().getState().getData();
			Block inFrontOfDispenser = e.getBlock().getRelative(dispenser.getFacing());
			ItemStack itemInDispenser = e.getItem();
			if (isPreventBreakBlock(inFrontOfDispenser) && isPreventDispenseItem(itemInDispenser)) {
				e.setCancelled(true);
				if(config.getBoolean("logging")) log("Prevented dispenser from breaking " + inFrontOfDispenser.getType().name() + "using " + itemInDispenser.getType().name() + " at " + inFrontOfDispenser.getLocation().getBlockX() + " " + inFrontOfDispenser.getLocation().getBlockY() + " " + inFrontOfDispenser.getLocation().getBlockZ());
			}
		}
	}
	
	public ArrayList<Material> updateMaterials(List<String> configList) {
		ArrayList<Material> materialArray = new ArrayList<>();
		for (String material : configList) {
			Material m = Material.getMaterial(material);
			if (!(m instanceof Material)) {
				log("There's no material called " + material);
			} else {
				materialArray.add(m);
			}
		}
		
		return materialArray;
	}

	public boolean isPreventBreakBlock(Block replacedBlock) {
		if (preventBlockBreak.contains(replacedBlock.getType())) {
			return true;
		}
		return false;
	}
	
	public boolean isPreventDispenseItem(ItemStack item) {
		if (preventItemDispense.contains(item.getType())) {
			return true;
		}
		return false;
	}
	
	public void log(String msg) {
		getLogger().info(msg);
	}
}
