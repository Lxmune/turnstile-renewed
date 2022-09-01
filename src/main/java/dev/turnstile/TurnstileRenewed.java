package dev.turnstile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class TurnstileRenewed extends JavaPlugin {

    public static String prefix = "§6[§eTurnstile§6]§r ";

    public static Plugin plugin = null;

    public static Economy economy;

    public static FileConfiguration config;

    // Loading the messages file
    public static File messagesFile;
    public static FileConfiguration messagesConfig;

    // Creating the fence var (list of fence blocks)
    static List<TurnstileData> stored_data = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;

        // Initializing economy
        if (!setupEconomy() ) {
            getLogger().log(Level.INFO, String.format("No Vault dependency found! Disabling the economy system."));
            economy = null;
        }

        messagesFile = new File(this.getDataFolder(), "messages.yml");

        // Check if the messages file exists
        if (!messagesFile.exists()) {
            getLogger().log(Level.INFO, "The messages file does not exist, creating it...");
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Init
        getLogger().log(Level.INFO, "The plugin has been enabled. Reminder that this plugin is still in development, so there might be unexpected bugs.");
        
        // Registering the command executor
        this.getCommand("turnstile").setExecutor(new TurnstileCommand());

        // Registering the event listener
        this.getServer().getPluginManager().registerEvents(new TurnstileEvent(), this);

        // Initializing the data
        config = TurnstileRenewed.plugin.getConfig();

        // Initializing the config
        config.addDefault("next_id", 0);

        stored_data = TurnstileSave.DataInit();

        if (stored_data == null) {
            getLogger().log(Level.INFO, "No data found, creating new data.");
            stored_data = new ArrayList<>();
        }

        System.out.printf("%d turnstiles found.\n", stored_data.size());
    }
    
    public static List<TurnstileData> GetData() {
        return stored_data;
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "The plugin has been disabled. Goodbye!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}