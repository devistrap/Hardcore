package nl.devistrap.hardcore.events;

import com.velocitypowered.api.proxy.ProxyServer;
import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.VelocityCommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;

public class JoinEvent implements Listener {

    private final Hardcore plugin;
    private final DatabaseManager dbManager;
    private final ProxyServer proxy;

    public JoinEvent(Hardcore plugin) {
        this.plugin = plugin;
        this.proxy = plugin.getProxy();
        this.dbManager = plugin.getDatabaseManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(dbManager.isPlayerBanned(event.getPlayer())){
            event.getPlayer().kickPlayer("You are deathbanned!");
            VelocityCommandExecutor executor = new VelocityCommandExecutor(proxy, plugin);
            executor.executeCommands(event.getPlayer().getName());
        }
        else{
            dbManager.addGracePeriod(event.getPlayer().getName(), new Timestamp(plugin.getConfig().getInt("settings.grace-period-duration") ));
            plugin.getLogger().info("Added grace period to player " + event.getPlayer().getName());
        }

    }


}
