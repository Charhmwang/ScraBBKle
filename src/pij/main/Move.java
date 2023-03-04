package pij.main;

import javafx.util.Pair;

import java.util.*;

public class Move {
    public String inputLetters;
    public char column; //not idx
    public int row;  //not idx
    public String position;
    public String direction;
    public Player player;
    public boolean isValid;
    public int[] tilesSetInto;
    public List<Tile> useTiles = new ArrayList<>();
    public String madeNewWord = "";
    // if going right: startfrom: (3,2) endat:(3,8) / if down: (5,8) (10, 8)
    public List<Integer> start_and_endPosOfNewWord = new ArrayList<>();


    public Move(Player player, String inputLetters, String position, String direction) {
        this.inputLetters = inputLetters;
        this.player = player;
        this.position = position;
        this.direction = direction;
        Pair<Pair<String, List<Integer>>, int[]> valid_setIntoPosition = validateMove();
        if (valid_setIntoPosition != null) {
            isValid = true;
            madeNewWord = valid_setIntoPosition.getKey().getKey();
            tilesSetInto = valid_setIntoPosition.getValue();
            start_and_endPosOfNewWord.add(valid_setIntoPosition.getKey().getValue().get(0));
            start_and_endPosOfNewWord.add(valid_setIntoPosition.getKey().getValue().get(1));
            start_and_endPosOfNewWord.add(valid_setIntoPosition.getKey().getValue().get(2));
            start_and_endPosOfNewWord.add(valid_setIntoPosition.getKey().getValue().get(3));
        } // else isValid=false, tilesSetInto=null as default initialization
    }

    public Pair<Pair<String,List<Integer>>, int[]> validateMove() {

        this.column = position.charAt(0);
        this.row = Integer.parseInt(position.substring(1));
        this.direction = direction.charAt(0) == 'r' ? "right" : "down";

        /*
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
        } else return null;

        // 2. Validate whether the player is using tiles from its own tiles rack
        TileRack tileRack = player.getTileRack();
        if (inputLetters.length() <= tileRack.getTilesAmount()) {
            for (int i = 0; i < inputLetters.length(); i++) {
                Tile t = tileRack.isTileExisting(inputLetters.charAt(i));
                if (t != null) {
                    if (useTiles.contains(t)) {
                        for (Tile tile : useTiles) {
                            if (tile.letter == t.letter) {
                                if (tile.hashCode() == t.hashCode()) return null;
                            }
                        }
                    }
                    useTiles.add(t);
                } else return null;
            }
            if (useTiles.size() != inputLetters.length()) return null;
        } else return null;


         */


        // 3. Check whether anywhere violates the game word rule after these tiles adding
        return WordsOnBoard.validateWord(inputLetters, row, column, direction);
    }


    // Change the grids on board contents as the user input tiles letters

    // If the move is valid,
    // need to update the grid content from the form such as "G{3}" to "G2" or "T." to "T1", or "t{3}" to "t3"(wildcard)
    // See method buildWordUsingTileLetters in WordsOnBoard class
    public boolean execute() {

        recoverBoardGridContent();
        // Take each tile out of rack
        for (int i = 0; i < tilesSetInto.length; i++) {
            char letter = inputLetters.charAt(i);
            player.getTileRack().takeOutTileFromRack(letter);
        }

        // refill the rack
        int counter = useTiles.size();
        while (counter-- > 0) {
            if (!player.getTileRack().fillUp()) {
                if (player.getTileRack().getTilesAmount() == 0)
                    return false; // cannot refill because tiles bag empty, and also the player rack empty, so game over
            }
        }
        return true;
    }

    void recoverBoardGridContent() {

        for (int i = 0; i < tilesSetInto.length; i++) {
            char letter = inputLetters.charAt(i);
            int letterPoints = 0;
            if (Character.isLowerCase(letter)) letterPoints = 3;
            else letterPoints = LetterPoints.letterMap.get(letter);
            String letter_with_points = String.valueOf(letter) + letterPoints + " ";

            if (direction.equals("right")) {
                GameBoard.reviseBoard(row, tilesSetInto[i], letter_with_points);
            }
            if (direction.equals("down")) {
                GameBoard.reviseBoard(tilesSetInto[i], column - 97, letter_with_points);
            }
        }
    }



    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
