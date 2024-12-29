package me.pixelmaniastudios.commands;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;

import me.pixelmaniastudios.utility.ItemUtils;
import me.pixelmaniastudios.itemlock.Main;

public class UnlockCommand implements CommandExecutor {

    private final Main plugin;

    public UnlockCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.must_be_player"));
            return false;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage(plugin.getConfig().getString("messages.no_item"));
            return false;
        }

        if (!ItemUtils.isLocked(item)) {
            player.sendMessage(plugin.getConfig().getString("messages.not_locked"));
            return false;
        }

        ItemUtils.unlockItem(item);
        player.sendMessage(plugin.getConfig().getString("messages.unlocked"));
        return true;
    }
}
