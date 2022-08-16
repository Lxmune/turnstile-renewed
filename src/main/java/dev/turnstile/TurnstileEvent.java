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

            event.setCancelled(true);

            final Block block = event.getClickedBlock();

            final Player player = event.getPlayer();

            TurnstileData data = TurnstileCheck.getTurnstile(player, block, true);
            if (data == null) return;

            final int time = data.delay;

            // Check if the player got the money
            if (TurnstileRenewed.economy.getBalance(event.getPlayer().getName()) < data.price) {
                event.getPlayer().sendMessage(TurnstileRenewed.prefix + "§6You don't have enough money to use this turnstile.");
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
                        Thread.sleep(time*1000);
                        CloseTurnstile close = new CloseTurnstile();
                        Bukkit.getScheduler().runTask(TurnstileRenewed.plugin, close);
                    } catch(InterruptedException e) {
                        System.out.println("Interrupted.");
                    }
                }
            });
            
            event.getPlayer().sendMessage(TurnstileRenewed.prefix + "§7The turnstile is §aopen§7.\n§7Your account got charged §a" + data.price + " " + TurnstileRenewed.economy.currencyNamePlural() + "§f.");
            TurnstileRenewed.economy.withdrawPlayer(event.getPlayer().getPlayerListName(), data.price);
            TurnstileRenewed.economy.depositPlayer("tom2090", data.price);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TurnstileCheck.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < TurnstileRenewed.GetData().size(); i++) {
                if (TurnstileRenewed.GetData().get(i).coords.x == event.getBlock().getX() && TurnstileRenewed.GetData().get(i).coords.y == event.getBlock().getY() && TurnstileRenewed.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(TurnstileRenewed.prefix + "§cYou can't break this turnstile.");
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