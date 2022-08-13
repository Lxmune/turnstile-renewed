package dev.turnstile;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;


public class TurnstileCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arguments[] = new String[3];

        if (arguments[0] == "") getHelp(sender);

        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            Block block = player.getPlayer().getTargetBlock(null, 10);
            
            if (getTypes().contains(block.getType()))
            {
                // Turnstile Creation

                MyPlugin myplugin = new MyPlugin();
                List<TurnstileData> stored_data = myplugin.GetData();

                TurnstileData new_data = new TurnstileData();
                new_data.coords.x = block.getX();
                new_data.coords.y = block.getY();
                new_data.coords.z = block.getZ();
                new_data.isClosed = true;
                new_data.id = stored_data.size() + 1;

                stored_data.add(new_data);
            }
            else
            {
                sender.sendMessage("You must be looking at a fence block.");
            }
        }

        return true;
    }

    @EventHandler
    public void onEvent(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (getTypes().contains(event.getClickedBlock().getType())) {
                if (event.getItem() == null) {
                    event.getClickedBlock().setType(Material.AIR);
                }
            }
        }
    }

    

    public boolean getHelp(CommandSender sender)
    {
        sender.sendMessage("&b/turnstile help &7- &fDisplay this help");
        sender.sendMessage("&b/turnstile info &7- &fDisplay information about the plugin");
        sender.sendMessage("&b/turnstile reload &7- &fReload the plugin");

        return true;
    }


    public List<Material> getTypes()
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