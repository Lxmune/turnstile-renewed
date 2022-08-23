package dev.turnstile;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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

        else if (args[0].equalsIgnoreCase("list"))
        {
            List<TurnstileData> stored_data = TurnstileRenewed.GetData();
            sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("list-turnstiles"));
            for (TurnstileData data : stored_data)
            {
                sender.sendMessage("§r §6§lID " + data.id
                + "\n§r   §7Owner - §6" + data.owner
                + "\n§r   §7Coords §6" + data.coords.x + "§7, §6" + data.coords.y + "§7, §6" + data.coords.z
                + "\n§r   §7Material - §6" + data.material.toString()
                + "\n§r   §7Price - §6" + data.price
                + "\n§r   §7Delay - §6" + data.delay
                + "\n§r   §7World - §6" + data.world
                );
            }
            return true;
        }

        else if (args[0].equalsIgnoreCase("info"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "info")) return true;

                TurnstileData data = TurnstileCheck.getTurnstile(player, block, false);

                if (data == null) return false;

                // Checking the owner
                if (data.owner_name == null) {
                    UUID temp = UUID.fromString(data.owner);
                    data.owner_name = Bukkit.getOfflinePlayer(temp).getName();
                    
                    if (data.owner_name != null) TurnstileSave.Save(data);
                }
                player.sendMessage(TurnstileRenewed.prefix + "§6Turnstile info:");
                player.sendMessage("§7ID: §e" + data.id);
                player.sendMessage("§7Owner (UUID): §e" + data.owner);
                if (data.owner_name == null) player.sendMessage("§7Owner (Name): §eUnknown");
                else player.sendMessage("§7Owner (Name): §e" + data.owner_name);
                player.sendMessage("§7Price: §e" + data.price);
                player.sendMessage("§7Delay: §e" + data.delay);
                player.sendMessage("§7World: §e" + data.world);
                player.sendMessage("§7Coords: §e" + data.coords.x + "," + data.coords.y + "," + data.coords.z);
                player.sendMessage("§7Material: §e" + data.material.toString());
                player.sendMessage("§7Command: §e" + data.command);
                return true;
            }
            else
            {
                sender.sendMessage(TurnstileMessages.getMessage("must-look-fence"));
                return true;
            }
        }

        else if (args[0].equalsIgnoreCase("create"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);
                
                if (!TurnstileCheck.getPermission(player, "create")) return false;

                // This one is a bit special so I'm keeping the old code

                if (TurnstileCheck.getTypes().contains(block.getType()))
                {
                    List<TurnstileData> stored_data = TurnstileRenewed.GetData();
    
                    TurnstileData new_data = new TurnstileData();
    
                    new_data.material = block.getType();
    
                    new_data.coords.x = block.getX();
                    new_data.coords.y = block.getY();
                    new_data.coords.z = block.getZ();

                    new_data.world = block.getWorld().getName();

                    new_data.owner = player.getUniqueId().toString();

                    new_data.owner_name = player.getName();

                    for (TurnstileData data : stored_data)
                    {
                        if (data.coords.x == new_data.coords.x && data.coords.y == new_data.coords.y && data.coords.z == new_data.coords.z)
                        {
                            if (!TurnstileCheck.getAccess(player, data)) return true;

                            sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("already-exists") + data.id + "§f.");
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
    
                    player.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-creation-1") + new_data.id + TurnstileMessages.getMessage("successful-creation-2") + new_data.coords.x + " " + new_data.coords.y + " " + new_data.coords.z);
                }
                else
                {
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-look-fence"));
                }
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
            }
            return true;
        }

        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "remove")) return true;

                TurnstileData data = TurnstileCheck.getTurnstile(player, block, false);
                if (data == null) return true;

                if (!TurnstileCheck.getAccess(player, data)) return true;

                // Remove turnstile from temp var + config
                TurnstileSave.Remove(data);
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-removal") + data.id + "§f.");
                return true;
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
            }
            return true;
        }
        
        else if (args[0].equalsIgnoreCase("price"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "price")) return true;

                TurnstileData data = TurnstileCheck.getTurnstile(sender, block, false);
                if (data == null) return true;

                if (!TurnstileCheck.getAccess(player, data)) return true;

                if (args.length == 2)
                {
                    try {
                       Double.parseDouble(args[1]);
                    }
                    catch (NumberFormatException e)
                    {
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("invalid-price"));
                        return true;
                    }
                    if (Double.parseDouble(args[1]) < 0)
                        {
                            sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("price-positive"));
                            return true;
                        }
                    else
                    {
                        data.price = Double.parseDouble(args[1]);
                        TurnstileSave.Save(data);
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-price") + data.price + "§f.");
                        return true;
                    }
                }
                else {
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("usage-price"));
                    return true;
                }
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
                return true;
            }
        }

        else if (args[0].equalsIgnoreCase("delay"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "delay")) return true;
                
                TurnstileData data = TurnstileCheck.getTurnstile(sender, block, false);
                if (data == null) return true;

                if (!TurnstileCheck.getAccess(player, data)) return true;
                
                if (args.length == 2)
                {
                    Double temp_delay;
                    try {
                       Integer.parseInt(args[1]);
                       // Convert Integer to Double
                       temp_delay = Double.parseDouble(args[1]);
                    }
                    catch (NumberFormatException e)
                    {
                        try {
                            temp_delay = Double.parseDouble(args[1]);
                        }
                        catch (NumberFormatException f)
                        {
                            sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("invalid-delay"));
                            return true;
                        }
                    }

                    if (temp_delay < 0)
                    {
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("delay-positive"));
                        return true;
                    }
                    else
                    {
                        data.delay = temp_delay;
                        TurnstileSave.Save(data);
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-delay-1") + data.delay + TurnstileMessages.getMessage("successful-delay-2"));
                        return true;
                    }
                }
                else {
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("usage-delay"));
                }
                return true;
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
            }
            return true;
        }

        else if (args[0].equalsIgnoreCase("owner"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "owner")) return true;

                TurnstileData data = TurnstileCheck.getTurnstile(sender, block, false);
                if (data == null) return true;

                if (!TurnstileCheck.getAccess(player, data)) return true;

                if (args.length == 2)
                {
                    Player target = TurnstileCheck.getPlayer(player, args[1]);
                    
                    if (target == null) return true;
                    else if (target.getUniqueId().equals(player.getUniqueId()))
                    {
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("owner-self"));
                        return true;
                    }
                    else
                    {
                        data.owner = target.getUniqueId().toString();
                        TurnstileSave.Save(data);
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-owner") + target.getName() + "§f.");
                        return true;
                    }
                }
                else {
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("usage-owner"));
                    return true;
                }
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
                return true;
            }
        }
        
        // Admin command

        else if (args[0].equalsIgnoreCase("command"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Block block = player.getPlayer().getTargetBlock(null, 10);

                if (!TurnstileCheck.getPermission(player, "admin.command")) return true;

                TurnstileData data = TurnstileCheck.getTurnstile(sender, block, false);
                if (data == null) return true;

                if (!TurnstileCheck.getAccess(player, data)) return true;

                if (args.length == 1) {
                    if (data.command != null) {
                        sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("command-removed") + "§f.");
                        data.command = null;
                        TurnstileSave.Save(data);
                        return true;
                    }
                }

                if (args.length >= 2)
                {
                    // Getting the rest of the arguments
                    StringBuilder myStringBuilder = new StringBuilder(args[1]);
                    for(int a = 2; a < args.length; a++) myStringBuilder.append(" ").append(args[a]);
                    data.command = myStringBuilder.toString(); // Player variable

                    TurnstileSave.Save(data);
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("successful-command") + data.command + "§f.");
                    return true;
                }
                else {
                    sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("usage-command"));
                    return true;
                }
            }
            else {
                sender.sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("must-be-player"));
                return true;
            }
        }

        else {
            getHelp(sender);
            return true;
        }
    }


    public boolean getHelp(CommandSender sender)
    {
        sender.sendMessage(TurnstileMessages.getMessage("help-help"));
        sender.sendMessage(TurnstileMessages.getMessage("help-info"));
        sender.sendMessage(TurnstileMessages.getMessage("help-create"));
        sender.sendMessage(TurnstileMessages.getMessage("help-remove"));
        sender.sendMessage(TurnstileMessages.getMessage("help-price"));
        sender.sendMessage(TurnstileMessages.getMessage("help-delay"));
        sender.sendMessage(TurnstileMessages.getMessage("help-owner"));
        sender.sendMessage(TurnstileMessages.getMessage("help-command"));
        return true;
    }
}