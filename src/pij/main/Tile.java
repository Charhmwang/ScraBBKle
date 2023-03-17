package pij.main;

/**
 * A tile has a char attribute representing an uppercase alphabet letter or a question mark
 * if it is a wildcard, an integer attribute marks points of the Tile object, and a boolean
 * value of whether it is a wildcard. Objects of this class are immutable:
 * after an object of class Tile has been created, one cannot change the values of its attributes.
 *
 * @author Haomeng
 * @version 1.0
 */
public class Tile {

    /** The letter on the tile. Always non-null after object creation. */
    private final char letter;

    /** The points of the tile. Always non-null after object creation. */
    private final int points;

    /** The identity of the tile whether a wildcard. Always non-null after object creation. */
    private final boolean wildCard;


    /**
     * Constructs a new Tile instance with given letter and corresponding points.
     *
     * @param letter letter of the tile; must not be null, uppercase alphabet letter or a question mark
     * @param points points of the tile; must not be null
     */
    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        this.wildCard = letter == '?';
    }


    /**
     * Returns the letter of the tile.
     *
     * @return the letter of the tile; always non-null
     */
    public char getLetter() { return this.letter; }


    /**
     * Returns a boolean result whether the tile is a wildcard.
     *
     * @return a boolean result whether the tile is a wildcard; always non-null
     */
    public boolean isWildCard() { return wildCard; }


    /**
     * Representation of this instance,
     * in the form "[letter + points]", e.g. [G3] if tile letter is G.
     *
     * @return the string representation of the tile; always non-null and non-empty
     */
    @Override
    public String toString() {
        return "[" + letter + points + "]";
    }

}
