package server.model.enums;

public enum MonsterModels {
    BEAST_WARRIOR("Beast Warrior"),
    WARRIOR("Warrior"),
    FIEND("Fiend"),
    AQUA("Aqua"),
    BEAST("Beast"),
    PYRO("Pyro"),
    SPELL_CASTER("Spell Caster"),
    THUNDER("Thunder"),
    DRAGON("Dragon"),
    MACHINE("Machine"),
    ROCK("Rock"),
    INSECT("Insect"),
    CYBERSE("Cyberse"),
    FAIRY("Fairy"),
    SEA_SERPENT("Sea Serpent");

    public final String name;

    MonsterModels(String name) {
        this.name = name;
    }
}
