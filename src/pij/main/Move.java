package pij.main;


import java.util.*;

import static pij.main.WordsOnBoard.words_on_board;

/**
 * A move has a string representing input tile letters, position of the starting square to set tiles,
 * direction of setting tiles, new created word on the board, integer values of row and col,
 * a player who made this move, boolean values to mark the move's validity and whether the first step in game,
 * one list of integers recording the squares where tiles are planned to set in,
 * another list of integers recording the square indexes of the new created word's first and last letter.
 *
 * @author Haomeng
 * @version 1.0
 */
public class Move {

    /** The input tile letters. Always non-null after object creation. */
    private final String inputLetters;

    /** Row of the square setting the first tile. Value assigned in constructor. */
    private final int row;

    /** Column of the square setting the first tile. Value assigned in constructor. */
    private final int col;

    /** The starting square location. Always non-null after object creation. */
    private final String position;

    /** The direction of setting tiles. Always non-null after object creation. */
    private final String direction;

    /** The player made this move. Always non-null after object creation. */
    private final Player player;

    /** To mark whether the move is valid.
     * Value assigned as true in constructor if the move successfully passed the validation. */
    private boolean isValid;

    /** To mark whether the move is first move in game. Always non-null after object creation. */
    private final boolean isFirstStep;

    /** To record the squares where tiles are planned to set in. Values assigned in constructor if the move
     *  validation has changed the board squares contents. Can be null.
     *  Instead of storing both row and column index for each square,
     *  there are only index numbers of columns for the move whose direction is right,
     *  and rows for the move whose direction is down.
     *  */
    private List<Integer> tilesSetInto;

    /** To record new created word on the board. Value assigned in constructor if the move
     *  successfully passed validation. If invalid move, stays in null. */
    private String madeNewWord;

    /** To record the square indexes of the new created word's first and last letter.
     * Value assigned in constructor if the move successfully passed validation.
     * If invalid move, stays in null. */
    private List<Integer> start_and_endPosOfNewWord;


