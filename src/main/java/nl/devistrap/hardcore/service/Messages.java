package nl.devistrap.hardcore.service;

import nl.devistrap.hardcore.Hardcore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Messages {

    private static Hardcore plugin;
    private static FileConfiguration messages;

    public static void init(Hardcore pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File msgFile = new File(dataFolder, "messages.yml");
        if (!msgFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(msgFile);
    }

    public static String text(String key, Object... kvPairs) {
        return formatInternal(key, true, kvPairs);
    }

    public static String textNoPrefix(String key, Object... kvPairs) {
        return formatInternal(key, false, kvPairs);
    }

    public static void send(Player player, String key, Object... kvPairs) {
        player.sendMessage(text(key, kvPairs));
    }

    public static void send(CommandSender sender, String key, Object... kvPairs) {
        sender.sendMessage(text(key, kvPairs));
    }

    private static String formatInternal(String key, boolean withPrefix, Object... kvPairs) {
        String template = messages != null ? messages.getString(key) : null;
        if (template == null) {
            template = key;
        }

        Map<String, String> vars = buildVars(kvPairs);
        for (Map.Entry<String, String> e : vars.entrySet()) {
            template = template.replace("{" + e.getKey() + "}", String.valueOf(e.getValue()));
        }


        String colored = ChatColor.translateAlternateColorCodes('&', template);
        if (withPrefix) {
            String prefix = plugin.getConfig().getString("prefix", "");
            colored = ChatColor.translateAlternateColorCodes('&', prefix) + colored;
        }
        return colored;
    }

    private static Map<String, String> buildVars(Object... kvPairs) {
        Map<String, String> vars = new HashMap<>();
        if (kvPairs == null) return vars;
        if (kvPairs.length == 1 && kvPairs[0] instanceof Map<?, ?> m) {
            for (Map.Entry<?, ?> e : m.entrySet()) {
                vars.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
            return vars;
        }
        for (int i = 0; i + 1 < kvPairs.length; i += 2) {
            String k = String.valueOf(kvPairs[i]);
            String v = String.valueOf(kvPairs[i + 1]);
            vars.put(k, v);
        }
        return vars;
    }
}
