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
                commandSender.sendMessage(utils.color("&e You have " + timeLeft / 60000 + " minutes of grace left.", true));
            } else {
                commandSender.sendMessage(utils.color("&e You have no active grace period.", true));
            }
        }
        String action = strings[0];
         if(action.equalsIgnoreCase("check")) {
             if(!commandSender.hasPermission("hardcore.grace.check")){
                 commandSender.sendMessage(utils.color("&cYou do not have permission to use this command.", true));
                 return true;
             }
             if (strings.length == 2) {
                 long timeplayed = Bukkit.getPlayer(strings[1]).getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
                 long gracePeriodDuration = dbManager.getGracePeriod(strings[1]) * 60000;
                 long timeLeft = gracePeriodDuration - timeplayed;
                 if (timeLeft > 0) {
                     commandSender.sendMessage(utils.color("&e" + strings[1] + " has " + timeLeft / 60000 + " minutes of grace period left.", true));
                 } else {
                     commandSender.sendMessage(utils.color("&e" + strings[1] + " does not have an active grace period.", true));
                 }
            }
             else {
                 commandSender.sendMessage(utils.color("&cPlease specify a player to check.", true));
             }
        } else if(action.equalsIgnoreCase("remove")) {
             if(!commandSender.hasPermission("hardcore.admin")){
                 commandSender.sendMessage(utils.color("&cYou do not have permission to use this command.", true));
                 return true;
             }
            dbManager.removeGracePeriod(strings[1]);
            commandSender.sendMessage(utils.color("&eRemoved grace period for player: " + strings[1], true));
             if (plugin.getConfig().getBoolean("discord-webhook.notify-on.grace-remove.enabled")) {
                 DiscordWebhookNotifier.sendWebhookNotification(strings[1] + "'s grace has been removed by " + commandSender.getName(), strings[1], plugin.getConfig().getBoolean("discord-webhook.notify-on.grace-remove.ping-role"));
             }
        } else {
            commandSender.sendMessage(utils.color("&eInvalid action. Usage: /grace <check|remove> [player]", true));
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
