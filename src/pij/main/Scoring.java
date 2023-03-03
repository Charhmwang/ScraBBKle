package pij.main;

// Present the scores getting from each move
public class Scoring {

    private int score;
    private Move move;
    private final Player owner;

    public Scoring(Move move, Player owner) {
        this.owner = owner;
        this.score = 0;
    }

    public int getScore() { return score; }

}
