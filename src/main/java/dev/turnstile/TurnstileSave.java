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
        TurnstileRenewed.config.set("turnstiles." + data.id + ".delay", data.delay);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".owner", data.owner);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".owner_name", data.owner_name);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".command", data.command);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".item.name", data.item.name);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".item.amount", data.item.amount);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".item.type", data.item.type);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".item.lore", data.item.lore);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".direction.north", data.direction.north);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".direction.south", data.direction.south);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".direction.east", data.direction.east);
        TurnstileRenewed.config.set("turnstiles." + data.id + ".direction.west", data.direction.west);

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
            data.price = TurnstileRenewed.config.getDouble("turnstiles." + key + ".price");
            data.coords.x = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.x");
            data.coords.y = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.y");
            data.coords.z = TurnstileRenewed.config.getInt("turnstiles." + key + ".coords.z");
            data.world = TurnstileRenewed.config.getString("turnstiles." + key + ".world");
            data.delay = TurnstileRenewed.config.getDouble("turnstiles." + key + ".delay");
            data.owner = TurnstileRenewed.config.getString("turnstiles." + key + ".owner");
            data.owner_name = TurnstileRenewed.config.getString("turnstiles." + key + ".owner_name");
            data.command = TurnstileRenewed.config.getString("turnstiles." + key + ".command");
            data.item.type = TurnstileRenewed.config.getString("turnstiles." + key + ".item.type");
            data.item.amount = TurnstileRenewed.config.getInt("turnstiles." + key + ".item.amount");
            data.item.name = TurnstileRenewed.config.getString("turnstiles." + key + ".item.name");
            // Type mismatch: cannot convert from String to List<String>
            // Fix:
            data.item.lore = TurnstileRenewed.config.getStringList("turnstiles." + key + ".item.lore");
            data.direction.north = TurnstileRenewed.config.getBoolean("turnstiles." + key + ".direction.north");
            data.direction.south = TurnstileRenewed.config.getBoolean("turnstiles." + key + ".direction.south");
            data.direction.east = TurnstileRenewed.config.getBoolean("turnstiles." + key + ".direction.east");
            data.direction.west = TurnstileRenewed.config.getBoolean("turnstiles." + key + ".direction.west");

            Block block = TurnstileRenewed.plugin.getServer().getWorld(data.world).getBlockAt(data.coords.x, data.coords.y, data.coords.z);

            block.setType(data.material);

            returned_data.add(data);
        }



        return returned_data;
    }
}