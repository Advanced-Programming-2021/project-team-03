package server.model.enums;

public enum FaceUpSituation {
    FACE_UP("Up"),
    FACE_DOWN("Down");

    public String string;

    FaceUpSituation(String s) {
        string = s;
    }
}
