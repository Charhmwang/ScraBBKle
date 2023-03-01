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

    void getPoints(int points) {
        score += points;
    }
}
