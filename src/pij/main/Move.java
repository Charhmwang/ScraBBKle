package pij.main;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

import static pij.main.WordsOnBoard.words_on_board;

public class Move {
    public String inputLetters;
    public int row;
    public int col;
    public String position;
    public String direction;

    public Player player;
    public boolean isValid;
    public boolean isFirstStep;
    public List<Integer> tilesSetInto;
    public String madeNewWord = "";
    public List<Integer> start_and_endPosOfNewWord = new ArrayList<>();

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

        this.row = Integer.parseInt(position.substring(1));
        //column = position.charAt(0);
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

        return coveringSquares.contains(GameBoard.CenterSquare);
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
            String letter_with_points = " " + String.valueOf(letter) + letterPoints + " "; //" g3 "

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
        for (int i = 0; i < tilesSetInto.size(); i++) {
            String gridContent = "";
            if (direction.equals("right")) {
                gridContent = GameBoard.getBoardGridContent(row, tilesSetInto.get(i));
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(row, tilesSetInto.get(i), gridContent);
            }
            else {
                gridContent = GameBoard.getBoardGridContent(tilesSetInto.get(i), col);
                gridContent = gridContent.substring(1);
                GameBoard.reviseBoard(tilesSetInto.get(i), col, gridContent);
            }
        }
    }


    public AbstractMap.SimpleEntry<List<Integer>, AbstractMap.SimpleEntry<String, List<Integer>>> validateWord(String inputLetters, int row, int col, String direction) {

        // rowIdx stays the same as arg row because the game board content at [0][0] is the size number,
        // not the first line of the board
        //int colIdx = col - 97;

        // CHECK 1. is the starting position already having a letter
        //boolean occupied = Character.isAlphabetic(GameBoard.getBoardGridContent(row, colIdx).charAt(0));
        boolean occupied = Character.isAlphabetic(GameBoard.getBoardGridContent(row, col).charAt(0));
        if (occupied) return null;
        //System.out.println("CHECK 0 done"); // debug


        // First fill up the row/column (depends on the direction) skipping the grid already had a letter
        // var newCreatedWordUndone - The word composed of letters from the first tile to the end, maybe it is the new
        // word on its own, maybe it is the substring of the new word constructed with other next letters in the same row/col
        //Pair<String, List<Integer>> word_and_idx = buildWordUsingTileLetters(inputLetters, row, colIdx, direction);
        //AbstractMap.SimpleEntry<String, List<Integer>> preword_and_idx = buildWordUsingTileLetters(inputLetters, row, colIdx, direction);

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

        //System.out.println(preWord);  //debug
        if (preWord == null) { // means user's input out of the board bound
            //System.out.println("out of bound");
            return forReturn;
        }
        //System.out.println("CHECK 1 done"); //debug


        // CHECK 2. is there one and only one legal word constructed on the current row (if right)/ col (if down)?
        // if there are more than one or zero, return false;
        int endIdxRow = row + preWord.length() - 1;
        //int endIdxCol = colIdx + preWord.length() - 1;
        int endIdxCol = col + preWord.length() - 1;

        //System.out.println(preWord);  //debug
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

        //System.out.println("CHECK 2 done"); //debug

        // Check 3. is there any new legal word constructed on each tile's right angle direction because of this tile?
        // if yes, return null;

        String newWord = (String)res.get(0);
        int startFrom = (int)res.get(1);
        int endAT = (int)res.get(2);
        List<Integer> idxOfNewWord = calNewWordIdx(row, col, startFrom, endAT, direction);


        //boolean rightAngleCheck = isAnyRightAngleNewWord(direction, tilesSetInto, row, colIdx);
        boolean rightAngleCheck = isAnyRightAngleNewWord(direction, tilesSetInto, row, col);
        if (rightAngleCheck) {
            return forReturn;
        }
        //System.out.println("CHECK 3 done"); //debug
        // Till here, can ensure that there is not more than one word constructed
        // due to the new adding tiles both horizontally and vertically.


        // CHECK 4. not allowed to place a word at right angles to a word already on the board without an overlap

        boolean rightAngleExistWordNoOverlap = isRightAngleExistWordNoOverlap(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (rightAngleExistWordNoOverlap) {
            return forReturn;
        }

        //System.out.println("CHECK 4 done"); //debug

        // CHECK 5. not allowed to place a complete word parallel immediately next to a word already played

        // If the board does not have the new created word played, then no need to check the following steps for parallel.
        forReturn.setValue(new AbstractMap.SimpleEntry<>(newWord, idxOfNewWord));

        //System.out.println(" new word : " + newWord);  //debug
        //for (int i : idxOfNewWord) System.out.println(i); //debug

        if (!words_on_board.containsValue(newWord.toLowerCase())) {
            return forReturn;
        }

        Map<String, ArrayList<ArrayList<Integer>>> reverseMap = new HashMap<>(
                words_on_board.entrySet().stream()
                        .collect(Collectors.groupingBy(Map.Entry::getValue)).values().stream()
                        .collect(Collectors.toMap(
                                item -> item.get(0).getValue(),
                                item -> new ArrayList<>(
                                        item.stream()
                                                .map(Map.Entry::getKey)
                                                .collect(Collectors.toList())
                                ))
                        ));
        // list of all the same existing word's indexes, for example, if there are 3 same word played on the board,
        // there are 3 items in the ArrayList: can be (3,2,3,5), (1,3,1,6), (5,7,8,7)
        ArrayList<ArrayList<Integer>> idxes = reverseMap.get(newWord);
        for (ArrayList<Integer> list : idxes) {

            int exWord_rowStart = list.get(0);
            int exWord_colStart = list.get(1);
            int exWord_rowEnd = list.get(2);
            int exWord_colEnd = list.get(3);

            boolean nextToParallelPlayedWord = isNextToParallelPlayedWord(direction,
                    idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3),
                    exWord_rowStart, exWord_colStart, exWord_rowEnd, exWord_colEnd);

            if (nextToParallelPlayedWord) {
                forReturn.setValue(new AbstractMap.SimpleEntry<>(null, null));
                return forReturn;
            }
        }
        return forReturn;
    }


    public static List<Integer> calNewWordIdx(int rowIdx, int colIdx, int startFrom, int endAT,
                                              String direction) {
        List<Integer> idxOfNewWord = new ArrayList<>();
        if (direction.equals("right")) {
            idxOfNewWord.add(rowIdx);
            //System.out.println("startRow: " + rowIdx);
            idxOfNewWord.add(startFrom);
            //System.out.println("startCol: " + startFrom);
            idxOfNewWord.add(rowIdx);
            //System.out.println("endRow: " + rowIdx);
            idxOfNewWord.add(endAT);
            //System.out.println("endCol: " + endAT);
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

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.size && bound2 < GameBoard.size) {
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

//        System.out.println("Create undone word case: tilesSetInto: ");    //debug
//        for (Integer i : tilesSetInto) System.out.print(i + " ");           //debug
//        System.out.println();                                                //debug
        return new AbstractMap.SimpleEntry<String, List<Integer>>(firstStepWord, tilesSetInto);
    }


    public AbstractMap.SimpleEntry<String, List<Integer>> buildWordUsingTileLetters(String inputLetters, int rowIdx, int colIdx, String direction) {

        //System.out.println("Try to build word with tiles"); //debug
        String newCreatedWordUndone = "";
        List<Integer> tilesSetInto = new ArrayList<>();
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.size && bound2 < GameBoard.size) {

            if (direction.equals("down")) {
                String gridContent = GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                Character gridTileLetter = isGridCoveredByTile(gridContent);
                if (gridTileLetter == null) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                    newCreatedWordUndone += curLetter;
                    GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                    //System.out.println(GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx));//debug
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
        if (bitCounter < inputLetters.length()) {
            return new AbstractMap.SimpleEntry<String, List<Integer>>(null, tilesSetInto);
        }

//        System.out.println("Create undone word case: tilesSetInto: ");    //debug
//        for (Integer i : tilesSetInto) System.out.print(i + " ");           //debug
//        System.out.println();                                                //debug
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
        boolean crossed = false;

        //System.out.println("allFilledFrom: " + allFilledFrom + "; allFilledTo: " + allFilledTo);// debug

        for (int left = colIdx - 1; left >= 0; left--) {
            Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, left));
            if (letter == null) {
                allFilledFrom = left + 1;
                break;
            }
        }

        for (int right = endIdxCol; right < GameBoard.size; right++) {
            Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, right));
            if (letter == null) {
                allFilledTo = right - 1;
                break;
            }
        }

        int legalWordsCounter = 0;
        int startFrom = 0, endAT = 0;
        String newWord1 = "";
        // Get the pre-cap string and post-cap string
        String preCap = "";
        for (int i = allFilledFrom; i < colIdx; i++) preCap += isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, i));
        //System.out.println("preCap: " + preCap); //debug
        String postCap = "";
        for (int i = endIdxCol + 1; i <= allFilledTo; i++) postCap += isGridCoveredByTile(GameBoard.getBoardGridContent(rowIdx, i));
        //System.out.println("postCap: " + postCap); //debug

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String useTilesGrids = "";
            for (int j = 0; j <= allFilledTo - colIdx; j++) {
                useTilesGrids += getLetter(GameBoard.getBoardGridContent(rowIdx, colIdx + j));
                // useTilesGrids += GameBoard.getBoardGridContent(rowIdx, colIdx + j).charAt(0);
                String outcome = currentPreCap + useTilesGrids;
                //System.out.println("Outcome: " + outcome); //debug
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    newWord1 = outcome;
                    startFrom = allFilledFrom + i;
                    endAT = colIdx + j;
                }
                if (legalWordsCounter > 1) {
                    //System.out.println("Flag 1");
                    return null;
                }
            }
        }

        String newWord2 = "";
        // Try post-cap plus each letter before using the last tiles
        for (int i = 0; i < postCap.length(); i++) {
            String currentPostCap = postCap.substring(0,postCap.length()-i);
            String useTilesGrids = "";
            for (int j = 0; j <= endIdxCol - allFilledFrom; j++) {
                useTilesGrids = getLetter(GameBoard.getBoardGridContent(rowIdx, endIdxCol - j)) + useTilesGrids;
                String outcome = useTilesGrids + currentPostCap;
                //System.out.println("Outcome: " + outcome); //debug
                if (WordList.validateWord(outcome.toLowerCase())) {
                    legalWordsCounter++;
                    newWord2 = outcome;
                    startFrom = endIdxCol - j;
                    endAT = allFilledTo - i;
                }
                if (legalWordsCounter > 1) {
                    //System.out.println("Flag 2");
                    return null;
                }
            }
        }
        // Till now, legalWordsCounter == 0 or 1
        // preword maybe including old tiles, maybe not. for example, A add to T can be a new word
        boolean preWordIsValid = false;
        if (WordList.validateWord(preWord.toLowerCase())) {
            legalWordsCounter++;
            preWordIsValid = true;
        }
        //System.out.println("line 312. preWord: " + preWord);  //debug
        if (legalWordsCounter == 0 || legalWordsCounter > 1) {
            //System.out.println("Flag 3");
            return null;
        }
        if (!preWordIsValid) {
            if (endAT < endIdxCol || startFrom > colIdx) {
                //System.out.println("Flag 4");
                return null;
            }
        }

        ArrayList<Object> res = new ArrayList<>();
        //System.out.println("newWord1: " + newWord1 + "; newWord2: " + newWord2 + "; preWord: " + preWord); //debug

        if (!newWord1.isEmpty()) {
            if (startFrom == colIdx) {
                res.add(newWord1); res.add(startFrom); res.add(endAT);
            } else {
                //System.out.println("Flag 5");
                return null;
            }
        } else if (!newWord2.isEmpty()) {
            if (startFrom == colIdx) {
                res.add(newWord2); res.add(startFrom); res.add(endAT);
            } else {
                //System.out.println("Flag 6");
                return null;
            }
        } else {
            //if preWord is including an old tile, then add; else return false
            if (preWord.length() > tilesSetInto.size()) {
                res.add(preWord);
                res.add(colIdx);
                res.add(endIdxCol);
            } else {
                //System.out.println("Flag 7");
                return null;
            }
        }
