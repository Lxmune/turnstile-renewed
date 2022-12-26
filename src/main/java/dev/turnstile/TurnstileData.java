package dev.turnstile;

import org.bukkit.Material;

public class TurnstileData {

    public long id;

    public Material material;

    public Double price = 1.0;

    public String item;

    public int item_amount;

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

    public Direction direction = new Direction();

    Coords coords = new Coords();
}