package pij.main;

public class Scores {

    private int score;
    private final Player owner;

    public Scores(Player owner) {
        this.owner = owner;
        this.score = 0;
    }

    public int getScore() { return score; }

    public void addScore(int adding) { score += adding; }

    public void reduceScore(int reducing) { score -= reducing; }

    @Override
    public String toString() {
        String playerName = owner.isHuman ? "Human" : "Computer";
        return playerName + " player score:\t" + score;
    }
}
