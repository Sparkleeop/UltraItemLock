package me.pixelmaniastudios.utility;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.pixelmaniastudios.itemlock.Main;

import java.util.List;

public class ItemUtils {

    // Lock the item and add locked lore
    public static void lockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = Main.getPlugin(Main.class).getConfig().getStringList("locked_lore");
            lore.add("Locked");  // Add the locked tag to the lore
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    // Unlock the item and remove the locked lore
    public static void unlockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore != null && lore.contains("Locked")) {
                lore.remove("Locked");  // Remove the "Locked" tag from lore
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    // Check if the item is locked based on the lore
    public static boolean isLocked(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            return lore != null && lore.contains("Locked");
        }
        return false;
    }
}
