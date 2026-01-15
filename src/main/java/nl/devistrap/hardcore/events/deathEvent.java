package nl.devistrap.hardcore.events;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.sql.Timestamp;

public class deathEvent implements Listener {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;

    public deathEvent(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        if(!plugin.getConfig().getBoolean("settings.permanent-deathban")) {
            String timeBanned = plugin.getConfig().getString("settings.deathban-duration");
            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Player " + event.getEntity().getName() + " has been deathbanned", event.getEntity().getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.ping-role"));
            }
            dbManager.deathBanPlayer(event.getEntity(), new Timestamp(System.currentTimeMillis() + Integer.parseInt(timeBanned) * 60 * 1000));
            event.getEntity().kickPlayer("You have been deathbanned for " + timeBanned + " minutes.");
        }
        else{
            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Player " + event.getEntity().getName() + " has been permanently deathbanned", event.getEntity().getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.ping-role"));
            }
            dbManager.deathBanPlayer(event.getEntity(), null);
            event.getEntity().kickPlayer("You have been permanently deathbanned.");
        }
    }

}
