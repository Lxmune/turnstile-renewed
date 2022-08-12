package dev.turnstile;

import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "The plugin has been enabled. Reminder that this plugin is still in development, so there might be unexpected bugs.");

        // Registering the command executor
        this.getCommand("turnstile").setExecutor(new TurnstileCommand());
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "The plugin has been disabled. Goodbye!");
    }
}