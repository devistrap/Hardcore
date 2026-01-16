package nl.devistrap.hardcore.service;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import nl.devistrap.hardcore.Hardcore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class CommandExecutor {

    private final Hardcore plugin;

    public CommandExecutor(Hardcore plugin) {
        this.plugin = plugin;
    }

    public void executeCommands(String playerName, @Nullable String duration) {
        List<String> commands = plugin.getConfig().getStringList("velocity-module.commands-to-execute");
        Player player = Bukkit.getPlayer(playerName);

        for (String command : commands) {
            String processedCommand = command
                    .replace("{player}", playerName != null ? playerName : "")
                    .replace("{duration}", duration != null ? duration : "");

            plugin.getLogger().info("Executing command: " + processedCommand);

        }
    }


    public void SendCommand(String server, Player player) {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot send offline player: " + (player != null ? player.getName() : "null"));
            return;
        }
        sendToServerViaBungeeCord(player, server);
    }

    private void sendToServerViaBungeeCord(Player player, String serverName) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(serverName);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            plugin.getLogger().info("Sent " + player.getName() + " to server: " + serverName + " via BungeeCord");

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to send player via BungeeCord: " + e.getMessage());
        }
    }

    public void executeCommands(String playerName) {
        executeCommands(playerName, "");
    }
}