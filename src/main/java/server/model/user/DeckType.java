package server.model.user;

public enum DeckType {
    MAIN("Main deck", 40, 60),
    SIDE("Side deck", 0, 15);

    private final String name;
    int minCards;
    int maxCards;

    DeckType(String name, int minCards, int maxCards) {
        this.name = name;
        this.minCards = minCards;
        this.maxCards = maxCards;
    }

    public String getName() {
        return name;
    }
}
