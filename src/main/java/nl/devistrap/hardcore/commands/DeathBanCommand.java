package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;

public class DeathBanCommand implements CommandExecutor, TabExecutor {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;

    public DeathBanCommand(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getCommand("deathban").setExecutor(this);
        plugin.getCommand("deathban").setTabCompleter(this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender.hasPermission("hardcore.admin")) {
            if (args.length != 1) {
                commandSender.sendMessage("Usage: /deathban <player>");
                return true;
            }

            String targetPlayerName = args[0];
            if(!plugin.getConfig().getBoolean("settings.permanent-deathban")) {


                String timeBanned = plugin.getConfig().getString("settings.deathban-duration");
                dbManager.deathBanPlayer(Bukkit.getPlayer(targetPlayerName), new Timestamp(System.currentTimeMillis() + Integer.parseInt(timeBanned) * 60 * 1000));

                if (Bukkit.getPlayer(targetPlayerName) != null) {
                    Bukkit.getPlayer(targetPlayerName).kickPlayer("You have been deathbanned for " + timeBanned + " minutes.");
                }

                commandSender.sendMessage("Player " + targetPlayerName + " has been deathbanned for" + timeBanned + " minutes.");
                return true;
            }
            else {
                dbManager.deathBanPlayer(Bukkit.getPlayer(targetPlayerName), null);
                if (plugin.getConfig().getBoolean("discord-webhook.notify-on.manual-deathban.enabled")) {
                    DiscordWebhookNotifier.sendWebhookNotification("Player " + targetPlayerName + " has been permanently deathbanned by " + commandSender.getName(), targetPlayerName, plugin.getConfig().getBoolean("discord-webhook.notify-on.manual-deathban.ping-role"));
                }
            }

            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.manual-deathban.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Player " + targetPlayerName + " has been deathbanned by " + commandSender.getName(), targetPlayerName, plugin.getConfig().getBoolean("discord-webhook.notify-on.manual-deathban.ping-role"));
            }
            return true;
        } else {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .toList();
        }
        return null;
    }
}
