package nl.devistrap.hardcore.events;

import nl.devistrap.hardcore.DatabaseManager;
import nl.devistrap.hardcore.Hardcore;
import nl.devistrap.hardcore.service.Messages;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Hitevent implements Listener {

    private Hardcore plugin;
    private DatabaseManager dbManager;
    public Hitevent(Hardcore plugin) {
        this.plugin = plugin;
        this.dbManager = plugin.getDatabaseManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damagedPlayer = (Player) event.getEntity();
        Player damagerPlayer = null;

        if (event.getDamager() instanceof Player) {
            damagerPlayer = (Player) event.getDamager();
        }
        else if(event.getDamager() instanceof Projectile){
            Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player){
                damagerPlayer = (Player) projectile.getShooter();
            }
            else{
                return;
            }
        }

        if (damagerPlayer == null) {
            return;
        }

        if(damagedPlayer.hasPermission("hardcore.grace.bypass") || damagerPlayer.hasPermission("hardcore.grace.bypass")){
            return;
        }

        if(dbManager.getGracePeriod(damagedPlayer.getName()) * 60000 > damagedPlayer.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE)){
            Messages.send(damagedPlayer, "grace_self_active");
            event.setCancelled(true);
            nl.devistrap.hardcore.utils.removePermission(nl.devistrap.hardcore.utils.lpapi.getUserManager().getUser(damagedPlayer.getName()), "hardcore.deathbanned");
            return;
        }

        if(dbManager.getGracePeriod(damagerPlayer.getName()) * 60000 > damagerPlayer.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE)){
            Messages.send(damagerPlayer, "grace_target_active");
            event.setCancelled(true);
        }
    }
}
