package dev.turnstile;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.time.LocalTime;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

// Listening to the player's interaction with the block


public class TurnstileEvent implements Listener {

    Map<UUID, String> cooldown = new HashMap<UUID, String>();
    
    @EventHandler()
    public void onBlockClick(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            final Block block = event.getClickedBlock();

            if (TurnstileCommand.getTypes().contains(block.getType())) {
                
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                // Turnstile Check (exists?)

                List<TurnstileData> stored_data = MyPlugin.GetData();

                for (TurnstileData data : stored_data) {
                    if (data.coords.x == x && data.coords.y == y && data.coords.z == z) {
                        if (cooldown.containsKey(event.getPlayer().getUniqueId())) {
                            if (LocalTime.now().isAfter(LocalTime.parse(cooldown.get(event.getPlayer().getUniqueId())))) {
                                cooldown.remove(event.getPlayer().getUniqueId());
                            }
                            else {
                                event.getPlayer().sendMessage("You must wait " + cooldown.get(event.getPlayer().getUniqueId()) + " before you can use this turnstile again.");
                                return;
                            }
                        }

                        else {
                            long cooldown_time = System.currentTimeMillis() + (1000 * 60 * 5);

                            cooldown.put(event.getPlayer().getUniqueId(), cooldown_time + "");

                            final Material temp_block = block.getType();
                            
                            block.setType(Material.AIR);

                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(1500);
                                    } catch(InterruptedException e) {
                                        System.out.println("Interrupted.");
                                    }
                                }
                                //CloseDoor(block, temp_block);
                            });

                            t.start();

                            class CloseDoor implements Runnable {
                                public void run() {
                                    block.setType(temp_block);
                                }
                            }

                            event.getPlayer().sendMessage("Turnstile opened.");
                        }

                        // Turnstile found
                        event.getPlayer().sendMessage("&a&lTURNSTILE\n\nEntry: 10$\nLeft-click to access.");

                    }
                }
            }
        }
    }
}