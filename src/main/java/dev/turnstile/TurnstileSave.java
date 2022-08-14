package dev.turnstile;

import java.util.List;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;



public class TurnstileSave {

    public static boolean Save(TurnstileData data) {

        TurnstileRenewed.config.set("turnstiles." + data.id + ".material", data.material.toString());
        TurnstileRenewed.config.set("turnstiles." + data.id + ".price", data.price);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".coords.x", data.coords.x);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".coords.y", data.coords.y);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".coords.z", data.coords.z);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".world", data.world);

        TurnstileRenewed.plugin.saveConfig();

        return true;
    }

    public static boolean Remove(TurnstileData data) {

            TurnstileRenewed.GetData().remove(data);
            TurnstileRenewed.config.set("turnstiles." + data.id, null);
            TurnstileRenewed.plugin.saveConfig();
    
            return true;
    }

    public static List<TurnstileData> DataInit() {

        if (TurnstileRenewed.config.get("turnstiles") == null) {
            return null;
        }

        List<TurnstileData> returned_data = new ArrayList<>();

        for (String key : TurnstileRenewed.config.getConfigurationSection("turnstiles").getKeys(false)) {
            TurnstileData data = new TurnstileData();

            data.id = Integer.parseInt(key);

            data.material = Material.valueOf(TurnstileRenewed.config.getString("turnstiles." + key + ".material"));
            data.price = TurnstileRenewed.config.getInt("turnstiles." + key + ".price");
            data.coords.x = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.x");
            data.coords.y = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.y");
            data.coords.z = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.z");
            data.world = TurnstileRenewed.config.getString("turnstiles." + key + ".world");

            Block block = TurnstileRenewed.plugin.getServer().getWorld(data.world).getBlockAt(data.coords.x, data.coords.y, data.coords.z);

            block.setType(data.material);

            returned_data.add(data);
        }



        return returned_data;
    }
}