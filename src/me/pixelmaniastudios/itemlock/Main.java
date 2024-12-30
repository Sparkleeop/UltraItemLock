package me.pixelmaniastudios.itemlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.pixelmaniastudios.commands.ReloadCommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        updateConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("lock").setExecutor(this);
        getCommand("unlock").setExecutor(this);
        getCommand("ultraitemlockreload").setExecutor(new ReloadCommand(this));

        getLogger().info("Ultra ItemLock has been enabled successfully!");
    }

    private void updateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);

        try (Reader defaultConfigStream = new InputStreamReader(getResource("config.yml"))) {
            if (defaultConfigStream != null) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
                boolean updated = false;

                for (String key : defaultConfig.getKeys(true)) {
                    if (!existingConfig.contains(key)) {
                        existingConfig.set(key, defaultConfig.get(key));
                        updated = true;
                    }
                }

                if (updated) {
                    existingConfig.save(configFile);
                    getLogger().info("Config.yml updated with missing keys.");
                } else {
                    getLogger().info("Config.yml is already up-to-date.");
                }
            }
        } catch (Exception e) {
            getLogger().severe("Failed to update config.yml: " + e.getMessage());
        }

        reloadConfig();
        this.config = getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(formatMessage("messages.must_be_player"));
            return false;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("lock")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isEmptyItem(item)) {
                sendMessage(player, "messages.no_item");
                return true;
            }
            if (isLocked(item)) {
                sendMessage(player, "messages.already_locked");
                return true;
            }
            lockItem(item);
            sendMessage(player, "messages.locked");
        } else if (label.equalsIgnoreCase("unlock")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isEmptyItem(item)) {
                sendMessage(player, "messages.no_item");
                return true;
            }
            if (!isLocked(item)) {
                sendMessage(player, "messages.not_locked");
                return true;
            }
            unlockItem(item);
            sendMessage(player, "messages.unlocked");
        }
        return true;
    }

    private void lockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList("locked_lore")) {
                lore.add(formatMessage(line));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    private void unlockItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(null);
            item.setItemMeta(meta);
        }
    }

    private boolean isLocked(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> lockedLore = config.getStringList("locked_lore");
                for (String line : lockedLore) {
                    if (lore.contains(ChatColor.stripColor(formatMessage(line)))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isEmptyItem(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isLocked(item)) {
            event.setCancelled(true);
            sendMessage(event.getPlayer(), "messages.cannot_drop");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getCurrentItem();
        if (isLocked(item)) {
            event.setCancelled(true);
            sendMessage((Player) event.getWhoClicked(), "messages.cannot_move");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType().isBlock() && isLocked(item)) {
            event.setCancelled(true);
            sendMessage(event.getPlayer(), "messages.cannot_place");
        }
    }

    private String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix")) + " ";
    }

    private String formatMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(key, key));
    }

    private void sendMessage(Player player, String key) {
        player.sendMessage(getPrefix() + formatMessage(key));
    }
}
