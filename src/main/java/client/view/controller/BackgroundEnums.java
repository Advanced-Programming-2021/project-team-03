package client.view.controller;

public enum BackgroundEnums {
    YAMI("yami"),
    FOREST("forest"),
    CLOSED_FOREST("closedforest"),
    UMIIRUKA("water"),
    NORMAL("NormalField");

    public final String backgroundName;

    BackgroundEnums(String backgroundName) {
        this.backgroundName = backgroundName;
    }
}