//        System.out.println("Legal word startFrom=" + startFrom + "; endAT=" + endAT);  //debug
//        System.out.println("Pre-word filledFrom: " + allFilledFrom + "; filledTo: " + allFilledTo);  //debug
//        System.out.println("newWord validated from row mul or none: " + newWord);  //debug
        return res;
    }


    public ArrayList<Object> multiWordsOrNoneCol(String preWord, int rowIdx, int colIdx, int endIdxRow, List<Integer> tilesSetInto) {

        //System.out.println("Coming into multiCol check"); //debug
        int allFilledFrom = rowIdx;
        int allFilledTo = endIdxRow;

        //System.out.println("allFilledFrom: " + allFilledFrom + "; allFilledTo: " + allFilledTo);// debug

        for (int up = rowIdx - 1; up >= 1; up--) {
            Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(up, colIdx));
            if (letter == null) {
                allFilledFrom = up + 1;
                break;
            }
        }

        for (int down = endIdxRow; down <= GameBoard.size; down++) {
            Character letter = isGridCoveredByTile(GameBoard.getBoardGridContent(down, colIdx));
            if (letter == null) {
                allFilledTo = down - 1;
                break;
            } else {
                allFilledTo = down;
            }
        }

        int legalWordsCounter = 0;
        String newWord1 = "";
        int startFrom = 0, endAT = 0;

        String preCap = "";
        for (int i = allFilledFrom; i < rowIdx; i++) preCap += isGridCoveredByTile(GameBoard.getBoardGridContent(i, colIdx));
        // System.out.println("preCap: " + preCap);  //debug
        String postCap = "";
        for (int i = endIdxRow + 1; i <= allFilledTo; i++) postCap += isGridCoveredByTile(GameBoard.getBoardGridContent(i, colIdx));
         //System.out.println("postCap: " + postCap);

        // Try pre-cap plus each letter after using tiles
        for (int i = 0; i < preCap.length(); i++) {
            String currentPreCap = preCap.substring(i);
            String currentTilesWithPostCap = "";
            for (int j = 0; j <= allFilledTo - rowIdx; j++) {
                currentTilesWithPostCap += getLetter(GameBoard.getBoardGridContent(rowIdx + j, colIdx));
                String outcome = currentPreCap + currentTilesWithPostCap;
                // System.out.println("Outcome: " + outcome); //debug
                if (WordList.validateWord(outcome.toLowerCase())) {
                    // System.out.println("Validated word " + outcome); // debug
                    legalWordsCounter++;
                    newWord1 = outcome;
                    startFrom = allFilledFrom + i;
                    endAT = rowIdx + j;
                }
                if (legalWordsCounter > 1) {
                    //System.out.println("Flag 1");
                    return null;
                }
            }
        }

        // Try post-cap plus each letter before using the last tiles
        String newWord2 = "";
        for (int i = 0; i < postCap.length(); i++) {
            String currentPostCap = postCap.substring(0, postCap.length() - i);
            String useTilesGrids = "";
            for (int j = 0; j <= endIdxRow - allFilledFrom; j++) {
                useTilesGrids = getLetter(GameBoard.getBoardGridContent(endIdxRow - j, colIdx)) + useTilesGrids;
                String outcome = useTilesGrids + currentPostCap;
                // System.out.println("Outcome: " + outcome); //debug
                if (WordList.validateWord(outcome.toLowerCase())) {
                    // System.out.println("Validated word " + outcome);
                    legalWordsCounter++;
                    newWord2 = outcome;
                    startFrom = endIdxRow - j;
                    endAT = allFilledTo - i;
                }
                if (legalWordsCounter > 1) {
                    //System.out.println("Flag 2");
                    return null;
                }
            }
        }

        boolean preWordIsValid = false;
        if (WordList.validateWord(preWord.toLowerCase())) {
            legalWordsCounter++;
            preWordIsValid = true;
        }
        //System.out.println(legalWordsCounter);
        if (legalWordsCounter == 0 || legalWordsCounter > 1) {
            //System.out.println("Flag 3");
            return null;
        }
        if (!preWordIsValid) {
            if (endAT < endIdxRow || startFrom > rowIdx) {
                //System.out.println("Flag 4");
                return null;
            }
        }

        ArrayList<Object> res = new ArrayList<>();
        //System.out.println("newWord1: " + newWord1 + "; newWord2: " + newWord2 + "; preWord: " + preWord); //debug
        if (!newWord1.isEmpty()) {
            if (startFrom == rowIdx) {
                res.add(newWord1); res.add(startFrom); res.add(endAT);
            } else {
                //System.out.println("Flag 5");
                return null;
            }
        } else if (!newWord2.isEmpty()) {
            if (startFrom == rowIdx) {
                res.add(newWord2); res.add(startFrom); res.add(endAT);
            } else {
                //System.out.println("Flag 6");
                return null;
            }
        } else {
            //if preWord is including an old tile, then add; else return false
            if (preWord.length() > tilesSetInto.size()) {
                res.add(preWord);
                res.add(rowIdx);
                res.add(endIdxRow);
            } else {
                //System.out.println("Flag 7");
                return null;
            }
        }
