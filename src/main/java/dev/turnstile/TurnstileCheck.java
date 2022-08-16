package dev.turnstile;

import java.util.List;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


// Checks if this block is a turnstile

public class TurnstileCheck {
    public static TurnstileData getTurnstile(CommandSender sender, Block block, Boolean silent) {
        if (getTypes().contains(block.getType()))
        {
            List<TurnstileData> stored_data = TurnstileRenewed.GetData();
            TurnstileData new_data = new TurnstileData();
            new_data.material = block.getType();
            new_data.coords.x = block.getX();
            new_data.coords.y = block.getY();
            new_data.coords.z = block.getZ();
            new_data.world = block.getWorld().getName().toLowerCase().toString();

            for (TurnstileData data : stored_data)
            {
                // Comparing the coords
                if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z && data.world.equals(new_data.world))
                {
                    return data;
                }
            }
            if (!silent) sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("not-turnstile"));
            return null;
        }
        else
        {
            if (!silent) sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-look-fence"));
            return null;
        }
    }

    public static boolean getPermission(CommandSender player, String name) {
        if (!player.hasPermission("turnstile." + name)) { 
            player.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-permission"));
            return false;
        }
        return true;
    }

    public static boolean getAccess(Player player, TurnstileData data) {
        if (data.owner != null) {
            if (data.owner.equals(player.getUniqueId().toString())) {
                return true;
            }
            else if (player.hasPermission("turnstile.bypass")) {
                player.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("turnstile-bypass"));
                return true;
            }
            else {
                player.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("not-owner"));
                return false;
            }
        }
        else {
            return true;
        }
    }

    public static Player getPlayer(Player sender, String player) {
        Player target = Bukkit.getPlayer(player);
        if (target == null) {
            sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("not-found"));
            return null;
        }
        return target;
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