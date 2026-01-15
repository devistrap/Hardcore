package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import nl.devistrap.hardcore.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReviveCommand implements CommandExecutor, TabExecutor {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;
    private final DiscordWebhookNotifier webhookNotifier;

    public ReviveCommand(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        this.webhookNotifier = new DiscordWebhookNotifier(plugin);
        plugin.getCommand("revive").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!commandSender.hasPermission("hardcore.admin")) {
            commandSender.sendMessage(utils.color("&cYou do not have permission to use this command.", true));
            return true;
        }

        if(args.length == 1) {
            String targetPlayerName = args[0];
            dbManager.revivePlayer(targetPlayerName);
            utils.broadcast(utils.color("Player " + targetPlayerName + " has been revived.", true));
            if(plugin.getConfig().getBoolean("discord-webhook.notify-on.revive.enabled")){
                DiscordWebhookNotifier.sendWebhookNotification("Player " + targetPlayerName + " has been revived by " + commandSender.getName(), targetPlayerName, plugin.getConfig().getBoolean("discord-webhook.notify-on.revive.ping-role"));
            }
            return true;
        }
        else{
            commandSender.sendMessage("Usage: /revive <player>");
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return dbManager.getAllBannedPlayers().stream()
                    .map(player -> player.getPlayerName())
                    .toList();
        }
        return null;
    }
}
