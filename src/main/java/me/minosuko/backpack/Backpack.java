package me.minosuko.backpack;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import lombok.Getter;
import me.minosuko.backpack.components.api.ItemAPI;
import me.minosuko.backpack.components.manager.BackpackManager;
import me.minosuko.backpack.commands.BackpackCommand;
import me.minosuko.backpack.MySQLDB.MySQL;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Backpack extends PluginBase {
	public static Config cfg;

	@Getter
	private Map<String, Inventory> backpacks = new HashMap<>();

	@Getter
	private BackpackManager manager;

	@Override
	public void onEnable() {
		this.cfg = new Config(new File(this.getDataFolder(), "config.yml"));
		this.manager = new BackpackManager(this, new ItemAPI());
        saveResource("config.yml");
		MySQL.connect();
		if (!MySQL.isConnected()) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		MySQL.update("CREATE TABLE IF NOT EXISTS `PlayerBackpack` (`UUID` VARCHAR(100) PRIMARY KEY, `PlayerName` VARCHAR(100), `BackPackData` LONGTEXT)");
		this.getServer().getCommandMap().register("bp", new BackpackCommand(this));
	}

	@Override
	public void onDisable() {
		MySQL.disconnect();
	}
}
