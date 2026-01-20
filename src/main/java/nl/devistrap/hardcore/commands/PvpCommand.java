package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.Hardcore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PvpCommand implements CommandExecutor, TabExecutor {

    private final Hardcore plugin;
    public PvpCommand(Hardcore plugin) {
        this.plugin = plugin;
        plugin.getCommand("pvp").setExecutor(this);
        plugin.getCommand("pvp").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("enable", "disable", "status");
        }
        if( args.length == 2 && (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))) {
            return Bukkit.getWorlds().stream().map(world -> world.getName()).toList();
        }
        return null;
    }
}
