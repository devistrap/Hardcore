package nl.devistrap.hardcore.events;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.utils;
import nl.devistrap.hardcore.service.Messages;
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
        if(event.getPlayer().hasPermission("hardcore.bypass")){
            return;
        }
        if(dbManager.isPlayerBanned(event.getPlayer())){
            if(plugin.getConfig().getBoolean("velocity-module.enabled")) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
                    if (player != null && player.isOnline()) {
                        plugin.getLogger().info("Sending deathbanned player " + player.getName() + " to " + plugin.getConfig().getString("velocity-module.server-to-send"));
                        if(plugin.getConfig().getString("velocity-module.server-to-send") != null && !plugin.getConfig().getString("velocity-module.server-to-send").isEmpty()) {
                            plugin.getCommandExecutor().SendCommand(plugin.getConfig().getString("velocity-module.server-to-send"), player);
                            plugin.getCommandExecutor().executeCommands(player.getName());
                        }
                        else {
                            plugin.getLogger().warning("Player went offline before commands could be executed");
                        }
                    }
                }, 20L);
            }
            else{
                event.getPlayer().kickPlayer(Messages.textNoPrefix("deathban_kick_generic"));
            }
        }
        else{
            dbManager.addGracePeriod(event.getPlayer().getName(), new Timestamp(plugin.getConfig().getInt("settings.grace-period-duration") ));
            utils.addPermission(utils.lpapi.getUserManager().getUser(event.getPlayer().getUniqueId()), "hardcore.ingraceperiod");
            plugin.getLogger().info("Added grace period to player " + event.getPlayer().getName());
        }
    }
}