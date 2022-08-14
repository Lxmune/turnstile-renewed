package dev.turnstile;

import java.util.List;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;



public class TurnstileSave {

    public static boolean Save(TurnstileData data) {

        MyPlugin.config.set("turnstiles." + data.id + ".material", data.material.toString());
        MyPlugin.config.set("turnstiles." + data.id + ".price", data.price);
        MyPlugin.config.set("turnstiles." + data.id + ".coords.x", data.coords.x);
        MyPlugin.config.set("turnstiles." + data.id + ".coords.y", data.coords.y);
        MyPlugin.config.set("turnstiles." + data.id + ".coords.z", data.coords.z);
        MyPlugin.config.set("turnstiles." + data.id + ".world", data.world);

        MyPlugin.plugin.saveConfig();

        return true;
    }

    public static boolean Remove(TurnstileData data) {

            MyPlugin.GetData().remove(data);
            MyPlugin.config.set("turnstiles." + data.id, null);
            MyPlugin.plugin.saveConfig();
    
            return true;
    }

    public static List<TurnstileData> DataInit() {

        if (MyPlugin.config.get("turnstiles") == null) {
            return null;
        }

        List<TurnstileData> returned_data = new ArrayList<>();

        for (String key : MyPlugin.config.getConfigurationSection("turnstiles").getKeys(false)) {
            TurnstileData data = new TurnstileData();

            data.id = Integer.parseInt(key);

            data.material = Material.valueOf(MyPlugin.config.getString("turnstiles." + key + ".material"));
            data.price = MyPlugin.config.getInt("turnstiles." + key + ".price");
            data.coords.x = MyPlugin.config.getInt("turnstiles." + key + ".coords.x");
            data.coords.y = MyPlugin.config.getInt("turnstiles." + key + ".coords.y");
            data.coords.z = MyPlugin.config.getInt("turnstiles." + key + ".coords.z");
            data.world = MyPlugin.config.getString("turnstiles." + key + ".world");

            Block block = MyPlugin.plugin.getServer().getWorld(data.world).getBlockAt(data.coords.x, data.coords.y, data.coords.z);

            block.setType(data.material);

            returned_data.add(data);
        }



        return returned_data;
    }
}