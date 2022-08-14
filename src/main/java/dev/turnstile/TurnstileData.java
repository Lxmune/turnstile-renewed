package dev.turnstile;

import org.bukkit.Material;

public class TurnstileData {

    public long id;

    public Material material;

    public int price = 1;

    public String world;

    public class Coords {
        public int x;
        public int y;
        public int z;
    }

    Coords coords = new Coords();
}