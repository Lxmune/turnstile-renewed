package dev.turnstile;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;

import java.util.List;

public class TurnstileActions {
    public static int SpawnAll() {
        // Get data
        List<TurnstileData> stored_data = TurnstileRenewed.GetData();

        // Loops in the stored_data list
        for (TurnstileData data : stored_data) {
            // Get the turnstile coords from the data
            int x = data.coords.x;
            int y = data.coords.y;
            int z = data.coords.z;

            // Get the turnstile world by parsing string to World
            World world = TurnstileRenewed.plugin.getServer().getWorld(data.world.toString());

            // Get the block at the coords
            Block block = world.getBlockAt(x, y, z);

            // Get and set the turnstile material by parsing string to Material
            Material material = Material.getMaterial(data.material.toString());
            block.setType(material);

            // Get the fence direction and set the new data direction without casting BlockData to Directional
            MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData();

            // Set the data values (north, east, south, west)
            if (data.direction.north) multipleFacing.setFace(BlockFace.NORTH, true);
            if (data.direction.east) multipleFacing.setFace(BlockFace.EAST, true);
            if (data.direction.south) multipleFacing.setFace(BlockFace.SOUTH, true);
            if (data.direction.west) multipleFacing.setFace(BlockFace.WEST, true);

            block.setBlockData(multipleFacing);
        }
        return 1;
    }
}