package game.battle;

import java.awt.Color;

public class Type {
    public String name;
    public Color color;

    public Type(String name, int color) {
        this.name = name;
        this.color = new Color(color);
    }
}