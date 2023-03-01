package pij.main;

import java.util.*;

public class Move {
    public String inputLetters;
    public char column;
    public int row;
    public String position;
    public String direction;
    public Player player;
    public boolean isValid;
    public Set<Tile> useTiles = new HashSet<>();


    public Move(Player player, String inputLetters, String position, String direction) {
        this.inputLetters = inputLetters;
        this.player = player;
        this.position = position;
        isValid = validateMove();
    }

    public boolean validateMove() {

        // 1. Validate player's input position and move direction
        int size = GameBoard.size;
        if (position.length() >= 2 && position.length() <= 3 &&
                direction.length() == 1 && (direction.charAt(0) == 'r' || direction.charAt(0) == 'd')) {
            this.direction = direction.charAt(0) == 'r' ? "right" : "down";
            char col = position.charAt(0);
            int row = Integer.parseInt(position.substring(1));
            if (col >= 'a' && col < ('a' + size) && row >= 1 && row <= size) {
                this.column = col;
                this.row = row;
            }
        } else return false;

        // 2. Validate whether the player is using tiles from its own tiles rack
        TileRack tileRack = player.getTileRack();
        if (inputLetters.length() <= tileRack.getTilesAmount()) {
            for (int i = 0; i < inputLetters.length(); i++) {
                Tile t = tileRack.isTileExisting(inputLetters.charAt(i));
                if (t != null && !useTiles.contains(t)) {
                    useTiles.add(t);
                } else return false;
            }
            if (useTiles.size() != inputLetters.length()) return false;
        } else return false;

        // 3. Check whether anywhere violates the game word rule after these tiles adding
        return WordsOnBoard.validateWord(inputLetters, row, column, direction);
    }


    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
