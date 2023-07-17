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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class BackpackManager {
	private final Backpack plugin;
	private final ItemAPI api;
	private static UUID playerUUID = null;
	
	public BackpackManager(final Backpack plugin, final ItemAPI api) {
		this.plugin = plugin;
		this.api = api;
	}

	public void showBackpack(@NotNull Player player) {
		String invString = null;
		IPlayer target = player;
		playerUUID = player.getUniqueId();
		DoubleChestFakeInventory chestFakeInventory = new DoubleChestFakeInventory();
		ResultSet rs = MySQL.getResult("SELECT * FROM `PlayerBackpack` WHERE `PlayerName` = '" + player.getName() + "'");
		try {
			if (rs.next()) {
				invString = rs.getString("BackPackData");
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		if(!this.plugin.getBackpacks().containsKey(player.getName()) && invString != null) {
			final Map<Integer, Item> items = new HashMap<>();
			this.api.inventoryFromString(invString).forEach(items::put);

			chestFakeInventory.setContents(items);
			this.plugin.getBackpacks().put(player.getName(), chestFakeInventory);
		} else if(this.plugin.getBackpacks().containsKey(player.getName())) {
			chestFakeInventory.setContents(this.plugin.getBackpacks().get(player.getName()).getContents());
		}
		
		chestFakeInventory.addListener(this::onFakeSlotChange);
		chestFakeInventory.setName(player.getName() + "'s Backpack");

		player.addWindow(chestFakeInventory);
	}

	public void saveBackpacks(String playerName, Inventory inventory) {
		MySQL.update("REPLACE INTO `PlayerBackpack` (`UUID`, `PlayerName`, `BackPackData`) VALUES ('" + playerUUID + "' , '" + playerName + "', '" + api.inventoryToString(inventory) + "')");
	}

	private void onFakeSlotChange(final FakeSlotChangeEvent event) {
		if(!(this.plugin.getBackpacks().containsKey(event.getPlayer().getName()))) {
			this.plugin.getBackpacks().put(event.getPlayer().getName(), event.getInventory());
		} else {
			this.plugin.getBackpacks().replace(event.getPlayer().getName(), this.plugin.getBackpacks().get(event.getPlayer().getName()), event.getInventory());
		}
		this.saveBackpacks(event.getPlayer().getName(), event.getInventory());
	}
}
