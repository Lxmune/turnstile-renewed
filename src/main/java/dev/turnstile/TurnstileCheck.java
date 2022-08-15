package dev.turnstile;

import java.util.List;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;


// Checks if this block is a turnstile

public class TurnstileCheck {
    public static TurnstileData getTurnstile(CommandSender sender, Block block) {
        if (getTypes().contains(block.getType()))
        {
            List<TurnstileData> stored_data = TurnstileRenewed.GetData();
            TurnstileData new_data = new TurnstileData();
            new_data.material = block.getType();
            new_data.coords.x = block.getX();
            new_data.coords.y = block.getY();
            new_data.coords.z = block.getZ();
            for (TurnstileData data : stored_data)
            {
                if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z && data.world == block.getWorld().getName())
                {
                    return data;
                }
            }
            sender.sendMessage("§cThis block is not a turnstile.");
            return null;
        }
        else
        {
            sender.sendMessage("You must be looking at a fence block.");
            return null;
        }
    }

    public static boolean getPermission(CommandSender player, String name) {
        if (!player.hasPermission("turnstile." + name)) { 
            player.sendMessage("§cYou don't have access to this command.");
            return false;
        }
        return true;
    }


    public static List<Material> getTypes()
    {
        List<Material> types = Arrays.asList(
        Material.OAK_FENCE, 
        Material.SPRUCE_FENCE, 
        Material.BIRCH_FENCE, 
        Material.JUNGLE_FENCE, 
        Material.ACACIA_FENCE, 
        Material.DARK_OAK_FENCE,
        Material.MANGROVE_FENCE,
        Material.CRIMSON_FENCE,
        Material.WARPED_FENCE,
        Material.NETHER_BRICK_FENCE
        );

        return types;
    }
}