package pij.main;

public class Player {
    private boolean isHuman;
    private int score;

    public Player (boolean isHuman) {
        this.isHuman = isHuman;
        this.score = 0;
    }

    void getPoints(int points) {
        score += points;
    }
}
