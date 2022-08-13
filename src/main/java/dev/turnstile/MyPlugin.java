package dev.turnstile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Init
        getLogger().log(Level.INFO, "The plugin has been enabled. Reminder that this plugin is still in development, so there might be unexpected bugs.");
        
        // Registering the command executor
        this.getCommand("turnstile").setExecutor(new TurnstileCommand());

        // Registering the event listener
        this.getServer().getPluginManager().registerEvents(new TurnstileEvent(), this);
    }

    // Creating the fence var (list of fence blocks)
    static List<TurnstileData> stored_data = new ArrayList<>();
    
    public static List<TurnstileData> GetData() {
        return stored_data;
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "The plugin has been disabled. Goodbye!");
    }
}