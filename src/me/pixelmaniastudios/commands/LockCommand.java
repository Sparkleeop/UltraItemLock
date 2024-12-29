package me.pixelmaniastudios.commands;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;

import me.pixelmaniastudios.itemlock.Main;
import me.pixelmaniastudios.utility.ItemUtils;

public class LockCommand implements CommandExecutor {

    private final Main plugin;

    public LockCommand(Main plugin) {
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

        if (ItemUtils.isLocked(item)) {
            player.sendMessage(plugin.getConfig().getString("messages.already_locked"));
            return false;
        }

        ItemUtils.lockItem(item);
        player.sendMessage(plugin.getConfig().getString("messages.locked"));
        return true;
    }
}
