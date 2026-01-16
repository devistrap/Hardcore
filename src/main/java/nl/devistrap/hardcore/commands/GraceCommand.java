package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import nl.devistrap.hardcore.service.Messages;
import nl.devistrap.hardcore.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;

public class GraceCommand implements CommandExecutor, TabExecutor {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;

    public GraceCommand(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getCommand("grace").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            long timeplayed = Bukkit.getPlayer(commandSender.getName()).getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
            long gracePeriodDuration = dbManager.getGracePeriod(strings[1]) * 60000;
            long timeLeft = gracePeriodDuration - timeplayed;
            if (timeLeft > 0) {
                Messages.send(commandSender, "grace_self_check", "minutes", timeLeft / 60000);
            } else {
                Messages.send(commandSender, "grace_self_none");
            }
        }
        String action = strings[0];
         if(action.equalsIgnoreCase("check")) {
             if(!commandSender.hasPermission("hardcore.grace.check")){
                 Messages.send(commandSender, "no_permission");
                 return true;
             }
             if (strings.length == 2) {
                 long timeplayed = Bukkit.getPlayer(strings[1]).getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
                 long gracePeriodDuration = dbManager.getGracePeriod(strings[1]) * 60000;
                 long timeLeft = gracePeriodDuration - timeplayed;
                 if (timeLeft > 0) {
                     Messages.send(commandSender, "grace_player_check", "player", strings[1], "minutes", timeLeft / 60000);
                 } else {
                     Messages.send(commandSender, "grace_player_none", "player", strings[1]);
                 }
            }
             else {
                 Messages.send(commandSender, "grace_missing_player");
             }
        } else if(action.equalsIgnoreCase("remove")) {
             if(!commandSender.hasPermission("hardcore.admin")){
                 Messages.send(commandSender, "no_permission");
                 return true;
             }
            dbManager.removeGracePeriod(strings[1]);
            Messages.send(commandSender, "grace_removed", "player", strings[1]);
             if (plugin.getConfig().getBoolean("discord-webhook.notify-on.grace-remove.enabled")) {
                 DiscordWebhookNotifier.sendWebhookNotification(strings[1] + "'s grace has been removed by " + commandSender.getName(), strings[1], plugin.getConfig().getBoolean("discord-webhook.notify-on.grace-remove.ping-role"));
             }
        } else {
            Messages.send(commandSender, "grace_invalid_action");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            if(!commandSender.hasPermission("hardcore.admin")){
                return List.of("check");
            }
            return List.of("check", "remove");
        }
        if (strings.length == 2) {
            if(commandSender.hasPermission("hardcore.admin")){
                return Bukkit.getOnlinePlayers().stream()
                        .map(player -> player.getName())
                        .toList();
            }
        }
        return null;
    }
}
