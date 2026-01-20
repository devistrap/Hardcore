package nl.devistrap.hardcore;

import net.luckperms.api.LuckPerms;
import nl.devistrap.hardcore.commands.*;
import nl.devistrap.hardcore.events.JoinEvent;
import nl.devistrap.hardcore.events.deathEvent;
import nl.devistrap.hardcore.service.CommandExecutor;
import nl.devistrap.hardcore.service.Messages;
import nl.devistrap.hardcore.service.PlaceholderApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class Hardcore extends JavaPlugin {




    private DatabaseManager databaseManager;
    private CommandExecutor commandExecutor;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Hardcore plugin has been enabled!");
        getConfig().set("version", getDescription().getVersion());


        Messages.init(this);
        new utils(this);
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();


        if (Bukkit.getServicesManager().getRegistration(LuckPerms.class) != null) {
            utils.lpapi = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderApi(this).register();
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.commandExecutor = new CommandExecutor(this);

        new HardCoreCommand(this);
        new DeathBanCommand(this);
        new DeathBanListCommand(this);
        new ReviveCommand(this);
        new GraceCommand(this);
        new PvpCommand(this);

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
