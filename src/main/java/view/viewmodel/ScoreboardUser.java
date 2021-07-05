package view.viewmodel;

public class ScoreboardUser {
    private int rank;
    private final String nickname;
    private final int score;

    public ScoreboardUser(int rank, String nickname, int score) {
        this.rank = rank;
        this.nickname = nickname;
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
