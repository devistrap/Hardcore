package nl.devistrap.hardcore.service;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.devistrap.hardcore.Hardcore;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

public class PlaceholderApi extends PlaceholderExpansion {

    private final Hardcore plugin;

    public PlaceholderApi(Hardcore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "BetterHardcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Devistrap";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getConfig().getString("version", "1.0.0");
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(org.bukkit.entity.Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equalsIgnoreCase("deathban_status")) {
            boolean isBanned = plugin.getDatabaseManager().isPlayerBanned(player);
            return isBanned ? "Banned" : "Not Banned";
        }

        if(identifier.equalsIgnoreCase("grace_time_left")) {
            long timeplayed = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
            long gracePeriodDuration = plugin.getDatabaseManager().getGracePeriod(player.getName()) * 60000;
            long timeLeft = gracePeriodDuration - timeplayed;
            if (timeLeft > 0) {
                return String.valueOf(timeLeft / 60000);
            } else {
                return "0";
            }
        }

        if(identifier.equalsIgnoreCase("grace_status")) {
            long timeplayed = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
            long gracePeriodDuration = plugin.getDatabaseManager().getGracePeriod(player.getName()) * 60000;
            long timeLeft = gracePeriodDuration - timeplayed;
            return timeLeft > 0 ? "In Grace Period" : "No Grace Period";
        }

        if(identifier.equalsIgnoreCase("permanent_deathban_status")) {
            boolean isPermanent = plugin.getConfig().getBoolean("settings.permanent-deathban");
            return isPermanent ? "Enabled" : "Disabled";
        }

        if(identifier.equalsIgnoreCase("deathban_duration")) {
            return plugin.getConfig().getString("settings.deathban-duration");
        }

        if (identifier.equalsIgnoreCase("kills")) {
            return (player.getStatistic(Statistic.PLAYER_KILLS) + " Kills");
        }

        if( identifier.equalsIgnoreCase("deaths")) {
            return (player.getStatistic(Statistic.DEATHS) + " Deaths");
        }
        return null;
    }
}
