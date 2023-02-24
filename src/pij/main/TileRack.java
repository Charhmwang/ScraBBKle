package pij.main;

import java.util.ArrayList;
import java.util.List;
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

    public TileRack getTileRack(Player player) { return this; }

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

    public Tile takeOutTileFromRack(char letter) {
        Tile t = isTileExisting(letter);
        if (t != null) {
                tiles.remove(t);
                return t;
            }
        return null;
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
                        t.letter = letter;
                        return t;
                    }
            }
        }
        return null;
    }

}
