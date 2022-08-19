package dev.turnstile;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

            // Check if the player got the money
            if (TurnstileRenewed.economy.getBalance(event.getPlayer().getName()) < data.price) {
                event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("no-money"));
                return;
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
            
            event.getPlayer().sendMessage(TurnstileRenewed.prefix + TurnstileMessages.getMessage("opened") + data.price + " " + TurnstileRenewed.economy.currencyNamePlural() + "§f.");
            TurnstileRenewed.economy.withdrawPlayer(event.getPlayer().getPlayerListName(), data.price);
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