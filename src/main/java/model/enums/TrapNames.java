package model.enums;

public enum TrapNames {
    MAGIC_CYLINDER("Magic Cylinder"),
    MIRROR_FORCE("Mirror Force"),
    MIND_CRUSH("Mind Crush"),
    TRAP_HOLE("Trap Hole"),
    TORRENTIAL_TRIBUTE("Torrential Tribute"),
    TIME_SEAL("Time Seal"),
    NEGATE_ATTACK("Negate Attack"),
    SOLEMN_WARNING("Solemn Warning"),
    MAGIC_JAMAMER("Magic Jamamer"),
    CALL_OF_THE_HAUNTED("Call of The Haunted");

    private final String name;

    TrapNames(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
