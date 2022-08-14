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
                
                if (!player.hasPermission("turnstile.create")) { 
                    player.sendMessage("§cYou don't have access to this command.");
                    return true;
                }

                if (getTypes().contains(block.getType()))
                {
                    // Turnstile Creation
    
                    List<TurnstileData> stored_data = TurnstileRenewed.GetData();
    
                    TurnstileData new_data = new TurnstileData();
    
                    new_data.material = block.getType();
    
                    new_data.coords.x = block.getX();
                    new_data.coords.y = block.getY();
                    new_data.coords.z = block.getZ();

                    new_data.world = block.getWorld().getName();

                    for (TurnstileData data : stored_data)
                    {
                        if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z)
                        {
                            sender.sendMessage("This turnstile §calready exists§f with the ID §6" + data.id + "§f.");
                            return true;
                        }
                    }
                    
                    if (stored_data == null) new_data.id = 1;

                    // Search for the nearest available ID
                    else {
                        new_data.id = TurnstileRenewed.config.getInt("next_id");
                        TurnstileRenewed.config.set("next_id", new_data.id + 1);
                    }
    
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
        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!player.hasPermission("turnstile.remove")) { 
                    player.sendMessage("§cYou don't have access to this command.");
                    return true;
                }

                
                if (getTypes().contains(block.getType()))
                {
                    // Turnstile Creation
    
                    List<TurnstileData> stored_data = TurnstileRenewed.GetData();
    
                    TurnstileData new_data = new TurnstileData();
    
                    new_data.material = block.getType();
    
                    new_data.coords.x = block.getX();
                    new_data.coords.y = block.getY();
                    new_data.coords.z = block.getZ();

                    for (TurnstileData data : stored_data)
                    {
                        if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z)
                        {
                            // Remove turnstile from temp var + config
                            TurnstileSave.Remove(data);

                            sender.sendMessage("This turnstile has been removed with the ID §6" + data.id + "§f.");
                            return true;
                        }
                    }
                    sender.sendMessage("§cThis block is not a turnstile.");
                    return true;
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
        
        else if (args[0].equalsIgnoreCase("price"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!player.hasPermission("turnstile.price")) { 
                    player.sendMessage("§cYou don't have access to this command.");
                    return true;
                }

                
                if (getTypes().contains(block.getType()))
                {
                    // Turnstile Creation
    
                    List<TurnstileData> stored_data = TurnstileRenewed.GetData();
    
                    TurnstileData new_data = new TurnstileData();
    
                    new_data.material = block.getType();
    
                    new_data.coords.x = block.getX();
                    new_data.coords.y = block.getY();
                    new_data.coords.z = block.getZ();

                    for (TurnstileData data : stored_data)
                    {
                        if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z)
                        {
                            if (args.length == 2)
                            {
                                try {
                                    Integer.parseInt(args[1]);
                                }
                                catch (NumberFormatException e)
                                {
                                    sender.sendMessage("§cInvalid price.");
                                    return true;
                                }

                                if (Integer.parseInt(args[1]) < 0)
                                {
                                    sender.sendMessage("§cThe price must be a positive number.");
                                    return true;
                                }
                                else
                                {
                                    data.price = Integer.parseInt(args[1]);
                                    TurnstileSave.Save(data);
                                    sender.sendMessage("The price of this turnstile has been set to §6" + data.price + "§f.");
                                    return true;
                                }
                            }
                            else {
                                sender.sendMessage("§cUsage: /turnstile price <price>");
                            }
                            return true;
                        }
                    }
                    sender.sendMessage("§cThis block is not a turnstile.");
                    return true;
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
        
        else {
            getHelp(sender);
            return true;
        }
    }


    public boolean getHelp(CommandSender sender)
    {
        sender.sendMessage("§b/turnstile help §7- §fDisplay this help");
        sender.sendMessage("§b/turnstile info §7- §fDisplay information about the plugin");
        sender.sendMessage("§b/turnstile create §7- §fCreate a turnstile at your target block");
        sender.sendMessage("§b/turnstile remove §7- §fRemove a turnstile at your target block");
        sender.sendMessage("§b/turnstile price §7- §fSet the price of a turnstile at your target block");
        return true;
    }

    public boolean getInfo(CommandSender sender)
    {
        sender.sendMessage("§bTurnstile §7- §fA plugin for turning fences into turnstiles.");
        sender.sendMessage("§bVersion §7- §f" + TurnstileRenewed.plugin.getDescription().getVersion());
        sender.sendMessage("§bAuthor §7- §f" + TurnstileRenewed.plugin.getDescription().getAuthors().toString());
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