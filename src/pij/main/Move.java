package pij.main;


import java.util.*;

import static pij.main.WordsOnBoard.words_on_board;

public class Move {
    private final String inputLetters;
    private int row;
    private int col;
    private final String position;
    private String direction;
    private final Player player;
    private boolean isValid;
    private final boolean isFirstStep;
    private List<Integer> tilesSetInto;
    private String madeNewWord = "";
    private List<Integer> start_and_endPosOfNewWord;

    public Move(Player player, boolean isFirstStep, String inputLetters, String position, String direction) {
        this.inputLetters = inputLetters;
        this.player = player;
        this.isFirstStep = isFirstStep;
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


    public String getInputLetters() { return this.inputLetters; }
    public int getCol() { return this.col; }
    public int getRow() { return this.row; }
    public String getDirection() { return this.direction; }
    public boolean getIsValid() { return this.isValid; }
    public List<Integer> getTilesSetInto() { return this.tilesSetInto; }
    public String getMadeNewWord() { return this.madeNewWord; }
    public List<Integer> get_start_and_endPosOfNewWord() { return this.start_and_endPosOfNewWord; }


    public AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>> validateMove() {

        this.row = Integer.parseInt(position.substring(1));
        this.col = position.charAt(0) - 'a';
        this.direction = direction.charAt(0) == 'r' ? "right" : "down";

        //Check whether anywhere violates the game word rule after these tiles adding
        //return WordsOnBoard.validateWord(inputLetters, row, column, direction);
        return validateWord(inputLetters, row, col, direction);
    }


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


    // Change the grids on board contents as the user input tiles letters

    // If the move is valid,
    // need to update the grid content from the form such as "G{3}" to "G2" or "T." to "T1", or "t{3}" to "t3"(wildcard)
    // See method buildWordUsingTileLetters in WordsOnBoard class
    public boolean execute() {

        recoverBoardGridContent();

        for (int i = 0; i < inputLetters.length(); i++) {
            char letter = inputLetters.charAt(i);
            player.getTileRack().takeOutTileFromRack(letter);
        }

        // Add word to wordsOnBoard map
        WordsOnBoard.addWord(start_and_endPosOfNewWord.get(0), start_and_endPosOfNewWord.get(1),
                start_and_endPosOfNewWord.get(2), start_and_endPosOfNewWord.get(3), madeNewWord);

        // refill the rack
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
            else letterPoints = LetterPoints.getMap().get(letter);
            String letter_with_points = " " + letter + letterPoints + " "; //" g3 "

            if (direction.equals("right")) {
                GameBoard.reviseBoard(row, tilesSetInto.get(i), letter_with_points);
            }
            if (direction.equals("down")) {
                GameBoard.reviseBoard(tilesSetInto.get(i), col, letter_with_points);
            }
        }
    }

    void recoverBoardGridContentForInvalidMove() {

        // Check if the grid content was revised
        for (Integer integer : tilesSetInto) {
            String gridContent = "";
            if (direction.equals("right")) {
                gridContent = GameBoard.getBoardGridContent(row, integer);
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(row, integer, gridContent);
            } else {
                gridContent = GameBoard.getBoardGridContent(integer, col);
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(integer, col, gridContent);
            }
        }
    }


