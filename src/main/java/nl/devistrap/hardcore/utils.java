package nl.devistrap.hardcore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class utils {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void broadcast(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(color(msg));
        }
    }


    public static Boolean isValidTime(String time) {
        if(!time.endsWith("s") && !time.endsWith("m") && !time.endsWith("h") && !time.endsWith("d")) {
            return false;
        }
        try {
            Integer.parseInt(time.substring(0, time.length() - 1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getTimeInSeconds(String time) {
        int multiplier = 1;
        if(time.endsWith("m")) {
            multiplier = 60;
        } else if(time.endsWith("h")) {
            multiplier = 3600;
        } else if(time.endsWith("d")) {
            multiplier = 86400;
        }
        return Integer.parseInt(time.substring(0, time.length() - 1)) * multiplier;
    }

}
