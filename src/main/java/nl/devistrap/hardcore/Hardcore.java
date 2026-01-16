package nl.devistrap.hardcore;

import com.velocitypowered.api.proxy.ProxyServer;
import nl.devistrap.hardcore.commands.*;
import nl.devistrap.hardcore.events.JoinEvent;
import nl.devistrap.hardcore.events.deathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.net.Proxy;

public final class Hardcore extends JavaPlugin {


    private final ProxyServer proxy;

    @Inject
    public Hardcore(ProxyServer proxy) {
        this.proxy = proxy;
    }

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Hardcore plugin has been enabled!");
        getConfig().set("version", getDescription().getVersion());
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        new utils(this);

        new HardCoreCommand(this);
        new DeathBanCommand(this);
        new DeathBanListCommand(this);
        new ReviveCommand(this);
        new GraceCommand(this);

        new JoinEvent(this);
        new deathEvent(this);

    }

    @Override
    public void onDisable() {
        getLogger().info("Hardcore plugin has been enabled!");
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ProxyServer getProxy() {
        return proxy;
    }
}
