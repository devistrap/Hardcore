package nl.devistrap.hardcore.events;


import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.DiscordWebhookNotifier;
import nl.devistrap.hardcore.service.Messages;
import nl.devistrap.hardcore.service.CommandExecutor;
import nl.devistrap.hardcore.utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        if(event.getEntity().getPlayer().hasPermission("hardcore.bypass")){
            return;
        }
        if(!plugin.getConfig().getBoolean("settings.permanent-deathban")) {

            long timeplayed = Bukkit.getPlayer(event.getEntity().getName()).getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
            long gracePeriodDuration = dbManager.getGracePeriod(event.getEntity().getName()) * 60000;
            long timeLeft = gracePeriodDuration - timeplayed;
            if (timeLeft > 0) {
                Messages.send(event.getEntity(), "grace_death_prevented", "minutes", timeLeft / 60000);
                return;
            }
            String timeBanned = plugin.getConfig().getString("settings.deathban-duration");
            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Player " + event.getEntity().getName() + " has been deathbanned", event.getEntity().getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.ping-role"));
            }
            dbManager.deathBanPlayer(event.getEntity(), new Timestamp(System.currentTimeMillis() + Integer.parseInt(timeBanned) * 60 * 1000));
            utils.addPermission(utils.lpapi.getUserManager().getUser(event.getEntity().getUniqueId()), "hardcore.deathbanned");
            if(plugin.getConfig().getBoolean("settings.deathban-sound.enabled")) {
                utils.PlaySoundEveryone(plugin.getConfig().getString("settings.deathban-sound.sound"));
            }
            if(plugin.getConfig().getBoolean("velocity-module.enabled")) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
                    if (player != null && player.isOnline()) {
                        plugin.getLogger().info("Sending deathbanned player " + player.getName() + " to " + plugin.getConfig().getString("velocity-module.server-to-send"));
                        plugin.getCommandExecutor().executeCommands(player.getName());
                        if(plugin.getConfig().getString("velocity-module.server-to-send") != null && !plugin.getConfig().getString("velocity-module.server-to-send").isEmpty()) {
                            plugin.getCommandExecutor().SendCommand(plugin.getConfig().getString("velocity-module.server-to-send"), player);
                        }
                    } else {
                        plugin.getLogger().warning("Player went offline before commands could be executed");
                    }
                }, 20L);
            }
            else{
                event.getEntity().getPlayer().kickPlayer(Messages.textNoPrefix("deathban_kick_generic"));
            }
        }
        else{
            if (plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.enabled")) {
                DiscordWebhookNotifier.sendWebhookNotification("Player " + event.getEntity().getName() + " has been permanently deathbanned", event.getEntity().getName(), plugin.getConfig().getBoolean("discord-webhook.notify-on.automatic-deathban.ping-role"));
            }
            dbManager.deathBanPlayer(event.getEntity(), null);
            utils.addPermission(utils.lpapi.getUserManager().getUser(event.getEntity().getUniqueId()), "hardcore.deathbanned");
            event.getEntity().kickPlayer(Messages.textNoPrefix("deathban_kick_permanent"));
        }
    }

}
