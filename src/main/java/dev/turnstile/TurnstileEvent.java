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

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

            event.setCancelled(true);

            final Block block = event.getClickedBlock();
            
            if (TurnstileCommand.getTypes().contains(block.getType())) {
                
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                System.out.println("2");

                // Turnstile Check (exists?)

                List<TurnstileData> stored_data = MyPlugin.GetData();

                for (TurnstileData data : stored_data) {
                    if (data.coords.x == x && data.coords.y == y && data.coords.z == z) {
                        if (cooldown.containsKey(event.getPlayer().getUniqueId())) {
                            if (LocalTime.now().isAfter(cooldown.get(event.getPlayer().getUniqueId()))) {
                                cooldown.remove(event.getPlayer().getUniqueId());
                            }
                            else {
                                System.out.println("Cooldown");
                                event.getPlayer().sendMessage("§fYou must wait §c" + LocalTime.now().until(cooldown.get(event.getPlayer().getUniqueId()), SECONDS)+ " seconds §fbefore you can use this turnstile again.");
                                return;
                            }
                        }

                        else {

                            // Check if the player got the money

                            if (MyPlugin.economy.getBalance(event.getPlayer().getName()) < 0.5) {
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

                            Bukkit.getScheduler().runTaskAsynchronously(MyPlugin.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);

                                        cooldown.remove(event.getPlayer().getUniqueId());

                                        CloseTurnstile close = new CloseTurnstile();
                                        Bukkit.getScheduler().runTask(MyPlugin.plugin, close);

                                    } catch(InterruptedException e) {
                                        System.out.println("Interrupted.");
                                    }
                                }
                            });

                            event.getPlayer().sendMessage("§aThe turnstile is open.\n§fYour account got charged 0.5 §a" + MyPlugin.economy.currencyNamePlural() + "§f.");

                            MyPlugin.economy.withdrawPlayer(event.getPlayer().getPlayerListName(), 0.5);
                            MyPlugin.economy.depositPlayer("tom2090", 0.5);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TurnstileCommand.getTypes().contains(event.getBlock().getType())) {
            for (int i = 0; i < MyPlugin.GetData().size(); i++) {
                if (MyPlugin.GetData().get(i).coords.x == event.getBlock().getX() && MyPlugin.GetData().get(i).coords.y == event.getBlock().getY() && MyPlugin.GetData().get(i).coords.z == event.getBlock().getZ()) {
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
            for (int i = 0; i < MyPlugin.GetData().size(); i++) {
                if (MyPlugin.GetData().get(i).coords.x == event.getBlock().getX() && MyPlugin.GetData().get(i).coords.y == event.getBlock().getY() && MyPlugin.GetData().get(i).coords.z == event.getBlock().getZ()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}