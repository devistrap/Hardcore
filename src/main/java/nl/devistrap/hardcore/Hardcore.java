package nl.devistrap.hardcore;

import net.luckperms.api.LuckPerms;
import nl.devistrap.hardcore.commands.*;
import nl.devistrap.hardcore.events.JoinEvent;
import nl.devistrap.hardcore.events.deathEvent;
import nl.devistrap.hardcore.service.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class Hardcore extends JavaPlugin {




    private DatabaseManager databaseManager;
    private CommandExecutor commandExecutor;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Hardcore plugin has been enabled!");
        getConfig().set("version", getDescription().getVersion());
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        new utils(this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            utils.lpapi = provider.getProvider();
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.commandExecutor = new CommandExecutor(this);

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

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}
