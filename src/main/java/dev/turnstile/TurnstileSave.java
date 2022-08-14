package dev.turnstile;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

import java.util.ArrayList;

import org.bukkit.Material;



public class TurnstileSave {

    static FileConfiguration config = MyPlugin.plugin.getConfig(); 

    public static boolean Save(TurnstileData data) {

        config.set("turnstiles." + data.id + ".material", data.material.toString());
        config.set("turnstiles." + data.id + ".price", data.price);
        config.set("turnstiles." + data.id + ".coords.x", data.coords.x);
        config.set("turnstiles." + data.id + ".coords.y", data.coords.y);
        config.set("turnstiles." + data.id + ".coords.z", data.coords.z);

        MyPlugin.plugin.saveConfig();

        return true;
    }

    public static List<TurnstileData> DataInit() {

        if (config.get("turnstiles") == null) {
            return null;
        }

        List<TurnstileData> returned_data = new ArrayList<>();

        for (String key : config.getConfigurationSection("turnstiles").getKeys(false)) {
            TurnstileData data = new TurnstileData();

            data.id = Integer.parseInt(key);

            data.material = Material.valueOf(config.getString("turnstiles." + key + ".material"));
            data.price = config.getInt("turnstiles." + key + ".price");
            data.coords.x = config.getInt("turnstiles." + key + ".coords.x");
            data.coords.y = config.getInt("turnstiles." + key + ".coords.y");
            data.coords.z = config.getInt("turnstiles." + key + ".coords.z");

            returned_data.add(data);
        }

        return returned_data;
    }
}