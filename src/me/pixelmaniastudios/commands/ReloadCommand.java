package me.pixelmaniastudios.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.pixelmaniastudios.itemlock.Main;


public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check if the player has permission to reload the plugin
            if (!player.hasPermission("itemlock.admin")) {
                player.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }
        }

        // Reload the configuration
        Main.getInstance().reloadConfig();
        sender.sendMessage("§aItemLock configuration reloaded successfully!");

        return true;
    }
}