    /**
     * Constructs a new Move with player's role, whether first move,
     * string of input letters, position and direction.
     * Assign values to part of the class attributes, and call method to validate the move.
     *
     * @param player the role of the Player; must not be null
     * @param isFirstStep whether the first move of game; must not be null
     * @param inputLetters string composed of letters from the using tiles; must not be null or empty
     * @param position position of the starting square to set tiles; must not be null or empty
     * @param direction direction of setting tiles; must not be null or empty
     */
    public Move(Player player, boolean isFirstStep, String inputLetters, String position, String direction) {
        this.inputLetters = inputLetters;
        this.player = player;
        this.isFirstStep = isFirstStep;
        this.position = position;
        this.row = Integer.parseInt(position.substring(1));
        this.col = position.charAt(0) - 'a';
        this.direction = direction.charAt(0) == 'r' ? "right" : "down";
        //< tilesSetInto, <newWord, idxOfNewWord> >
        AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>>
                setIntoPosition = validateMove(inputLetters, row, col, this.direction);
        // setIntoPosition will be null if the player's move input form is wrong,
        // or if the player try to use the word not coming from its own rack,
        // or if the player try to set tile into a grid already been covered by tile
        if (setIntoPosition != null) {
            tilesSetInto = setIntoPosition.getKey();
            AbstractMap.SimpleEntry<String, List<Integer>> newWord_and_idxes = setIntoPosition.getValue();
            if (newWord_and_idxes.getKey() != null) { //there is valid string returned
                isValid = true;
                madeNewWord = newWord_and_idxes.getKey();
                start_and_endPosOfNewWord = new ArrayList<>();
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(0));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(1));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(2));
                start_and_endPosOfNewWord.add(newWord_and_idxes.getValue().get(3));
            }
        }
    }


    /**
     * Returns the string composed of letters from the using tiles from this move.
     *
     * @return string composed of letters from the using tiles; always non-null and non-empty string
     */
    public String getInputLetters() { return this.inputLetters; }


    /**
     * Returns column of the square in which this move setting the first tile.
     *
     * @return column of the square setting the first tile
     */
    public int getCol() { return this.col; }


    /**
     * Returns row of the square in which this move setting the first tile.
     *
     * @return row of the square setting the first tile
     */
    public int getRow() { return this.row; }


    /**
     * Returns direction of setting tiles from this move.
     *
     * @return direction of setting tiles; always non-null and non-empty string
     */
    public String getDirection() { return this.direction; }


    /**
     * Returns the status of this move whether valid.
     *
     * @return whether the move is valid
     */
    public boolean getIsValid() { return this.isValid; }


    /**
     * Returns the squares location where tiles are planned to set in.
     *
     * @return list of row or column indexes of the squares where tiles are planned to set in;
     * can be null if the move did not pass CHECK 1 in validateMove method
     */
    public List<Integer> getTilesSetInto() { return this.tilesSetInto; }


    /**
     * Returns new created word on the board from this move.
     *
     * @return new created word on the board; can be null if the move is invalid
     */
    public String getMadeNewWord() { return this.madeNewWord; }


    /**
     * Returns indexes of the first and last square of the new created word on the board from this move.
     *
     * @return indexes of the first and last square of the new created word on the board;
     * can be null if the move did not successfully pass the validation in validateMove method
     */
    public List<Integer> get_start_and_endPosOfNewWord() { return this.start_and_endPosOfNewWord; }


    /**
     * Checks whether the move violates any of the game rules after setting specific tiles in.
     *
     * @param inputLetters string composed of letters from the using tiles;
     * @param row row of the square setting the first tile; must include between 1 and board size
     * @param col column of the square setting the first tile; must include between 0 and board size minus 1
     * @param direction direction of setting tiles; must not be null or empty string
     * @return a list of integer storing the row/col indexes of the squares where the using tiles
     * have been set in during validation, a string of new created word, a list of integers storing
     * the square indexes of the new created word's first and last letter. Can be null value.
     */
    public AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>> validateMove
    (String inputLetters, int row, int col, String direction) {

        // CHECK 1. has the starting position square already been covered
        String gridContent = GameBoard.getBoardSquareContent(row, col);
        Character gridTileLetter = isGridCoveredByTile(gridContent);
        boolean occupied = gridTileLetter != null;
        if (occupied) {
            if (player.isHuman())
                System.out.print("The starting square has already been covered by a tile. ");
            return null;
        }

        // CHECK 2. is the move out of board boundary
        AbstractMap.SimpleEntry<String, List<Integer>> preword_and_idx;
        if (isFirstStep)
            preword_and_idx = buildWordForFirstMove(inputLetters, row, col, direction);
        else
            preword_and_idx = buildWordUsingTileLetters(inputLetters, row, col, direction);

        String preWord = preword_and_idx.getKey();
        List<Integer> tilesSetInto = preword_and_idx.getValue();
        AbstractMap.SimpleEntry<String, List<Integer>> word_and_idx =
                new AbstractMap.SimpleEntry<String, List<Integer>>(null, null);
        AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>>
                forReturn = new AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>>
                (tilesSetInto, word_and_idx);

        if (preWord == null) { // means user's input out of the board boundary
            if (player.isHuman())
                System.out.print("You are setting tiles out of the board boundary. ");
            return forReturn;
        }

        // CHECK 3. is there one and only one legal word created on the current row (if right)/ col (if down)?
        int endIdxRow = row + preWord.length() - 1;
        int endIdxCol = col + preWord.length() - 1;
        ArrayList<Object> res = new ArrayList<>();

        if (direction.equals("right")) {
            if (this.isFirstStep) {
                res.add(inputLetters); res.add(col); res.add(endIdxCol);
            }
            else res = multiWordsOrNoneRow(preWord, row, col, endIdxCol, tilesSetInto);
        }
        if (direction.equals("down")) {
            if (this.isFirstStep) {
                res.add(inputLetters); res.add(row); res.add(endIdxRow);
            }
            else res = multiWordsOrNoneCol(preWord, row, col, endIdxRow, tilesSetInto);
        }
        if (res == null) {
            return forReturn;
        }

        // Check 4. is there any legal word constructed on each tile's right angle direction
        // because of this tile's adding?
        String newWord = (String)res.get(0);
        int startFrom = (int)res.get(1);
        int endAT = (int)res.get(2);
        List<Integer> idxOfNewWord = calNewWordIdx(row, col, startFrom, endAT, direction);

        boolean rightAngleCheck = isAnyRightAngleNewWord(direction, tilesSetInto, row, col);
        if (rightAngleCheck) {
            if (player.isHuman())
                System.out.print("There are more than one new word created in this move. ");
            return forReturn;
        }
        // Till here, can ensure that there is only one word created by this move.

        // CHECK 5. not allowed to place a word at right angles to a word already on the board without an overlap
        boolean rightAngleExistWordNoOverlap = isRightAngleExistWordNoOverlap(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (rightAngleExistWordNoOverlap) {
            if (player.isHuman())
                System.out.println("This move creates a word locating at right angle with existing word " +
                        "on the board without overlap.");
            return forReturn;
        }

        // CHECK 6. not allowed to place a complete word parallel immediately next to a word already played
        boolean parallelNextToAWord = isNextToParallelPlayedWord(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (parallelNextToAWord) {
            if (player.isHuman())
                System.out.println("This move creates a word locating immediately next to " +
                        "an existing parallel word on the board. ");
            return forReturn;
        }
        // Till here, each rule's checking has been passed,
        // the new word and its start and end indexes can be added into the returned value.
        forReturn.setValue(new AbstractMap.SimpleEntry<>(newWord, idxOfNewWord));
        return forReturn;
    }


    /**
     * Validates whether the first move in game has covered the centre square of the board.
     *
     * @param letters string composed of letters from the using tiles
     * @param position position of the starting square to set tiles
     * @param direction direction of setting tiles
     * @return boolean value represents whether the first move in game has covered the centre square of the board
     */
    public static boolean coveredCenterSquares(String letters, String position, String direction) {
        List<List<Integer>> coveringSquares = new ArrayList<>();
        int row = Integer.parseInt(position.substring(1));
        int col = position.charAt(0) - 'a';
        if (direction.equals("r")) {
            for (int i = 0; i < letters.length(); i++) {
                List<Integer> currentSquareIdx = List.of(row, col + i);
                coveringSquares.add(currentSquareIdx);
            }
        } else {
            for (int i = 0; i < letters.length(); i++) {
                List<Integer> currentSquareIdx = List.of(row + i, col);
                coveringSquares.add(currentSquareIdx);
            }
        }

        return coveringSquares.contains(GameBoard.getCenterSquare());
    }


    /**
     * Executing the valid move after validation, including revise the board squares contents,
     * take the move using tiles off the rack and refill the rack, and add the new created word
     * to the recording map of words on board.
     *
     * @return false if both the tiles bag and player's rack empty, true if executed successfully
     */
    public boolean execute() {

        // Revise the board targeting squares contents
        reviseBoardContentForValidMove();

        // Take tiles off the rack
        for (int i = 0; i < inputLetters.length(); i++) {
            char letter = inputLetters.charAt(i);
            player.getTileRack().takeOutTileFromRack(letter);
        }

        // Add word to wordsOnBoard map
        WordsOnBoard.addWord(start_and_endPosOfNewWord.get(0), start_and_endPosOfNewWord.get(1),
                start_and_endPosOfNewWord.get(2), start_and_endPosOfNewWord.get(3), madeNewWord);

        // Refill the rack from tiles bag
        int counter = inputLetters.length();
        while (counter-- > 0) {
            boolean filled = player.getTileRack().fillUp();
            if (!filled) {
                // Once failed to refill means tiles bag empty and also the player rack empty
                // so if a move execute method return false value representing game over
                if (player.getTileRack().getTilesAmount() == 0)
                    return false;
            }
        }
        return true;
    }


    /**
     * To be used after the move has successfully passed the validation.
     * Revises the board targeting squares contents into the form of letter with the corresponding points.
     * For example, change "G{3}" to "G2", or "T." to "T1", or "t{3}" to "t3" if there is wildcard.
     * Read method buildWordUsingTileLetters for more how it became.
     */
    void reviseBoardContentForValidMove() {

        for (int i = 0; i < tilesSetInto.size(); i++) {
            char letter = inputLetters.charAt(i);
            int letterPoints;
            if (Character.isLowerCase(letter)) letterPoints = 3;
            else letterPoints = LetterPoints.getMap().get(letter);
            String letter_with_points = " " + letter + letterPoints + " ";
            if (letterPoints > 9) letter_with_points =
                    letter_with_points.stripTrailing();

            if (direction.equals("right")) {
                GameBoard.reviseBoard(row, tilesSetInto.get(i), letter_with_points);
            }
            if (direction.equals("down")) {
                GameBoard.reviseBoard(tilesSetInto.get(i), col, letter_with_points);
            }
        }
    }


    /**
     * Revises the board targeting squares contents to the original state.
     * For example, change "G{3}" to "{3}", or "T." to ".", etc.
     * Read method buildWordUsingTileLetters for more how it became.
     */
    void recoverBoardSquareContentToInitial() {

        // Check if the grid content was revised
        for (Integer integer : tilesSetInto) {
            String gridContent = "";
            if (direction.equals("right")) {
                gridContent = GameBoard.getBoardSquareContent(row, integer);
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(row, integer, gridContent);
            } else {
                gridContent = GameBoard.getBoardSquareContent(integer, col);
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(integer, col, gridContent);
            }
        }
    }


    /**
     * Revises the board targeting squares contents to the original state.
     * For example, change "G{3}" to "{3}", or "T." to ".", etc.
     * Read method buildWordUsingTileLetters for more how it became.
     *
     * @param rowIdx row of the square setting the first tile
     * @param colIdx column of the square setting the first tile
     * @param startFrom index of the new created word's first letter's row/column (depends on the direction)
     * @param endAT index of the new created word's last letter's row/column (depends on the direction)
     * @param direction direction of setting tiles
     * @return a list of integers storing the indexes of the new created word's first and last letter.
     */
    public static List<Integer> calNewWordIdx(int rowIdx, int colIdx, int startFrom, int endAT,
                                              String direction) {
        List<Integer> idxOfNewWord = new ArrayList<>();
        if (direction.equals("right")) {
            idxOfNewWord.add(rowIdx);
            idxOfNewWord.add(startFrom);
            idxOfNewWord.add(rowIdx);
            idxOfNewWord.add(endAT);
        } else {
            idxOfNewWord.add(startFrom);
            idxOfNewWord.add(colIdx);
            idxOfNewWord.add(endAT);
            idxOfNewWord.add(colIdx);
        }
        return idxOfNewWord;
    }


    /**
     * Checks whether the square has already been covered by tile from the square's contents.
     *
     * @param curSquare contents of the validating square
     * @return a character of alphabet letter if the square is covered by tile, null if it is vacant.
     */
    public static Character isGridCoveredByTile(String curSquare) {
        if (curSquare.contains(".") || curSquare.contains("(") || curSquare.contains("{")) {
            return null;
        } else {
            for (int i = 0; i < curSquare.length(); i++)
                if (Character.isAlphabetic(curSquare.charAt(i))) return curSquare.charAt(i);
        }
        return null;
    }


    /**
     * Checks whether the square's contents contain an alphabet letter, if yes, take the character value.
     *
     * @param curSquare contents of the targeting square
     * @return a character of alphabet letter if the square's contents contained one, null if there is not.
     */
    public static Character getLetter(String curSquare) {
        for (int i = 0; i < curSquare.length(); i++) {
            if (Character.isAlphabetic(curSquare.charAt(i))) {
                return curSquare.charAt(i);
            }
        }
        return null;
    }


    /**
     * Builds a string using input letters for the case of the first move in game.
     * Revises the targeting squares contents for validating with game rules later on in validateMove method.
     * The return string can be null value if the move made letters went out of the board bound.
     * The return list of integer is always non-null value.
     *
     * @param inputLetters string composed of letters from the using tiles
     * @param rowIdx row of the square setting the first tile
     * @param colIdx column of the square setting the first tile
     * @param direction direction of setting tiles
     * @return a string of new created but yet validated word, and a list of integer storing
     * the row/col indexes of the squares where the using tiles have been set in.
     */
    public AbstractMap.SimpleEntry<String, List<Integer>> buildWordForFirstMove(String inputLetters, int rowIdx, int colIdx, String direction) {

        String firstStepWord = "";
        List<Integer> tilesSetInto = new ArrayList<>();
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.getSize() && bound2 < GameBoard.getSize()) {
            if (direction.equals("down")) {
                char curLetter = inputLetters.charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardSquareContent(rowIdx + gridCounter, colIdx);
                firstStepWord += curLetter;
                GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                tilesSetInto.add(rowIdx + gridCounter);
                bitCounter++;
            }
            if (direction.equals("right")) {
                char curLetter = inputLetters.charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardSquareContent(rowIdx, colIdx + gridCounter);
                firstStepWord += curLetter;
                GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
                tilesSetInto.add(colIdx + gridCounter);
                bitCounter++;
            }
            gridCounter++;
            if (direction.equals("down")) bound1 = rowIdx + gridCounter;
            if (direction.equals("right")) bound2 = colIdx + gridCounter;
        }

        // Out of legal bounds before using all the tiles
        if (bitCounter < inputLetters.length()) {
            return new AbstractMap.SimpleEntry<String, List<Integer>>(null, tilesSetInto);
        }
        return new AbstractMap.SimpleEntry<String, List<Integer>>(firstStepWord, tilesSetInto);
    }


    /**
     * Builds a string using input letters and adds to the letters if there is already on the board throughout
     * the direction of the tiles moving forward.
     * Revises the targeting squares contents for validating with game rules later on in validateMove method.
     * This is for the case of NOT the first move in game.
     * The return string can be null value if the move made letters went out of the board bound.
     * The return list of integer is always non-null value.
     *
     * @param inputLetters string composed of letters from the using tiles
     * @param rowIdx row of the square setting the first tile
     * @param colIdx column of the square setting the first tile
     * @param direction direction of setting tiles
     * @return a new created string using all the letter tiles from player's input, and a list of integer storing
     * the row/col indexes of the squares where the using tiles have been set in.
     */
    public AbstractMap.SimpleEntry<String, List<Integer>> buildWordUsingTileLetters(String inputLetters, int rowIdx, int colIdx, String direction) {

        String newCreatedWordUndone = "";
        List<Integer> tilesSetInto = new ArrayList<>();
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        // Only add letter into square for the word validation
        // If move is valid, square contents needs to be changed into letter
        // followed by the corresponding points.
        // For example, (3) is changed into G(3) at this stage,
        // and will be changed into G2 if it's a valid move in execute method.
        // If move is invalid, square contents need to be recovered to initial.
        // For example, (3) is changed into G(3) at this stage, and will be reset to (3).
        while (bitCounter < inputLetters.length() && bound1 < GameBoard.getSize() && bound2 < GameBoard.getSize()) {

            if (direction.equals("down")) {
                String gridContent = GameBoard.getBoardSquareContent(rowIdx + gridCounter, colIdx);
                Character gridTileLetter = isGridCoveredByTile(gridContent);
                if (gridTileLetter == null) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardSquareContent(rowIdx + gridCounter, colIdx);
                    newCreatedWordUndone += curLetter;
                    GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                    tilesSetInto.add(rowIdx + gridCounter);
                    bitCounter++;
                } else {
                    newCreatedWordUndone += gridTileLetter;
                }
            }

            if (direction.equals("right")) {
                String gridContent = GameBoard.getBoardSquareContent(rowIdx, colIdx + gridCounter);
                Character gridTileLetter = isGridCoveredByTile(gridContent);
                if (gridTileLetter == null) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardSquareContent(rowIdx, colIdx + gridCounter);
                    newCreatedWordUndone += curLetter;
                    //same as above in "down" case
                    GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
                    tilesSetInto.add(colIdx + gridCounter);
                    bitCounter++;
                } else {
                    newCreatedWordUndone += gridTileLetter;
                }
            }
            gridCounter++;
            if (direction.equals("down")) bound1 = rowIdx + gridCounter;
            if (direction.equals("right")) bound2 = colIdx + gridCounter;
        }

        // Out of legal bounds before using all the tiles
        if (bitCounter < inputLetters.length())
            return new AbstractMap.SimpleEntry<String, List<Integer>>(null, tilesSetInto);

        return new AbstractMap.SimpleEntry<String, List<Integer>>(newCreatedWordUndone, tilesSetInto);
    }


    /**
     * To check for the move that having "right" direction, whether leading to zero legal word or
     * more than one legal word being created on the row.
     *
     * @param preWord the new created string using all the letter tiles from player's input
     * @param rowIdx first tile row index
     * @param colIdx first tile column index
     * @param endIdxCol last tile column index
     * @param tilesSetInto a list of integer storing where the using tiles have been set in
     * @return a list of objects comprised of a legal word, two column indexes are respectively
     * the first and the last letter of the created word. Will be null value if there is zero or
     * more than one legal new word created on the row.
     */
    public ArrayList<Object> multiWordsOrNoneRow(String preWord, int rowIdx, int colIdx, int endIdxCol, List<Integer> tilesSetInto) {

        int allFilledFrom = colIdx;
        int allFilledTo = endIdxCol;
        boolean preCapLegalWord = false;

        for (int left = colIdx - 1; left >= 0; left--) {
            Character letter = getLetter(GameBoard.getBoardSquareContent(rowIdx, left));
            if (letter == null) {
                allFilledFrom = left + 1;
                break;
            } else {
                allFilledFrom = left;
            }
        }

        for (int right = endIdxCol; right < GameBoard.getSize(); right++) {
            Character letter = getLetter(GameBoard.getBoardSquareContent(rowIdx, right));
            if (letter == null) {
                allFilledTo = right - 1;
                break;
            } else {
                allFilledTo = right;
            }
        }

        int legalWordsCounter = 0;
        int startFrom = 0, endAT = 0;
        // Get the pre-cap string and post-cap string
        String preCap = "";
        for (int i = allFilledFrom; i < colIdx; i++) preCap += isGridCoveredByTile(GameBoard.getBoardSquareContent(rowIdx, i));
        String postCap = "";
        for (int i = endIdxCol + 1; i <= allFilledTo; i++) postCap += isGridCoveredByTile(GameBoard.getBoardSquareContent(rowIdx, i));

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String useTilesGrids = "";
            for (int j = 0; j <= allFilledTo - colIdx; j++) {
                useTilesGrids += getLetter(GameBoard.getBoardSquareContent(rowIdx, colIdx + j));
                String outcome = currentPreCap + useTilesGrids;
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    startFrom = allFilledFrom + i;
                    endAT = colIdx + j;
                }
                if (legalWordsCounter > 0) {
                    preCapLegalWord = true;
                    break;
                }
            }
        }

        String newWord = "";
        // Try post-cap plus each letter before using the last tiles
        for (int i = 0; i < postCap.length(); i++) {
            String currentPostCap = postCap.substring(0,postCap.length()-i);
            String useTilesGrids = "";
            for (int j = 0; j <= endIdxCol - allFilledFrom; j++) {
                useTilesGrids = getLetter(GameBoard.getBoardSquareContent(rowIdx, endIdxCol - j)) + useTilesGrids;
                String outcome = useTilesGrids + currentPostCap;
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    newWord = outcome;
                    startFrom = endIdxCol - j;
                    endAT = allFilledTo - i;
                }
                if (legalWordsCounter > 1) {
                    if (player.isHuman())
                        System.out.print("There are more than one new word created in this move. ");
                    return null;
                }
            }
        }
        // Till now, legalWordsCounter == 0 or 1
        // preword itself maybe legal word or not
        boolean preWordIsValid = false;
        if (WordList.validateWord(preWord.toLowerCase())) {
            legalWordsCounter++;
            preWordIsValid = true;
        }

        if (legalWordsCounter == 0 ) {
            if (player.isHuman())
                System.out.print("This move does not create any legal word. ");
            return null;
        }
        if (legalWordsCounter > 1) {
            if (player.isHuman())
                System.out.print("There are more than a new word created in this move.");
            return null;
        }
        if (preCapLegalWord) {
            if (player.isHuman())
                System.out.println("This move created a new word but it is not created from " +
                    "the first tile to your supposed direction.");
            return null;
        }

        if (!preWordIsValid) {
            if (endAT < endIdxCol || startFrom > colIdx) {
                if (player.isHuman())
                    System.out.println("This move created a new word but it is not using " +
                            "all the tiles you were choosing.");
                return null;
            }
        }

        ArrayList<Object> res = new ArrayList<>();
        if (!newWord.isEmpty()) {
            if (startFrom == colIdx) {
                res.add(newWord); res.add(startFrom); res.add(endAT);
            } else {
                if (player.isHuman())
                    System.out.println("This move created a new word but it is not created from " +
                        "the first tile to your supposed direction.");
                return null;
            }
        } else {
            //if preWord is including an old tile, then add; else return false
            if (preWord.length() > tilesSetInto.size()) {
                res.add(preWord); res.add(colIdx); res.add(endIdxCol);
            } else {
                if (player.isHuman())
                    System.out.println("This move created a new word but it did not cross " +
                            "any existing tile on the board.");
                return null;
            }
        }
        return res;
    }

    /**
     * To check for the move that having "down" direction, whether leading to zero legal word or
     * more than one legal word being created on the column.
     *
     * @param preWord the new created string using all the letter tiles from player's input
     * @param rowIdx first tile row index
     * @param colIdx first tile column index
     * @param endIdxRow last tile row index
     * @param tilesSetInto a list of integer storing where the using tiles have been set in
     * @return a list of objects comprised of a legal word, two column indexes are respectively
     * the first and the last letter of the created word. Will be null value if there is zero or
     * more than one legal new word created on the column.
     */
    public ArrayList<Object> multiWordsOrNoneCol(String preWord, int rowIdx, int colIdx, int endIdxRow, List<Integer> tilesSetInto) {

        int allFilledFrom = rowIdx;
        int allFilledTo = endIdxRow;
        boolean preCapLegalWord = false;

        for (int up = rowIdx - 1; up >= 1; up--) {
            Character letter = getLetter(GameBoard.getBoardSquareContent(up, colIdx));
            if (letter == null) {
                allFilledFrom = up + 1;
                break;
            } else {
                allFilledFrom = up;
            }
        }

        for (int down = endIdxRow; down <= GameBoard.getSize(); down++) {
            Character letter = getLetter(GameBoard.getBoardSquareContent(down, colIdx));
            if (letter == null) {
                allFilledTo = down - 1;
                break;
            } else {
                allFilledTo = down;
            }
        }

        int legalWordsCounter = 0;
        int startFrom = 0, endAT = 0;

        String preCap = "";
        for (int i = allFilledFrom; i < rowIdx; i++) preCap += getLetter(GameBoard.getBoardSquareContent(i, colIdx));
        String postCap = "";
        for (int i = endIdxRow + 1; i <= allFilledTo; i++) postCap += getLetter(GameBoard.getBoardSquareContent(i, colIdx));

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String currentTilesWithPostCap = "";
            for (int j = 0; j <= allFilledTo - rowIdx; j++) {
                currentTilesWithPostCap += getLetter(GameBoard.getBoardSquareContent(rowIdx + j, colIdx));
                String outcome = currentPreCap + currentTilesWithPostCap;
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    startFrom = allFilledFrom + i;
                    endAT = rowIdx + j;
                }
                if (legalWordsCounter > 0) {
                    preCapLegalWord = true;
                    break;
                }
            }
        }

        // Try post-cap plus each letter before using the last tiles
        String newWord = "";
        for (int i = 0; i < postCap.length(); i++) {
            String currentPostCap = postCap.substring(0, postCap.length() - i);
            String useTilesGrids = "";
            int temp = endIdxRow - allFilledFrom;
            for (int j = 0; j <= endIdxRow - allFilledFrom; j++) {
                useTilesGrids = getLetter(GameBoard.getBoardSquareContent(endIdxRow - j, colIdx)) + useTilesGrids;
                String outcome = useTilesGrids + currentPostCap;
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    newWord = outcome;
                    startFrom = endIdxRow - j;
                    endAT = allFilledTo - i;
                }
                if (legalWordsCounter > 1) {
                    if (player.isHuman())
                        System.out.print("There are more than one new word created in this move. ");
                    return null;
                }
            }
        }

        boolean preWordIsValid = false;
        if (WordList.validateWord(preWord.toLowerCase())) {
            legalWordsCounter++;
            preWordIsValid = true;
        }

        if (legalWordsCounter == 0 ) {
            if (player.isHuman())
                System.out.print("This move does not create any legal word. ");
            return null;
        }
        if (legalWordsCounter > 1) {
            if (player.isHuman())
                System.out.print("There are more than a new word created in this move.");
            return null;
        }
        if (preCapLegalWord) {
            if (player.isHuman())
                System.out.println("This move created a new word but it is not created from " +
                        "the first tile to your supposed direction.");
            return null;
        }
        if (!preWordIsValid) {
            if (endAT < endIdxRow || startFrom > rowIdx) {
                if (player.isHuman())
                    System.out.println("This move created a new word but it is not using " +
                            "all the tiles you were choosing.");
                return null;
            }
        }

        ArrayList<Object> res = new ArrayList<>();
        if (!newWord.isEmpty()) {
            if (startFrom == rowIdx) {
                res.add(newWord);
                res.add(startFrom);
                res.add(endAT);
            } else {
                if (player.isHuman())
                    System.out.println("This move created a new word but it is not created from " +
                            "the first tile to your supposed direction.");
                return null;
            }
        } else {
            //if preWord is including an old tile, then add; else return false
            if (preWord.length() > tilesSetInto.size()) {
                res.add(preWord);
                res.add(rowIdx);
                res.add(endIdxRow);
            } else {
                if (player.isHuman())
                    System.out.println("This move created a new word but it did not cross " +
                            "any existing tile on the board.");
                return null;
            }
        }

        return res;
    }


    /**
     * To check for the move that whether there is any new word occurrence on the right angel line
     * of each using tile in the move.
     *
     * @param direction direction of setting tiles
     * @param tilesSetInto a list of integer storing where the using tiles have been set in
     * @param rowIdx first tile row index
     * @param colIdx first tile column index
     *
     * @return a boolean result whether there is any new word occurrence on the right angle line
     * of each using tile in the move.
     */
    public boolean isAnyRightAngleNewWord(String direction, List<Integer> tilesSetInto, int rowIdx, int colIdx) {

        for (int i : tilesSetInto) {

            if (direction.equals("right")) {
                // Check each column of the tiles trying to set in
                // First find out the range of continuously covered squares in the current column
                int allFilledFrom = 1, allFilledTo = GameBoard.getSize();
                for (int up = rowIdx - 1; up >= 1; up--) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardSquareContent(up, i));
                    if (letter == null) {
                        allFilledFrom = up + 1;
                        break;
                    } else {
                        allFilledFrom = up;
                    }
                }
                for (int down = rowIdx + 1; down <= GameBoard.getSize(); down++) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardSquareContent(down, i));
                    if (letter == null) {
                        allFilledTo = down - 1;
                        break;
                    } else {
                        allFilledTo = down;
                    }
                }
                for (int a = allFilledFrom; a <= rowIdx; a++) {
                    String curTry = "";
                    // build each pre-cap including the tile letter
                    for (int b = a; b <= rowIdx; b++) {
                        curTry += getLetter(GameBoard.getBoardSquareContent(b, i));
                    }
                    if (rowIdx == allFilledTo) {
                        // there is no post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = rowIdx + 1; c <= allFilledTo; c++) {
                            curTry += getLetter(GameBoard.getBoardSquareContent(c, i));
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            } else {
                // Check each row of the tiles trying to set in
                // First find out the range of continuously covered squares in the current row
                int allFilledFrom = 0, allFilledTo = GameBoard.getSize() - 1;
                for (int left = colIdx - 1; left >= 0; left--) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardSquareContent(i, left));
                    if (letter == null) {
                        allFilledFrom = left + 1;
                        break;
                    } else {
                        allFilledFrom = left;
                    }
                }
                for (int right = colIdx + 1; right < GameBoard.getSize(); right++) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardSquareContent(i, right));
                    if (letter == null) {
                        allFilledTo = right - 1;
                        break;
                    } else {
                        allFilledTo = right;
                    }
                }
                for (int a = allFilledFrom; a <= colIdx; a++) {
                    String curTry = "";
                    // build each pre-cap including the tile letter
                    for (int b = a; b <= colIdx; b++) {
                        curTry += getLetter(GameBoard.getBoardSquareContent(i, b));
                    }
                    if (colIdx == allFilledTo) {
                        // there is not post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = colIdx + 1; c <= allFilledTo; c++) {
                            curTry += getLetter(GameBoard.getBoardSquareContent(i, c));
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * To check for the move that whether the move leads the new created word placing at right angle to a word
     * already on the board without an overlap.
     *
     * @param direction direction of setting tiles
     * @param nwStartRow row index of the new created word's first square
     * @param nwStartCol column index of the new created word's first square
     * @param nwEndRow row index of the new created word's last square
     * @param nwEndCol column index of the new created word's last square
     * @return a boolean result whether the move leads the new created word placing at right angle to a word
     * already on the board without an overlap.
     */
    public boolean isRightAngleExistWordNoOverlap(String direction, int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean rightAngleNoOverlap = false;
       for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            int startRow = idxes.get(0);
            int startCol = idxes.get(1);
            int endRow = idxes.get(2);
            int endCol = idxes.get(3);

            if (direction.equals("right")) {
                // If the existing word is in same direction, continue
                if (startRow == endRow) continue;
                if ( ((startCol == nwStartCol - 1 || startCol == nwEndCol + 1) && (nwStartRow >= startRow && nwStartRow <= endRow))
                        || ((startRow == nwStartRow + 1 || endRow == nwStartRow - 1) && (startCol >= nwStartCol && startCol <= nwEndCol)) )
                    rightAngleNoOverlap = true;
            }
            if (direction.equals("down")) {
                if (startCol == endCol) continue;
                if ( ((endCol == nwStartCol - 1 || startCol == nwStartCol + 1) && (nwStartRow <= startRow && nwEndRow >= startRow))
                        || ((startRow == nwStartRow - 1 || startRow == nwEndRow + 1) && (startCol <= nwStartCol && endCol >= nwStartCol)) )
                    rightAngleNoOverlap = true;
            }
        }
        return rightAngleNoOverlap;
    }


    /**
     * To check for the move that whether the move is placing a complete word parallel immediately next to a word
     * already played.
     *
     * @param direction direction of setting tiles
     * @param nwStartRow row index of the new created word's first square
     * @param nwStartCol column index of the new created word's first square
     * @param nwEndRow row index of the new created word's last square
     * @param nwEndCol column index of the new created word's last square
     * @return a boolean result whether the move is placing a complete word parallel immediately next to a word
     * already played.
     */
    public boolean isNextToParallelPlayedWord(String direction,
                                                     int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean nextTo = false;

        for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            int startRow = idxes.get(0);
            int startCol = idxes.get(1);
            int endRow = idxes.get(2);
            int endCol = idxes.get(3);

            if (direction.equals("right")) {
                // If the existing word is not in same direction, continue
                if (startRow != endRow) continue;
                if (((endCol == nwStartCol - 1 || startCol == nwEndCol + 1) && (startRow == nwStartRow))
                        || ((startRow == nwStartRow + 1 || startRow == nwStartRow - 1) &&
                        ((startCol >= nwStartCol && startCol <= nwEndCol) || (endCol >= nwStartCol && endCol <= nwEndCol))))
                    nextTo = true;
            }
            if (direction.equals("down")) {
                if (startCol != endCol) continue;
                if (((endRow == nwStartRow - 1 || startRow == nwEndRow + 1) && (startCol == nwStartCol))
                        || ((startCol == nwStartCol + 1 || startCol == nwStartCol - 1) &&
                        ((startRow >= nwStartRow && startRow <= nwEndRow) || (endRow >= nwStartRow && endRow <= nwEndRow)))) {
                    nextTo = true;
                }
            }
        }
        return nextTo;
    }


    /**
     * Enable to print out this Move's contents.
     *
     * @return a String represents the Move's contents composed of input tile letters, position and direction.
     */
    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
