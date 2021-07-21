package server.control.game;

public enum GamePhases {
    DRAW("DRAW"),
    STANDBY("STANDBY"),
    FIRST_MAIN("FIRST MAIN"),
    BATTLE("BATTLE"),
    SECOND_MAIN("SECOND MAIN");

    public String name;

    GamePhases(String name) {
        this.name = name;
    }
}

