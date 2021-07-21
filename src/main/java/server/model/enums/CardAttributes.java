package server.model.enums;

public enum CardAttributes {
    DARK("Dark"),
    LIGHT("Light"),
    EARTH("Earth"),
    FIRE("Fire"),
    WATER("Water"),
    WIND("Wind"),
    SPELL("Spell"),
    TRAP("Trap");

    public final String name;

    CardAttributes(String name) {
        this.name = name;
    }
}
