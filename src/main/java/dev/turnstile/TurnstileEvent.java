package dev.turnstile;

import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;

// Listening to the player's interaction with the block


public class TurnstileEvent implements Listener {

    public boolean checkItemType(ItemStack item, String type) {
        if (item == null) return false;
        if (type == null) return true;
        if (item.getType().toString().equals(type)) return true;
        return false;
    }

    public boolean checkItemName(ItemStack item, String name) {
        if (item == null) return false;
        if (name == null) return true;
        if (item.getItemMeta().getDisplayName().equals(name)) return true;
        return false;
    }

    public boolean checkItemAmount(ItemStack item, int amount) {
        if (item == null) return false;
        if (amount == 0) return true;
        if (item.getAmount() >= amount) return true;
        return false;
    }

    public boolean checkItemLore(ItemStack item, List<String> lore) {
        if (item == null) return false;
        if (lore == null) return true;
        if (item.getItemMeta().getLore() == null) return false;
        if (item.getItemMeta().getLore().equals(lore)) return true;
        return false;
    }
    
    @EventHandler()
    public void onEvent(final PlayerInteractEvent event) {

        // TURNSTILE INTERACTION

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND && event.getItem() == null) {

            final Block block = event.getClickedBlock();

            final Player player = event.getPlayer();

            TurnstileData data = TurnstileCheck.getTurnstile(player, block, true);
            if (data == null) return;

            event.setCancelled(true);

            final Double time = data.delay;

            if (TurnstileCheck.getEconomy(player)) {
                // Check if the player got the money
                if (TurnstileRenewed.economy.getBalance(event.getPlayer().getName()) < data.price) {
                    event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-money"));
                    return;
                }
            }

            // Check if the player has the required item type
            // If the data.item.type isn't null, that means that there is an item to check.
            if (data.item.type != null) {
                int found = 0;
                Inventory inventory = player.getInventory();

                for (ItemStack item : inventory.getContents()) {
                    if (checkItemType(item, data.item.type) && 
                    checkItemName(item, data.item.name) && 
                    checkItemAmount(item, data.item.amount) &&
                    checkItemLore(item, data.item.lore)) found = 1;
                }

                if (found == 0) {
                    event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-item") +  " (" + data.item.amount + "x " + data.item.type + ")");
                    return;
                }
            }



            

            final Material temp_block = block.getType();
            final TurnstileData temp_data = data;
            
            block.setType(Material.AIR);

            class CloseTurnstile implements Runnable {
                @Override
                public void run() {
                    block.setType(temp_block);

                    // Get the fence direction and set the new data direction without casting BlockData to Directional
                    MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData();

                    // Set the data values (north, east, south, west)
                    if (temp_data.direction.north) multipleFacing.setFace(BlockFace.NORTH, true);
                    if (temp_data.direction.east) multipleFacing.setFace(BlockFace.EAST, true);
                    if (temp_data.direction.south) multipleFacing.setFace(BlockFace.SOUTH, true);
                    if (temp_data.direction.west) multipleFacing.setFace(BlockFace.WEST, true);
                    
                    block.setBlockData(multipleFacing);
                }
            }
            
            Bukkit.getScheduler().runTaskAsynchronously(TurnstileRenewed.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Double temp = (Double)time * 1000;
                        Thread.sleep(temp.longValue());
                        CloseTurnstile close = new CloseTurnstile();
                        Bukkit.getScheduler().runTask(TurnstileRenewed.plugin, close);
                    } catch(InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                }
            });

            if (data.command != null) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), data.command.toString().replace("%p", player.getName()));
            }

            String message = TurnstileMessages.getMessage("opened");
            
            // Check price
            if (TurnstileCheck.getEconomy(player)) {
                // Pay the player
                TurnstileRenewed.economy.withdrawPlayer(player.getName(), data.price);
                message += TurnstileMessages.getMessage("charged") + data.price + " " + TurnstileRenewed.economy.currencyNamePlural() + "§f.";
            }

            // Check item
            if (data.item.type != null) {
                message += TurnstileMessages.getMessage("item-charged") + data.item.amount + "x " + data.item.type + "§f.";
                // Remove the item from the player's inventory
                // If the item data has the same data as the item in the player's inventory, remove the item (type, amount, name, lore)
                if (data.item.name != null && data.item.lore != null) {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType().toString().equals(data.item.type) && item.getItemMeta().getDisplayName().equals(data.item.name) && item.getItemMeta().getLore().equals(data.item.lore)) {
                            item.setAmount(item.getAmount() - data.item.amount);
                            break;
                        }
                    }
                }


                // If the item data doesn't have a name, remove the item with the type
                else {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType().toString().equals(data.item.type)) {
                            item.setAmount(item.getAmount() - data.item.amount);
                            break;
                        }
                    }
                }
            }

            // Send the message
            event.getPlayer().sendMessage(TurnstileRenewed.prefix + message);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TurnstileCheck.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < TurnstileRenewed.GetData().size(); i++) {
                if (TurnstileRenewed.GetData().get(i).coords.x == event.getBlock().getX() && TurnstileRenewed.GetData().get(i).coords.y == event.getBlock().getY() && TurnstileRenewed.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-break"));
                }
            }
        }
    }

    @EventHandler
    public void onBlockNaturalBreak(BlockIgniteEvent event)
    {
        if (TurnstileCheck.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < TurnstileRenewed.GetData().size(); i++) {
                if (TurnstileRenewed.GetData().get(i).coords.x == event.getBlock().getX() && TurnstileRenewed.GetData().get(i).coords.y == event.getBlock().getY() && TurnstileRenewed.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}