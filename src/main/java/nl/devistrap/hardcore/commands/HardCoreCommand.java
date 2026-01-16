package nl.devistrap.hardcore.commands;

import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import nl.devistrap.hardcore.service.Messages;
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
        if (!commandSender.hasPermission("hardcore.admin")) {
            Messages.send(commandSender, "no_permission");
            return true;
        }

        String option = args[0];
        if (option.equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            Messages.init(plugin);
            Messages.send(commandSender, "hardcore_reloaded");
            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.config-reload.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Config has been reloaded by " + commandSender.getName(), command.getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.config-reload.ping-role"));
            }
            return true;
        } else if (option.equalsIgnoreCase("status")) {
            Messages.send(commandSender, "hardcore_running");
            String statusLabel = plugin.getConfig().getBoolean("settings.permanent-deathban") ? "&aEnabled" : "&cDisabled";
            Messages.send(commandSender, "hardcore_permanent_deathban", "status", statusLabel);
            Messages.send(commandSender, "hardcore_deathban_duration", "minutes", plugin.getConfig().getString("settings.deathban-duration"));
            return true;
        } else if (option.equalsIgnoreCase("version")) {
            Messages.send(commandSender, "hardcore_version", "version", plugin.getDescription().getVersion());
            return true;
        } else if (option.equalsIgnoreCase("permanent")) {
            if (args.length != 2) {
                Messages.send(commandSender, "hardcore_permanent_usage");
                return true;
            }
            String state = args[1];
            if (state.equalsIgnoreCase("on")) {
                plugin.getConfig().set("settings.permanent-deathban", true);
                plugin.saveConfig();
                Messages.send(commandSender, "permanent_enabled");
                if (plugin.getConfig().getBoolean("discord-webhook.notify-on.permanent-deathban.enabled")) {
                    DiscordWebhookNotifier.sendWebhookNotification("Permanent deathban has been enabled by " + commandSender.getName(), command.getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.permanent-deathban.ping-role"));
                }
                return true;
            } else if (state.equalsIgnoreCase("off")) {
                plugin.getConfig().set("settings.permanent-deathban", false);
                plugin.saveConfig();
                plugin.reloadConfig();
                Messages.send(commandSender, "permanent_disabled");
                if (plugin.getConfig().getBoolean("discord-webhook.notify-on.permanent-deathban.enabled")) {
                    DiscordWebhookNotifier.sendWebhookNotification("Permanent deathban has been disabled by " + commandSender.getName(), command.getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.permanent-deathban.ping-role"));
                }
                return true;
            } else {
                Messages.send(commandSender, "hardcore_permanent_usage");
                return true;
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender.hasPermission("hardcore.admin")) {
            if (args.length == 1) {
                return List.of("reload", "status", "help", "version", "permanent");
            }
            if (args.length == 2) {
                return List.of("on", "off");
            }
        }
        return null;
    }
}
