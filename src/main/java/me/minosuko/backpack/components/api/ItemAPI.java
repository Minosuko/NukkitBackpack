package me.minosuko.backpack.components.api;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ItemAPI {
	public String inventoryToString(Inventory inventory) {
		StringBuilder builder = new StringBuilder();
		inventory.getContents().forEach(((slot, item) -> builder.append(itemToString(slot, item)).append(";")));

		return builder.substring(0, builder.toString().length() - 1);
	}

	public Map<Integer, Item> inventoryFromString(String invString) {
		if(!invString.equalsIgnoreCase("empty")) {
			String[] itemStrings = invString.split(";");
			final Map<Integer, Item> backpackInv = new HashMap<>();

			for(String itemString : itemStrings) {
				ItemWithSlot itemWithSlot = itemFromString(itemString);
				backpackInv.put(itemWithSlot.getSlot(), itemWithSlot.getItem());
			}

			return backpackInv;
		} else return new HashMap<>();
	}

	public String itemToString(int slot, Item item) {
		return slot + ":" +
				item.getId() + ":" +
				item.getDamage() + ":" +
				item.getCount() + ":" +
				(item.hasCompoundTag() ? this.bytesToBase64(item.getCompoundTag()) : "not");
	}

	public ItemWithSlot itemFromString(String itemString) throws NumberFormatException {
		String[] info = itemString.split(":");
		int slot = Integer.parseInt(info[0]);

		Item item = Item.get(
				Integer.parseInt(info[1]),
				Integer.parseInt(info[2]),
				Integer.parseInt(info[3])
		);

		if(!info[4].equals("not")) item.setCompoundTag(base64ToBytes(info[4]));

		return new ItemWithSlot(slot, item);
	}

	private String bytesToBase64(byte[] bytes) {
		if(bytes == null || bytes.length <= 0) return "not";

		return Base64.getEncoder().encodeToString(bytes);
	}

	private byte[] base64ToBytes(String hexString) {
		if(hexString == null || hexString.equals("")) return null;

		return Base64.getDecoder().decode(hexString);
	}

	@RequiredArgsConstructor
	@Getter
	public static class ItemWithSlot {
		private final int slot;
		private final Item item;
	}
}
