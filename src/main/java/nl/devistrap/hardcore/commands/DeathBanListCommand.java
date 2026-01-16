package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.objects.playerFromDb;
import nl.devistrap.hardcore.service.Messages;
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
        if (commandSender.hasPermission("hardcore.admin")) {
            Messages.send(commandSender, "deathban_list_header");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
            for (playerFromDb playerInfo : dbManager.getAllBannedPlayers()) {
                if(playerInfo.getBanTime() == null) {
                    Messages.send(commandSender, "deathban_list_line_permanent", "player", playerInfo.getPlayerName());
                    continue;
                }
                String formattedBanTime = formatter.format(Instant.ofEpochMilli(Long.parseLong(playerInfo.getBanTime())));
                Messages.send(commandSender, "deathban_list_line_until", "player", playerInfo.getPlayerName(), "until", formattedBanTime);
            }
            return true;
        } else {
            Messages.send(commandSender, "no_permission");
            return true;
        }
    }
}
