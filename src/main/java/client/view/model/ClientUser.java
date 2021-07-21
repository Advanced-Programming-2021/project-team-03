package client.view.model;

public class ClientUser {
    public String username;
    public String nickname;
    public int score;
    public int balance;
    public int level;
    public int profileImageID;

    @Override
    public String toString() {
        return "Username: " + username + "\n" +
                "Nickname: " + nickname + "\n" +
                "Score: " + score + "\n" +
                "Level: " + level + "\n";
    }
}
