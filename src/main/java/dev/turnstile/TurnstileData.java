package dev.turnstile;

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

    public class Coords {
        public int x;
        public int y;
        public int z;
    }

    Coords coords = new Coords();
}