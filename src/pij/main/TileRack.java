package pij.main;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TileRack {
    private final ArrayList<Tile> tiles;
    private final Player player;

    public TileRack(Player player) {
        this.player = player;
        this.tiles = new ArrayList<Tile>();
        fillUp();
    }

    public boolean fillUp() {
        int RACK_SIZE = 7;
        while (tiles.size() < RACK_SIZE) {
            // If tile bag is empty , return false
            if (TileBag.tilesInBag.isEmpty()) return false;

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
                .collect(Collectors.joining(", ", "[", "]" )));
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
        for (Tile t : tiles) {
            if (t.letter == letter) return t;
        }
        return null;
    }
}
