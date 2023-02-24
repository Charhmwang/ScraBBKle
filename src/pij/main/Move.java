package pij.main;

import java.util.ArrayList;
import java.util.List;

public class Move {
    public String word;
    public char column;
    public int row;
    public String position;
    public String direction;
    public Player player;
    public boolean isValid;
    public List<Tile> usingTiles = new ArrayList<>();
    public Move(Player player, String word, String position, String direction) {
        this.word = word;
        this.player = player;
        isValid = validateTiles(word, position, direction);
        this.position = isValid ? position : "";
    }

    public boolean validateTiles(String word, String position, String direction) {

        // Validate input position and move direction
        boolean loc_dir = false;
        int size = GameBoard.size;
        if (position.length() >= 2 && position.length() <= 3 &&
                direction.length() == 1 && (direction.charAt(0) == 'r' || direction.charAt(0) == 'd')) {
            this.direction = direction.charAt(0) == 'r' ? "right" : "down";
            char col = position.charAt(0);
            int row = Integer.parseInt(position.substring(1));
            if (col >= 'a' && col < ('a' + size) && row >= 1 && row <= size) {
                loc_dir = true;
                this.column = col;
                this.row = row;
            }
        }

        // Validate tiles from player's tiles rack
        boolean validate_word = false;
        TileRack tileRack = player.getTileRack();
        if (word.length() <= tileRack.getTilesAmount()) {
            for (int i = 0; i < word.length(); i++) {
                Tile tile = tileRack.takeOutTileFromRack(word.charAt(i));
                if (tile != null) usingTiles.add(tile);
                else break;
            }
            if (usingTiles.size() == word.length()) validate_word = true;
        }

        return loc_dir && validate_word;
    }


    @Override
    public String toString() { return "The move is:     Word: "
            + word + " at position " + position + ", direction: " + direction; }

}
