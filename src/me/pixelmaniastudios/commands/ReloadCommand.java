package me.pixelmaniastudios.commands;

import me.pixelmaniastudios.itemlock.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Main plugin;

    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemlock.reload")) {
            sender.sendMessage(formatMessage("messages.no_permission"));
            return true;
        }

        plugin.reloadConfig(); // Reload the config
        sender.sendMessage(formatMessage("messages.reload_success"));
        return true;
    }

    private String formatMessage(String key) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix", "&7[&6ItemLock&7] ")) + " ";
        String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(key, key));
        return prefix + message;
    }
}
