package dev.turnstile;

import java.util.List;

import org.bukkit.Material;

public class TurnstileData {

    public long id;

    public Material material;

    public Double price = 1.0;

    public Double delay = 4.0;

    public String world;

    public String owner;

    public String owner_name;

    public String command;

    public class Direction {
        public boolean north;
        public boolean south;
        public boolean east;
        public boolean west;
    }

    public class Coords {
        public int x;
        public int y;
        public int z;
    }

    // Custom NBT Tags
    public class ItemData {
        public String type;
        public int amount;
        public String name;
        public List<String> lore;
    }

    public Direction direction = new Direction();

    Coords coords = new Coords();

    ItemData item = new ItemData();
}