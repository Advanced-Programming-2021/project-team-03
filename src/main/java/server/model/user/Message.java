package server.model.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Message {
    public static final HashMap<Integer, Message> allMessages = new HashMap<>();
    public static Message pinned;
    private static int IDCount = 1;

    public final String senderNickname;
    public final String senderUsername;
    private String repliedMessage;
    public final int ID;
    private String text;
    private String time;
    private boolean edited;
    private boolean deleted;

    public Message(String text, User user) {
        this.text = text;
        this.senderNickname = user.getNickname();
        this.senderUsername = user.getUsername();
        ID = IDCount++;
        setCurrentTime();
        allMessages.put(ID, this);
    }

    public void setCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        time = dtf.format(now);
    }

    public void deleteMessage() {
        this.text = "(Deleted Message)";
        deleted = true;
        if (pinned.ID == this.ID) pinned = null;
        allMessages.remove(this.ID);
    }

    public void editText(String text) {
        if (deleted) return;
        this.text = text;
        edited = true;
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

    public void reply(Message repliedMessage) {
        this.repliedMessage = repliedMessage.text;
    }
}
