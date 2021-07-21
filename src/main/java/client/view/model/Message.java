package client.view.model;

public class Message {
    public String senderNickname;
    public String senderUsername;
    public int ID;
    public String repliedMessage;
    public String text;
    public String time;
    public boolean edited;
    public boolean deleted;

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
