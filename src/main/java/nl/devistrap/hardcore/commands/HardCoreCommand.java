package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HardCoreCommand implements CommandExecutor, TabExecutor {

    private final Hardcore plugin;

    public HardCoreCommand(Hardcore plugin) {
        this.plugin = plugin;
        plugin.getCommand("hardcore").setExecutor(this);
        plugin.getCommand("hardcore").setTabCompleter(this);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        String option = args[0];
        if (option.equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            commandSender.sendMessage(utils.color("&eHardcore configuration reloaded."));
            return true;
        } else if (option.equalsIgnoreCase("status")) {
            commandSender.sendMessage("&eHardcore plugin is running.");
            return true;
        } else if (option.equalsIgnoreCase("version")) {
            commandSender.sendMessage("&eHardcore plugin version: " + plugin.getDescription().getVersion());
            return true;
        } else if (option.equalsIgnoreCase("permanent")) {
            if (args.length != 2) {
                commandSender.sendMessage(utils.color("&cUsage: /hardcore permanent <on|off>"));
                return true;
            }
            String state = args[1];
            if (state.equalsIgnoreCase("on")) {
                plugin.getConfig().set("settings.permanent-deathban", true);
                plugin.saveConfig();
                commandSender.sendMessage(utils.color("&aPermanent deathban enabled."));
                return true;
            } else if (state.equalsIgnoreCase("off")) {
                plugin.getConfig().set("settings.permanent-deathban", false);
                plugin.saveConfig();
                plugin.reloadConfig();
                commandSender.sendMessage(utils.color("&aPermanent deathban disabled."));
                return true;
            } else {
                commandSender.sendMessage(utils.color("&cUsage: /hardcore permanent <on|off>"));
                return true;
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("reload", "status", "help", "version", "permanent");
        }
        if (args.length == 2 ) {
            return List.of("on", "off");
        }
        return null;
    }
}
