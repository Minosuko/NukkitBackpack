package me.minosuko.backpack.components.manager;

import cn.nukkit.Player;
import cn.nukkit.IPlayer;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeSlotChangeEvent;
import me.minosuko.backpack.Backpack;
import me.minosuko.backpack.MySQLDB.MySQL;
import me.minosuko.backpack.components.api.ItemAPI;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class BackpackManager {
	private final Backpack plugin;
	private final ItemAPI api;
	private static UUID playerUUID = null;
	private static String playerName = null;
	
	public BackpackManager(final Backpack plugin, final ItemAPI api) {
		this.plugin = plugin;
		this.api = api;
	}

	public void showBackpack(Player player, IPlayer iplayer) {
		String invString = null;
		playerUUID = iplayer.getUniqueId();
		playerName = iplayer.getName();
		DoubleChestFakeInventory chestFakeInventory = new DoubleChestFakeInventory();
		ResultSet rs = MySQL.getResult("SELECT * FROM `PlayerBackpack` WHERE `UUID` = '" + playerUUID + "'");
		try {
			if (rs.next()) {
				invString = rs.getString("BackPackData");
			}
		} catch (SQLException throwables) {
			invString = null;
		}
		if(!this.plugin.getBackpacks().containsKey(playerName) && invString != null) {
			final Map<Integer, Item> items = new HashMap<>();
			this.api.inventoryFromString(invString).forEach(items::put);

			chestFakeInventory.setContents(items);
			this.plugin.getBackpacks().put(playerName, chestFakeInventory);
		} else if(this.plugin.getBackpacks().containsKey(playerName)) {
			chestFakeInventory.setContents(this.plugin.getBackpacks().get(playerName).getContents());
		}
		
		chestFakeInventory.addListener(this::onFakeSlotChange);
		chestFakeInventory.setName(playerName + "'s Backpack");

		player.addWindow(chestFakeInventory);
	}

	public void saveBackpacks(Inventory inventory) {
		MySQL.update("REPLACE INTO `PlayerBackpack` (`UUID`, `PlayerName`, `BackPackData`) VALUES ('" + playerUUID + "' , '" + playerName + "', '" + api.inventoryToString(inventory) + "')");
	}

	private void onFakeSlotChange(final FakeSlotChangeEvent event) {
		if(!(this.plugin.getBackpacks().containsKey(playerName))) {
			this.plugin.getBackpacks().put(playerName, event.getInventory());
		} else {
			this.plugin.getBackpacks().replace(playerName, this.plugin.getBackpacks().get(playerName), event.getInventory());
		}
		this.saveBackpacks( event.getInventory());
	}
}
