package pij.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TileRack {
    public static final int RACK_SIZE = 7;
    private final List<Tile> tiles = new ArrayList<Tile>();
    private final Player player;

    public TileRack(Player player) {
        this.player = player;
        player.setTileRack(this);
        fillUp();
    }

    public List<Tile> getTiles() { return tiles; }

    public boolean fillUp() {
        while (tiles.size() < RACK_SIZE) {
            // If tile bag is empty , return false
            if (TileBag.isEmpty()) return false;

            // Take tile out of tile bag randomly and add to rack
            tiles.add(TileBag.takeOutTile());
        }
        return true;
    }

    public int getTilesAmount() { return tiles.size(); }

    public void displayTiles() {
        System.out.println("It's your turn! Your tiles:");
        System.out.println(
                tiles.stream().map(Tile::toString)
                .collect(Collectors.joining(", ")));
    }

    //public void takeOutTileFromRack(Tile tile) { tiles.remove(tile); }

    public void takeOutTileFromRack(char letter) {
        Tile tile = isTileExisting(letter);
        tiles.remove(tile);
    }


    public static boolean validateTilesFromRack(TileRack rack, String letters) {
        // memorize the tiles letters and each letter's amounts
        Map<Character, Integer> letterAmountOnRack = new HashMap<>();
        for (Tile tile : rack.getTiles()) {
            if (!letterAmountOnRack.containsKey(tile.letter)) {
                letterAmountOnRack.put(tile.letter, 1);
            } else {
                int oldValue = letterAmountOnRack.get(tile.letter);
                letterAmountOnRack.replace(tile.letter, oldValue + 1);
            }
        }

        // validate letters using memoization
        for (int i = 0; i < letters.length(); i++) {
            char ch = letters.charAt(i);
            if (Character.isLowerCase(ch)) ch = '?';
            // letter not existing or exceed the letter's using amount
            if (!letterAmountOnRack.containsKey(ch) || letterAmountOnRack.get(ch) == 0) return false;
            else {
                int oldValue = letterAmountOnRack.get(ch);
                letterAmountOnRack.replace(ch, oldValue - 1);
            }
        }
        return true;
    }


    public Tile isTileExisting(char letter) {

        if (Character.isAlphabetic(letter)) {
            if (Character.isUpperCase(letter)) {
                for (Tile t : tiles)
                    if (t.letter == letter)
                        return t;
            } else {
                for (Tile t : tiles)
                    if (t.letter == '?') {
                        return t;
                    }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return tiles.stream().map(Tile::toString).collect(Collectors.joining(", "));
    }

}
