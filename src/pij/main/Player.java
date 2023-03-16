package pij.main;
/**
 * A Player has a tile rack, score and identity status of whether a human or computer.
 * Objects of this class are mutable: after an object of class Player has been created,
 * scores and tile rack contents are changed while the game is in progress.
 * Attribute isHuman is immutable.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class Player {

    /** The identity of the Player. True if it is human user. Always non-null after object creation. */
    private final boolean isHuman;

    /** The total gained score of the Player. Initialized to be 0 after object creation. Mutable. */
    private int score;

    /** The tile rack of the Player. Initialized to be null after object creation. Mutable */
    private TileRack tileRack;


    /**
     * Constructs a new Player with identity of whether a human user.
     * Initialize boolean argument value to attribute isHuman and score to be 0.
     *
     * @param isHuman the boolean value of whether it is a human user or not
     */
    public Player (boolean isHuman) {
        this.isHuman = isHuman;
        this.score = 0;
    }


    /**
     * Sets the tile rack of this Player.
     *
     * @param tileRack a created TileRack object for this Player
     */
    public void setTileRack(TileRack tileRack) { this.tileRack = tileRack; }


    /**
     * Returns the total gained score of this Player.
     *
     * @return the total gained score
     */
    public int getScore() { return score; }


    /**
     * Returns the identity of this Player whether a human user.
     *
     * @return the identity of this Player whether a human user
     */
    public boolean isHuman() { return isHuman; }


    /**
     * Returns the tile rack of this Player.
     *
     * @return the tile rack of this Player
     */
    public TileRack getTileRack() { return tileRack; }


    /**
     * Adds new gaining scores to the player's currently total gained score.
     *
     * @param score new gaining score
     */
    public void addScore(int score) { this.score += score; }


    /**
     * Removes scores from the player's currently total gained score.
     *
     * @param score score amount needs to be reduced from this player
     */
    public void reduceScore(int score) { this.score -= score; }


    /**
     * Enable to print out this Player's score.
     *
     * @return a String represents the Player's score composed of the identity of this player and its score.
     */
    @Override
    public String toString() {
        String playerName = isHuman ? "Human" : "Computer";
        return playerName + " player score:\t" + score;
    }
}
