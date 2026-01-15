package nl.devistrap.hardcore.events;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        if(dbManager.isPlayerBanned(event.getPlayer())){
            event.getPlayer().kickPlayer("You are deathbanned!");
        }
        else{
            dbManager.addGracePeriod(event.getPlayer().getName(), new Timestamp(plugin.getConfig().getInt("settings.grace-period-duration") ));
            plugin.getLogger().info("Added grace period to player " + event.getPlayer().getName());
        }

    }


}
