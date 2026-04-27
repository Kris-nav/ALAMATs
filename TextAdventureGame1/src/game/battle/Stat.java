package game.battle;

public class Stat {
    public String name;
    public double value;
    public double base;

    public Stat(String name, double value) {
        this.base = value;
        this.name = name;
        this.value = value;
    }
}