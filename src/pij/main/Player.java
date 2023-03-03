package pij.main;

public class Player {
    public boolean isHuman;
    private int score;
    private TileRack tileRack;

    public Player (boolean isHuman) {
        this.isHuman = isHuman;
        this.score = 0;
    }

    public void setTileRack(TileRack tileRack) { this.tileRack = tileRack; }

    public TileRack getTileRack() { return tileRack; }

    void addScore(int score) {
        this.score += score;
    }

    public void reduceScore(int reducing) { score -= reducing; }

    @Override
    public String toString() {
        String playerName = isHuman ? "Human" : "Computer";
        return playerName + " player score:\t" + score;
    }
}
