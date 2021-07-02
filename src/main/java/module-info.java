module Yu.Gi.Oh {
    requires com.google.gson;
    requires jbcrypt;
    requires org.json;
    requires jetty.util;
    requires opencsv;
    requires commons.collections4;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires lombok;
    exports control;
    opens control to com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports control.game;
    opens control.game to com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports control.databaseController;
    opens control.databaseController to lombok, com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports model.card;
    opens model.card to com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports model.game;
    opens model.game to com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports model.user;
    opens model.user to com.google.gson, jbcrypt, org.json, jetty.util, opencsv, commons.collections4;
    exports view;
    opens view to javafx.controls, javafx.base, javafx.fxml, javafx.media, javafx.graphics;
}