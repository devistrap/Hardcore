package nl.devistrap.hardcore.service;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.command.CommandSource;
import nl.devistrap.hardcore.Hardcore;

import javax.annotation.Nullable;
import java.util.List;

public class VelocityCommandExecutor {

    private final Hardcore plugin;
    private final ProxyServer proxy;

    public VelocityCommandExecutor(ProxyServer proxy, Hardcore plugin) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    public void executeCommands(@Nullable String playerName) {
        CommandSource console = proxy.getConsoleCommandSource();
        List<String> commands = plugin.getConfig().getStringList("velocity-module.commands-to-execute");
        for (String command : commands) {
            command = command.replace("{player}", playerName != null ? playerName : "");
            proxy.getCommandManager().executeAsync(console, command);
        }
    }
}