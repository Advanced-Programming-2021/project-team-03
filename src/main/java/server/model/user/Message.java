package server.model.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Message {
    public static final HashMap<Integer, Message> allMessages = new HashMap<>();
    public static Message pinned;
    private static int IDCount = 1;

    public final String senderNickname;
    public final int ID;
    private String text;
    private String time;
    private boolean edited;
    private boolean deleted;

    public Message(String text, User user) {
        this.text = text;
        this.senderNickname = user.getNickname();
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
        deleted = true;
        return this;
    }

    public Message editText(String text) {
        if (deleted) return this;
        this.text = text;
        edited = true;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setPinned() {
        pinned = this;
    }

    public static Message getByID(int ID) {
        return allMessages.get(ID);
    }
}
