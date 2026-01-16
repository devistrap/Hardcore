package nl.devistrap.hardcore.events;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;

public class JoinEvent implements Listener {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;


    public JoinEvent(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(dbManager.isPlayerBanned(event.getPlayer())){
            if(plugin.getConfig().getBoolean("velocity-module.enabled")) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
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
                event.getPlayer().kickPlayer("You are deathbanned!");
            }
        }
        else{
            dbManager.addGracePeriod(event.getPlayer().getName(), new Timestamp(plugin.getConfig().getInt("settings.grace-period-duration") ));
            plugin.getLogger().info("Added grace period to player " + event.getPlayer().getName());
        }
    }
}