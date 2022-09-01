package dev.turnstile;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;

// Listening to the player's interaction with the block


public class TurnstileEvent implements Listener {
    
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

            // Check if the player got the required item
            if (data.item != null) {
                if (event.getPlayer().getInventory().contains(new ItemStack(Material.valueOf(data.item), data.item_amount))) {
                }
                else {
                    event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-item") + " (" + data.item_amount + "x " + data.item + ")");
                    return;
                }
            }

            final Material temp_block = block.getType();
            block.setType(Material.AIR);
            class CloseTurnstile implements Runnable {
                @Override
                public void run() {
                    block.setType(temp_block);
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
            if (data.item != null) {
                message += TurnstileMessages.getMessage("item-charged") + data.item_amount + "x " + data.item + "§f.";
                // Remove the item
                event.getPlayer().getInventory().removeItem(new ItemStack(Material.valueOf(data.item), data.item_amount));
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