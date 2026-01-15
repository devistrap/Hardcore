package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
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
            commandSender.sendMessage(utils.color("&eUsage: /grace <set|check|remove> [player]", true));
            return true;
        }
        String action = strings[0];
         if(action.equalsIgnoreCase("check")) {
            long timeplayed = Bukkit.getPlayer(strings[1]).getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
            long gracePeriodDuration = dbManager.getGracePeriod(strings[1]) * 60000;
            long timeLeft = gracePeriodDuration - timeplayed;
            if (timeLeft > 0) {
                commandSender.sendMessage( utils.color("&e" + strings[1] + " has " + timeLeft / 60000 + " minutes of grace period left.", true));
            } else {
                commandSender.sendMessage(utils.color("&e" + strings[1] + " does not have an active grace period.", true));
            }
        } else if(action.equalsIgnoreCase("remove")) {
            dbManager.removeGracePeriod(strings[1]);
            commandSender.sendMessage(utils.color("&eRemoved grace period for player: " + strings[1], true));
        } else {
            commandSender.sendMessage(utils.color("&eInvalid action. Usage: /grace <set|check|remove> [player]", true));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            return List.of("check", "remove");
        }
        if (strings.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName())
                    .toList();
        }
        return null;
    }
}
