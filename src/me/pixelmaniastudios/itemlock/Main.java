package me.pixelmaniastudios.itemlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        this.saveDefaultConfig();
        this.config = getConfig();
        
        // Register the events for this plugin
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Register the command executors
        getCommand("lock").setExecutor(this);
        getCommand("unlock").setExecutor(this);
    }

    // /lock and /unlock commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.must_be_player"));
            return false;
        }

        Player player = (Player) sender;
        
        if (label.equalsIgnoreCase("lock")) {
            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(config.getString("messages.no_item"));
                return true;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isLocked(item)) {
                player.sendMessage(config.getString("messages.already_locked"));
                return true;
            }
            lockItem(item);
            player.sendMessage(config.getString("messages.locked"));
        } else if (label.equalsIgnoreCase("unlock")) {
            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(config.getString("messages.no_item"));
                return true;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!isLocked(item)) {
                player.sendMessage(config.getString("messages.not_locked"));
                return true;
            }
            unlockItem(item);
            player.sendMessage(config.getString("messages.unlocked"));
        }
        return true;
    }

    // Lock the item by adding a lore
    private void lockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = config.getStringList("locked_lore");
            meta.setLore(lore);  // Add "Locked" lore from the config
            item.setItemMeta(meta);
        }
    }

    // Unlock the item by removing the lore
    private void unlockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(null);  // Clear lore to unlock
            item.setItemMeta(meta);
        }
    }

    // Check if the item is locked based on the lore
    private boolean isLocked(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            return lore != null && lore.contains("Locked");  // Check for the "Locked" lore
        }
        return false;
    }

    // Prevent dropping locked items
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isLocked(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.getString("messages.cannot_drop"));
        }
    }

    // Prevent moving locked items in inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getCurrentItem();
        if (isLocked(item)) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).sendMessage(config.getString("messages.cannot_move"));
        }
    }

    // Prevent placing locked block items
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType().isBlock() && isLocked(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.getString("messages.cannot_place"));
        }
    }
}
