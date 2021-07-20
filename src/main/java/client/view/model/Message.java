package client.view.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Message {

    public String sender;
    public int ID;
    public String text;
    public String time;

    public void setCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        time = dtf.format(now);
    }

    public Message deleteMessage() {
        this.text = "(Deleted Message)";
        return this;
    }

    public Message editText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
