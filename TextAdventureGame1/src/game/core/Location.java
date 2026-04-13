package game.core;

import java.util.*;

public class Location {
    public String description;
    public Map<String, String> exits;
    public String event;

    public Location(String description, Map<String, String> exits, String event) {
        this.description = description;
        this.exits = exits;
        this.event = event;
    }
}