    public AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>> validateWord(String inputLetters, int row, int col, String direction) {

        // rowIdx stays the same as arg row because the game board content at [0][0] is the size number,
        // not the first line of the board

        // CHECK 1. is the starting position already having a letter
        String gridContent = GameBoard.getBoardGridContent(row, col);
        Character gridTileLetter = isGridCoveredByTile(gridContent);
        boolean occupied = gridTileLetter != null;
        if (occupied) {
            if (player.isHuman())
                System.out.print("The starting square has already been covered by a tile. ");
            return null;
        }

        // CHECK 2. is the move out of board boundary
        // First fill up the row/column (depends on the direction) skipping the grid already had a letter
        // var newCreatedWordUndone - The word composed of letters from the first tile to the end, maybe it is the new
        // word on its own, maybe it is the substring of the new word constructed with other next letters in the same row/col
        AbstractMap.SimpleEntry<String, List<Integer>> preword_and_idx;
        if (this.isFirstStep)
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

        if (preWord == null) { // means user's input out of the board bound
            if (player.isHuman())
                System.out.print("You are setting tiles out of the board boundary. ");
            return forReturn;
        }

        // CHECK 3. is there one and only one legal word constructed on the current row (if right)/ col (if down)?
        // if there are more than one or zero, return false;
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


        // Check 3. is there any new legal word constructed on each tile's right angle direction because of this tile?
        // if yes, return null;
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
        // Till here, can ensure that there is not more than one word constructed
        // due to the new adding tiles both horizontally and vertically.


        // CHECK 4. not allowed to place a word at right angles to a word already on the board without an overlap

        boolean rightAngleExistWordNoOverlap = isRightAngleExistWordNoOverlap(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (rightAngleExistWordNoOverlap) {
            if (player.isHuman())
                System.out.println("This move creates a word locating at right angle with existing word " +
                        "on the board without overlap.");
            return forReturn;
        }


        // CHECK 5. not allowed to place a complete word parallel immediately next to a word already played
        boolean parallelNextToAWord = isNextToParallelPlayedWord(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (parallelNextToAWord) {
            if (player.isHuman())
                System.out.println("This move creates a word locating immediately next to " +
                        "an existing parallel word on the board. ");
            return forReturn;
        }

        forReturn.setValue(new AbstractMap.SimpleEntry<>(newWord, idxOfNewWord));
        return forReturn;
    }


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


    public static Character isGridCoveredByTile(String curGrid) {
        if (curGrid.contains(".") || curGrid.contains("(") || curGrid.contains("{")) {
            return null;
        } else {
            for (int i = 0; i < curGrid.length(); i++) {
                if (Character.isAlphabetic(curGrid.charAt(i))) {
                    return curGrid.charAt(i);
                }
            }
        }
        return null;
    }


    public static Character getLetter(String curGrid) {
        for (int i = 0; i < curGrid.length(); i++) {
            if (Character.isAlphabetic(curGrid.charAt(i))) {
                return curGrid.charAt(i);
            }
        }
        return null;
    }


    public AbstractMap.SimpleEntry<String, List<Integer>> buildWordForFirstMove(String inputLetters, int rowIdx, int colIdx, String direction) {

        //System.out.println("Build word for first step"); //debug
        String firstStepWord = "";
        List<Integer> tilesSetInto = new ArrayList<>();
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.getSize() && bound2 < GameBoard.getSize()) {
            if (direction.equals("down")) {
                char curLetter = inputLetters.charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                firstStepWord += curLetter;
                GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                //System.out.println(GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx));//debug
                tilesSetInto.add(rowIdx + gridCounter);
                bitCounter++;
            }
            if (direction.equals("right")) {
                char curLetter = inputLetters.charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter);
                firstStepWord += curLetter;
                GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
                //System.out.println(GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter));//debug
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


    public AbstractMap.SimpleEntry<String, List<Integer>> buildWordUsingTileLetters(String inputLetters, int rowIdx, int colIdx, String direction) {

        //System.out.println("Try to build word with tiles"); //debug
        String newCreatedWordUndone = "";
        List<Integer> tilesSetInto = new ArrayList<>();
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.getSize() && bound2 < GameBoard.getSize()) {

            if (direction.equals("down")) {
                String gridContent = GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                Character gridTileLetter = isGridCoveredByTile(gridContent);
                if (gridTileLetter == null) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                    newCreatedWordUndone += curLetter;
                    GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                    tilesSetInto.add(rowIdx + gridCounter);
                    //not score, only letter for testing
                    //if move is valid, board content needs to add the corresponding scores
                    //e.g. (3) is changed into G(3) at this stage, will be change into G2 if it's valid move when it's executed
                    //if move is invalid, board grid contents need to recover (cut the letter of the head)
                    //e.g. (3) is changed into G(3) at this stage, will be reset into (3)
                    bitCounter++;
                } else {
                    newCreatedWordUndone += gridTileLetter;
                }
            }

            if (direction.equals("right")) {
                String gridContent = GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter);
                Character gridTileLetter = isGridCoveredByTile(gridContent);
                if (gridTileLetter == null) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter);
                    newCreatedWordUndone += curLetter;
                    //same as above in "down" case
                    GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
                    //System.out.println(GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter));//debug
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

        // Out of legal bounds before using all the tiles,
        // or Tiles did not go through any letter on board to contribute new word.
        if (bitCounter < inputLetters.length())
            return new AbstractMap.SimpleEntry<String, List<Integer>>(null, tilesSetInto);

        return new AbstractMap.SimpleEntry<String, List<Integer>>(newCreatedWordUndone, tilesSetInto);
    }


    /**
     * @param rowIdx first tile row index
     * @param colIdx first tile column index
     * @param endIdxCol last tile column index
     */
    public ArrayList<Object> multiWordsOrNoneRow(String preWord, int rowIdx, int colIdx, int endIdxCol, List<Integer> tilesSetInto) {

        // 1. find out this row from where to where (index) having consistent letters connected with the current word
        // 2. then use this below way to check how many legal word it can construct including the tiles word as a part

        //check each col the tile is set in
        //first find out the consist word with the tile
        int allFilledFrom = colIdx;
        int allFilledTo = endIdxCol;
        boolean preCapLegalWord = false;

        for (int left = colIdx - 1; left >= 0; left--) {
            Character letter = getLetter(GameBoard.getBoardGridContent(rowIdx, left));
            if (letter == null) {
                allFilledFrom = left + 1;
                break;
            } else {
                allFilledFrom = left;
            }
        }

        for (int right = endIdxCol; right < GameBoard.getSize(); right++) {
            Character letter = getLetter(GameBoard.getBoardGridContent(rowIdx, right));
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
        for (int i = allFilledFrom; i < colIdx; i++) preCap += isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, i));
        String postCap = "";
        for (int i = endIdxCol + 1; i <= allFilledTo; i++) postCap += isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, i));

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String useTilesGrids = "";
            for (int j = 0; j <= allFilledTo - colIdx; j++) {
                useTilesGrids += getLetter(GameBoard.getBoardGridContent(rowIdx, colIdx + j));
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
                useTilesGrids = getLetter(GameBoard.getBoardGridContent(rowIdx, endIdxCol - j)) + useTilesGrids;
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
                res.add(preWord);
                res.add(colIdx);
                res.add(endIdxCol);
            } else {
                if (player.isHuman())
                    System.out.println("This move created a new word but it did not cross " +
                            "any existing tile on the board.");
                return null;
            }
        }
        return res;
    }


    public ArrayList<Object> multiWordsOrNoneCol(String preWord, int rowIdx, int colIdx, int endIdxRow, List<Integer> tilesSetInto) {

        int allFilledFrom = rowIdx;
        int allFilledTo = endIdxRow;
        boolean preCapLegalWord = false;

        for (int up = rowIdx - 1; up >= 1; up--) {
            Character letter = getLetter(GameBoard.getBoardGridContent(up, colIdx));
            if (letter == null) {
                allFilledFrom = up + 1;
                break;
            } else {
                allFilledFrom = up;
            }
        }

        for (int down = endIdxRow; down <= GameBoard.getSize(); down++) {
            Character letter = getLetter(GameBoard.getBoardGridContent(down, colIdx));
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
        for (int i = allFilledFrom; i < rowIdx; i++) preCap += getLetter(GameBoard.getBoardGridContent(i, colIdx));
        String postCap = "";
        for (int i = endIdxRow + 1; i <= allFilledTo; i++) postCap += getLetter(GameBoard.getBoardGridContent(i, colIdx));

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String currentTilesWithPostCap = "";
            for (int j = 0; j <= allFilledTo - rowIdx; j++) {
                currentTilesWithPostCap += getLetter(GameBoard.getBoardGridContent(rowIdx + j, colIdx));
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
                useTilesGrids = getLetter(GameBoard.getBoardGridContent(endIdxRow - j, colIdx)) + useTilesGrids;
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


    public boolean isAnyRightAngleNewWord(String direction, List<Integer> tilesSetInto, int rowIdx, int colIdx) {

        for (int i : tilesSetInto) {
            int allFilledFrom = 1, allFilledTo = GameBoard.getSize() - 1;
            if (direction.equals("right")) {
                //check each col the tile is set in
                //first find out the consist word with the tile
                for (int up = rowIdx - 1; up >= 1; up--) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(up, i));
                    if (letter == null) {
                        allFilledFrom = up + 1;
                        break;
                    } else {
                        allFilledFrom = up;
                    }
                }
                for (int down = rowIdx + 1; down <= GameBoard.getSize(); down++) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(down, i));
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
                        curTry += getLetter(GameBoard.getBoardGridContent(b, i));
                    }
                    if (rowIdx == allFilledTo) {
                        // there is not post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = rowIdx + 1; c <= allFilledTo; c++) {
                            curTry += getLetter(GameBoard.getBoardGridContent(c, i));
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            } else {
                //check each row the tile is set in
                //first find out the consist word with the tile
                for (int left = colIdx - 1; left >= 0; left--) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(i, left));
                    if (letter == null) {
                        allFilledFrom = left + 1;
                        break;
                    } else {
                        allFilledFrom = left;
                    }
                }
                for (int right = colIdx + 1; right < GameBoard.getSize(); right++) {
                    Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(i, right));
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
                        curTry += getLetter(GameBoard.getBoardGridContent(i, b));
                    }
                    if (colIdx == allFilledTo) {
                        // there is not post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = colIdx + 1; c <= allFilledTo; c++) {
                            curTry += getLetter(GameBoard.getBoardGridContent(i, c));
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public boolean isRightAngleExistWordNoOverlap(String direction, int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean rightAngleNoOverlap = false;
       for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            int startRow = idxes.get(0);
            int startCol = idxes.get(1);
            int endRow = idxes.get(2);
            int endCol = idxes.get(3);

            if (direction.equals("right")) {
                // if the existing word is in same direction, continue
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


    public boolean isNextToParallelPlayedWord(String direction,
                                                     int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean nextTo = false;

        for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            int startRow = idxes.get(0);
            int startCol = idxes.get(1);
            int endRow = idxes.get(2);
            int endCol = idxes.get(3);

            if (direction.equals("right")) {
                // if the existing word is not in same direction, continue
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


    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
