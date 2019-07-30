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
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	ArrayList<Material> preventBlockBreak = new ArrayList<>();
	FileConfiguration config = getConfig();
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		updateConfig(getConfig().getStringList("preventbreak"));
		log("Prevent these block from breaking " + preventBlockBreak.toString());
	}

	@EventHandler
	public void onPlaceWater(PlayerBucketEmptyEvent e) {
		Block clickedBlock = e.getBlockClicked();
		Material replacedBlock = clickedBlock.getRelative(e.getBlockFace()).getType();
		if (isEndPortal(replacedBlock)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onWaterDispenser(BlockDispenseEvent e) {
		if (e.getBlock().getType().equals(Material.DISPENSER)) {
			Dispenser dispenser = (Dispenser) e.getBlock().getState().getData();
			Block inFrontOfDispenser = e.getBlock().getRelative(dispenser.getFacing());
			if (isEndPortal(inFrontOfDispenser.getType())) {
				e.setCancelled(true);
			}
		}

	}

	public void updateConfig(List<String> blocks) {
		ArrayList<Material> materialBlock = new ArrayList<>();
		for (String block : blocks) {
			Material b = Material.getMaterial(block);
			if (!(b instanceof Material)) {
				log("There's no block called " + b.name());
			} else {
				materialBlock.add(b);
			}
		}
		preventBlockBreak = materialBlock;
	}

	public boolean isEndPortal(Material replacedBlock) {
		if (preventBlockBreak.contains(replacedBlock)) {
			log("someone try to break " + replacedBlock.name());
			return true;
		}

		return false;
	}
	public void log(String msg) {
		if(config.getBoolean("logging")) getLogger().info(msg);
	}
}
