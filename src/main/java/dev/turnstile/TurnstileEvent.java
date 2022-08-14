package dev.turnstile;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.SECONDS;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;

// Listening to the player's interaction with the block


public class TurnstileEvent implements Listener {

    Map<UUID, LocalTime> cooldown = new HashMap<UUID, LocalTime>();
    
    @EventHandler()
    public void onEvent(final PlayerInteractEvent event) {

        // TURNSTILE INTERACTION

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND && event.getItem() == null) {

            event.setCancelled(true);

            final Block block = event.getClickedBlock();
            
            if (TurnstileCommand.getTypes().contains(block.getType())) {
                
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                // Turnstile Check (exists?)

                List<TurnstileData> stored_data = TurnstileRenewed.GetData();

                for (TurnstileData data : stored_data) {
                    if (data.coords.x == x && data.coords.y == y && data.coords.z == z) {
                        if (cooldown.containsKey(event.getPlayer().getUniqueId())) {
                            if (LocalTime.now().isAfter(cooldown.get(event.getPlayer().getUniqueId()))) {
                                cooldown.remove(event.getPlayer().getUniqueId());
                            }
                            else {
                                event.getPlayer().sendMessage("§fYou must wait §c" + LocalTime.now().until(cooldown.get(event.getPlayer().getUniqueId()), SECONDS)+ " seconds §fbefore you can use this turnstile again.");
                                return;
                            }
                        }

                        else {

                            // Check if the player got the money

                            if (TurnstileRenewed.economy.getBalance(event.getPlayer().getName()) < data.price) {
                                event.getPlayer().sendMessage("§6You don't have enough money to use this turnstile.");
                                return;
                            }

                            LocalTime cooldown_time = LocalTime.now().plusSeconds(5);

                            cooldown.put(event.getPlayer().getUniqueId(), cooldown_time);

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
                                        Thread.sleep(3000);

                                        cooldown.remove(event.getPlayer().getUniqueId());

                                        CloseTurnstile close = new CloseTurnstile();
                                        Bukkit.getScheduler().runTask(TurnstileRenewed.plugin, close);

                                    } catch(InterruptedException e) {
                                        System.out.println("Interrupted.");
                                    }
                                }
                            });

                            event.getPlayer().sendMessage("§aThe turnstile is open.\n§fYour account got charged §a" + data.price + " " + TurnstileRenewed.economy.currencyNamePlural() + "§f.");

                            TurnstileRenewed.economy.withdrawPlayer(event.getPlayer().getPlayerListName(), data.price);
                            TurnstileRenewed.economy.depositPlayer("tom2090", data.price);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TurnstileCommand.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < TurnstileRenewed.GetData().size(); i++) {
                if (TurnstileRenewed.GetData().get(i).coords.x == event.getBlock().getX() && TurnstileRenewed.GetData().get(i).coords.y == event.getBlock().getY() && TurnstileRenewed.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou can't break this turnstile.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockNaturalBreak(BlockIgniteEvent event)
    {
        if (TurnstileCommand.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < TurnstileRenewed.GetData().size(); i++) {
                if (TurnstileRenewed.GetData().get(i).coords.x == event.getBlock().getX() && TurnstileRenewed.GetData().get(i).coords.y == event.getBlock().getY() && TurnstileRenewed.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}