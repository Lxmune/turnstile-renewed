package dev.turnstile;

import java.util.List;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class TurnstileCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            getHelp(sender);
            return true;
        }

        else if (args[0].equalsIgnoreCase("help"))
        {
            getHelp(sender);
            return true;
        }

        else if (args[0].equalsIgnoreCase("info"))
        {
            getInfo(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("create"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);
                
                if (getTypes().contains(block.getType()))
                {
                    // Turnstile Creation
    
                    List<TurnstileData> stored_data = MyPlugin.GetData();
    
                    TurnstileData new_data = new TurnstileData();
    
                    new_data.material = block.getType();
    
                    new_data.coords.x = block.getX();
                    new_data.coords.y = block.getY();
                    new_data.coords.z = block.getZ();

                    for (TurnstileData data : stored_data)
                    {
                        if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z)
                        {
                            sender.sendMessage("This turnstile §calready exists§f with the ID §6" + data.id + "§f.");
                            return true;
                        }
                    }
                    
                    if (stored_data == null) new_data.id = 1;
                    else new_data.id = stored_data.size() + 1;
    
                    stored_data.add(new_data);
    
                    TurnstileSave.Save(new_data);
    
                    player.sendMessage("Turnstile created with ID: " + new_data.id + " at position " + new_data.coords.x + " " + new_data.coords.y + " " + new_data.coords.z);
                }
                else
                {
                    sender.sendMessage("You must be looking at a fence block.");
                }
            }
            else {
                sender.sendMessage("You must be a player to use this command.");
            }
            return true;
        }
        /*else if (args[0].equalsIgnoreCase("remove"))
        {
            getRemove(sender);
            return true;
        }*/
        else {
            getHelp(sender);
            return true;
        }
    }


    public boolean getHelp(CommandSender sender)
    {
        sender.sendMessage("§b/turnstile help §7- §fDisplay this help");
        sender.sendMessage("§b/turnstile info §7- §fDisplay information about the plugin");
        sender.sendMessage("§b/turnstile reload §7- §fReload the plugin");
        sender.sendMessage("§b/turnstile create §7- §fCreate a turnstile at your target block");
        return true;
    }

    public boolean getInfo(CommandSender sender)
    {
        sender.sendMessage("§bTurnstile §7- §fA plugin for turning fences into turnstiles.");
        sender.sendMessage("§bVersion §7- §f" + MyPlugin.plugin.getDescription().getVersion());
        sender.sendMessage("§bAuthor §7- §f" + MyPlugin.plugin.getDescription().getAuthors().toString());
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