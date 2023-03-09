package pij.main;

import java.util.*;

public class Move {
    public String inputLetters;
    public char column; //not idx
    public int row;  //idx
    public String position;
    public String direction;

    public Player player;
    public boolean isValid;
    public List<Integer> tilesSetInto;
    //public List<Tile> useTiles = new ArrayList<>();
    public String madeNewWord = "";
    public List<Integer> start_and_endPosOfNewWord = new ArrayList<>();

    public Move(Player player, String inputLetters, String position, String direction) {
        this.inputLetters = inputLetters;
        this.player = player;
        this.position = position;
        this.direction = direction;
        //( tilesSetInto, (newWord, idxOfNewWord) )
        AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>>
                setIntoPosition = validateMove();
        // valid_setIntoPosition will be null
        // 1. if the player's move input form is wrong,
        // 2. if the player try to use the word not coming from its own rack,
        // 3. if the player try to set tile into a grid already been covered by tile
        if (setIntoPosition != null) {
            tilesSetInto = setIntoPosition.getKey();
            //for (int i : tilesSetInto) System.out.println(i);  //debug
            AbstractMap.SimpleEntry<String, List<Integer>> newWord_and_idxes = setIntoPosition.getValue();
            if (newWord_and_idxes.getKey() != null) { //there is valid string returned
                isValid = true;
                madeNewWord = newWord_and_idxes.getKey();
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(0));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(1));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(2));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(3));
            }
        }
    }


    public AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>> validateMove() {

        // 1. Validate player's input position and move direction
        int size = GameBoard.size;
        if (position.length() >= 2 && position.length() <= 3 &&
                direction.length() == 1 && (direction.charAt(0) == 'r' || direction.charAt(0) == 'd')) {
            this.direction = direction.charAt(0) == 'r' ? "right" : "down";
            char col = position.charAt(0);
            int row = 0;
            // Initiate then use try catch to prevent player enter in a wrong form
            // such like "8f" instead of the supposed form "f8"
            try {
                row = Integer.parseInt(position.substring(1));
            } catch (NumberFormatException e) {
                return null;
            }
            if (col >= 'a' && col < ('a' + size) && row >= 1 && row <= size) {
                this.column = col;
                this.row = row;
            }
        } else return null;

        // 2. Validate whether the player is using tiles from its own tiles rack
//        TileRack tileRack = player.getTileRack();
//        if (inputLetters.length() <= tileRack.getTilesAmount()) {
//            for (int i = 0; i < inputLetters.length(); i++) {
//                Tile t = tileRack.isTileExisting(inputLetters.charAt(i));
//                if (t != null) {
//                    useTiles.add(t);
//                } else return null;
//            }
//            if (useTiles.size() != inputLetters.length()) return null;
//        } else return null;
//
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
//        for (Tile useTile : useTiles) {
//            player.getTileRack().takeOutTileFromRack(useTile);
//        }

        for (int i = 0; i < inputLetters.length(); i++) {
            char letter = inputLetters.charAt(i);
            player.getTileRack().takeOutTileFromRack(letter);
        }

        // Add word to wordsOnBoard map
        WordsOnBoard.addWord(start_and_endPosOfNewWord.get(0), start_and_endPosOfNewWord.get(1),
                start_and_endPosOfNewWord.get(2), start_and_endPosOfNewWord.get(3), madeNewWord);

        // refill the rack
        //int counter = useTiles.size();
        int counter = inputLetters.length();
        while (counter-- > 0) {
            boolean filled = player.getTileRack().fillUp();
            if (!filled) {
                if (player.getTileRack().getTilesAmount() == 0)
                    return false; // cannot refill because tiles bag empty, and also the player rack empty, so game over
            }
        }
        return true;
    }

    void recoverBoardGridContent() {

        for (int i = 0; i < tilesSetInto.size(); i++) {
            char letter = inputLetters.charAt(i);
            int letterPoints = 0;
            if (Character.isLowerCase(letter)) letterPoints = 3;
            else letterPoints = LetterPoints.letterMap.get(letter);
            String letter_with_points = String.valueOf(letter) + letterPoints + " ";

            if (direction.equals("right")) {
                GameBoard.reviseBoard(row, tilesSetInto.get(i), letter_with_points);
            }
            if (direction.equals("down")) {
                GameBoard.reviseBoard(tilesSetInto.get(i), column - 'a', letter_with_points);
            }
        }
    }

    void recoverBoardGridContentForInvalidMove() {

        // Check if the grid content was revised
        int col = column - 'a';

        for (int i = 0; i < tilesSetInto.size(); i++) {
                String gridContent = "";
                if (direction.equals("right")) {
                    gridContent = GameBoard.getBoardGridContent(row, tilesSetInto.get(i));
                    if ((gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                            && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '('))
                    {
                        gridContent = gridContent.substring(1);
                        GameBoard.reviseBoard(row, tilesSetInto.get(i), gridContent);
                    }
                }
                else {
                    gridContent = GameBoard.getBoardGridContent(tilesSetInto.get(i), col);
                    if ((gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                            && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '('))
                    {
                        gridContent = gridContent.substring(1);
                        GameBoard.reviseBoard(tilesSetInto.get(i), col, gridContent);
                    }
                }
            }
    }



    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
