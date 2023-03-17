package pij.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A TileRack has a list of Tile objects and a size recording the full-size of the tile rack.
 * Object of this class is Immutable: After TileBag has been created,
 * one cannot change the value of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class TileRack {

    /** The full-size of rack. Always value of 7 after object creation. */
    public static final int RACK_SIZE = 7;

    /** The tiles on this rack. A list of Tile objects. Always non-null after object creation. */
    private final List<Tile> tiles = new ArrayList<Tile>();


    /**
     * Constructs a TileRack instance. Fill up the rack from TileBag to full-size 7.
     */
    public TileRack() {
        fillUp();
    }

    /**
     * Returns all the current tiles on this rack.
     *
     * @return all the current tiles on this rack; can be null if rack is empty
     */
    public List<Tile> getTiles() { return tiles; }

    /**
     * Returns a boolean result whether the rack has been fully filled up.
     *
     * @return a boolean result whether the rack has been fully filled up
     */
    public boolean fillUp() {
        while (tiles.size() < RACK_SIZE) {
            // If tile bag is empty, return false
            if (TileBag.isEmpty()) return false;

            // Take tile out of tile bag randomly and add to rack
            tiles.add(TileBag.takeOutTile());
        }
        return true;
    }


    /**
     * Returns amount of the current tiles on this rack.
     *
     * @return amount of the current tiles on this rack; always included between 0 and 7
     */
    public int getTilesAmount() { return tiles.size(); }


    /**
     * Display tiles on this rack to the player on console.
     */
    public void displayTiles() {
        System.out.println("It's your turn! Your tiles:");
        System.out.println(
                tiles.stream().map(Tile::toString)
                .collect(Collectors.joining(", ")));
    }


    /**
     * Take a tile of the specific letter off from this rack.
     *
     * @param letter a tile of which letter is supposed to be taken off from rack; must non-null
     */
    public void takeOutTileFromRack(char letter) {
        Tile tile = isTileExisting(letter);
        tiles.remove(tile);
    }


    /**
     * Returns a boolean result whether the player's input letters are all
     * from the current tile rack.
     *
     * @param rack the targeted rack to be checked for input letters; must not be null
     * @param letters player's input for indicating the word; must not be null or empty string
     * @return a boolean result whether the player's input letters are all
     * from the current tile rack
     */
    public static boolean validateTilesFromRack(TileRack rack, String letters) {

        // Memorize the tiles letters and each letter's amount
        Map<Character, Integer> letterAmountOnRack = new HashMap<>();
        for (Tile tile : rack.getTiles()) {
            if (!letterAmountOnRack.containsKey(tile.getLetter())) {
                letterAmountOnRack.put(tile.getLetter(), 1);
            } else {
                int oldValue = letterAmountOnRack.get(tile.getLetter());
                letterAmountOnRack.replace(tile.getLetter(), oldValue + 1);
            }
        }

        // Validate letters using memoization
        for (int i = 0; i < letters.length(); i++) {
            char ch = letters.charAt(i);
            if (Character.isLowerCase(ch)) ch = '?';
            // Letter not existing or exceed the letter tiles amount
            if (!letterAmountOnRack.containsKey(ch) || letterAmountOnRack.get(ch) == 0) return false;
            else {
                int oldValue = letterAmountOnRack.get(ch);
                letterAmountOnRack.replace(ch, oldValue - 1);
            }
        }
        return true;
    }

    /**
     * Returns a Tile if a Tile of the specific letter exists; null if Tile not found.
     *
     * @param letter a tile of which letter is supposed to be found; must not be null
     * @return a Tile if a tile of the specific letter exists; null if Tile not found
     */
    public Tile isTileExisting(char letter) {

        if (Character.isAlphabetic(letter)) {
            if (Character.isUpperCase(letter)) {
                for (Tile t : tiles)
                    if (t.getLetter() == letter)
                        return t;
            } else {
                for (Tile t : tiles)
                    if (t.getLetter() == '?') {
                        return t;
                    }
            }
        }
        return null;
    }


    /**
     * Enable to print out this TileRack's contents.
     *
     * @return a String represents the move's contents composed of input tile letters, position and direction;
     * always non-null and non-empty string.
     */
    @Override
    public String toString() {
        return tiles.stream().map(Tile::toString).collect(Collectors.joining(", "));
    }

}