//        System.out.println("Legal word startFrom=" + startFrom + "; endAT=" + endAT);  //debug
//        System.out.println("Pre-word filledFrom: " + allFilledFrom + "; filledTo: " + allFilledTo);  //debug
//        System.out.println("newWord validated from col mul or none: " + newWord);  //debug
        return res;
    }


    public boolean isAnyRightAngleNewWord(String direction, List<Integer> tilesSetInto, int rowIdx, int colIdx) {

        for (int i : tilesSetInto) {
            //System.out.println(i); //debug
            int allFilledFrom = 1, allFilledTo = GameBoard.size - 1;
            if (direction.equals("right")) {
                //check each col the tile is set in
                //first find out the consist word with the tile
                for (int up = rowIdx - 1; up >= 1; up--) {
                    char letter = GameBoard.getBoardGridContent(up, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = up + 1;
                        break;
                    } else {
                        allFilledFrom = up;
                    }
                }
                for (int down = rowIdx + 1; down <= GameBoard.size; down++) {
                    char letter = GameBoard.getBoardGridContent(down, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
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
                        curTry += GameBoard.getBoardGridContent(b, i).charAt(0);
                    }
                    if (rowIdx == allFilledTo) {
                        // there is not post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = rowIdx + 1; c <= allFilledTo; c++) {
                            curTry += GameBoard.getBoardGridContent(c, i).charAt(0);
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            } else {
                //check each row the tile is set in
                //first find out the consist word with the tile
                for (int left = colIdx - 1; left >= 0; left--) {
                    char letter = GameBoard.getBoardGridContent(i, left).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = left + 1;
                        break;
                    } else {
                        allFilledFrom = left;
                    }
                }
                for (int right = colIdx + 1; right < GameBoard.size; right++) {
                    char letter = GameBoard.getBoardGridContent(i, right).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
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
                        curTry += GameBoard.getBoardGridContent(i, b).charAt(0);
                    }
                    if (colIdx == allFilledTo) {
                        // there is not post-cap on this line after tile
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
                    } else {
                        // combine each pre-cap with different length of post-cap
                        for (int c = colIdx + 1; c <= allFilledTo; c++) {
                            curTry += GameBoard.getBoardGridContent(i, c).charAt(0);
                            if (WordList.validateWord(curTry.toLowerCase())) return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    // if next to with no overlap, is like:
    //      if the new word going right: other words are :
    //1. on left or right vertical: (start col == nw start col-1 || start col == nw start col+1)
    //                       && (nw start row >= start row && nw start row <= end row)
    //2. on top or down vertical: (start row == nw start row+1 || end row == nw start row-1)
    //                       && (start col >= nw start col && start col <= nw end col)

    //      if the new word going down: other words are :
    //1. on left or right vertical: (end col == nw start col-1 || start col == nw start col+1)
    //                        && (nw start row <= start row && nw end row >= start row)
    //2. on top or down vertical: (start row == nw start row-1 || start row == nw end row+1)
    //                        && (start col >= nw start col && end col <= nw start col)
    public boolean isRightAngleExistWordNoOverlap(String direction, int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean rightAngleNoOverlap = false;
        for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            int startRow = idxes.get(0);
            int startCol = idxes.get(1);
            int endRow = idxes.get(2);
            int endCol = idxes.get(3);
            //System.out.println("Word " + words_on_board.get(idxes) + ": indexes: [" + startRow + "," + startCol +
            //        "], [" + endRow + "," + endCol + "].");

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


    // to have a new word parallel next to an existing word(same content word)
    //      if the new word going right: old words are :
    //1. on left or right parallel: (end col == nw start col-1 || start col == nw end col+1)
    //                       && (start row == nw start row && start row == end row)
    //2. on top or down parallel: (start row == nw start row+1 || start row == nw start row-1 && start row == end row)
    //                       && ((start col >= nw start col && start col <= nw end col) || (end col >= nw start col && end col <= nw end col))

    //      if the new word going down: old words are :
    //1. on top or down parallel: (end row == nw start row-1 || start row == nw end row+1)
    //                       && (start col == nw start col && start col == end col)
    //2. on left or right parallel: (start col == nw start col+1 || start col == nw start col-1 && start col == end col)
    //                       && ((start row >= nw start row && start row <= nw end row) || (end row >= nw start row && end row <= nw end row))

    public boolean isNextToParallelPlayedWord(String direction,
                                                     int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol,
                                                     int startRow, int startCol,
                                                     int endRow, int endCol){
        boolean nextTo = false;
        if (direction.equals("right")) {
            if (startRow != endRow) return false; //the existed word is not parallel with the new one

            if ( ((endCol == nwStartCol - 1 || startCol == nwEndCol + 1) && (startRow == nwStartRow))
                    || ((startRow == nwStartRow + 1 || startRow == nwStartRow - 1) &&
                    ((startCol >= nwStartCol && startCol <= nwEndCol) || (endCol >= nwStartCol && endCol <= nwEndCol))) )
                nextTo = true;
        }

        if (direction.equals("down")) {
            if (startRow == endRow) return false;

            if ( ((endRow == nwStartRow - 1 || startRow == nwEndRow + 1) && (startCol == nwStartCol))
                    || ((startCol == nwStartCol + 1 || startCol == nwStartCol - 1) &&
                    ((startRow >= nwStartRow && startRow <= nwEndRow) || (endRow >= nwStartRow && endRow <= nwEndRow))) ) {
                nextTo = true;
            }
        }
        return nextTo;
    }


    @Override
    public String toString() { return "The move is:     Word: "
            + inputLetters + " at position " + position + ", direction: " + direction; }

}
