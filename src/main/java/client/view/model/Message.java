package client.view.model;

public class Message {
    public String senderNickname;
    public int ID;
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
