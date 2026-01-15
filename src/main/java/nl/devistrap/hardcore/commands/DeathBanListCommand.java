package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.objects.playerFromDb;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DeathBanListCommand implements CommandExecutor {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;
    public DeathBanListCommand(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getCommand("deathbanlist").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("hardcore.deathbanlist")) {
            commandSender.sendMessage("Banned players:");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
            for (playerFromDb playerInfo : dbManager.getAllBannedPlayers()) {
                if(playerInfo.getBanTime() == null) {
                    commandSender.sendMessage("- " + playerInfo.getPlayerName() + " | Banned permanently");
                    continue;
                }
                String formattedBanTime = formatter.format(Instant.ofEpochMilli(Long.parseLong(playerInfo.getBanTime())));
                commandSender.sendMessage("- " + playerInfo.getPlayerName() + " | Banned until: " + formattedBanTime);
            }
            return true;
        } else {
            commandSender.sendMessage("You do not have permission to use this command.");
            return true;
        }
    }
}
