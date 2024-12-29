package me.pixelmaniastudios.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.pixelmaniastudios.itemlock.Main;
import me.pixelmaniastudios.utility.ItemUtils;

public class ItemLockListener implements Listener {

    private final Main plugin;

    public ItemLockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (ItemUtils.isLocked(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getConfig().getString("messages.cannot_drop"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getCurrentItem();
        if (ItemUtils.isLocked(item)) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).sendMessage(plugin.getConfig().getString("messages.cannot_move"));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType().isBlock() && ItemUtils.isLocked(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getConfig().getString("messages.cannot_place"));
        }
    }
}
