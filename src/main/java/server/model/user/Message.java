package server.model.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Message {
    public static final HashMap<Integer, Message> allMessages = new HashMap<>();
    public static Message pinned;
    private static int IDCount = 1;

    public final String sender;
    public final int ID;
    private String text;
    private String time;

    public Message(String text, String sender) {
        this.text = text;
        this.sender = sender;
        ID = IDCount++;
        setCurrentTime();
        allMessages.put(ID, this);
    }

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

    public static void setPinned(Message pinned) {
        Message.pinned = pinned;
    }

    public String getTime() {
        return time;
    }

    public static Message getByID(int ID) {
        return allMessages.get(ID);
    }
}
