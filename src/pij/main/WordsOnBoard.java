package pij.main;


import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class WordsOnBoard {

    // String is word, ArrayList [0]start row idx [1]start col idx, [2]end row idx, [3]end col idx
    private static Map<ArrayList<Integer>, String> words_on_board = new HashMap<>();
    // [8, 5, 8, 7] : "GIT" represents the word starts from index[8][5] ends at[8][7] on the board is GIT

    private WordsOnBoard() {}
    private static WordsOnBoard instance;

    public synchronized static WordsOnBoard getInstance() {
        if (instance == null) {
            instance = new WordsOnBoard();
        }
        return instance;
    }

    public static void addWord(int startRow, int startCol, int endRow, int endCol, String word) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0, startRow);
        list.add(1, startCol);
        list.add(2, endRow);
        list.add(3, endCol);
        words_on_board.put(list, word);
    }

    public static Pair<Pair<String, List<Integer>>, int[]> validateWord(String inputLetters, int row, char col, String direction) {
        //change return from boolean to pair <boolean, int[]>

        // rowIdx stays the same as arg row because the game board content at [0][0] is the size number,
        // not the first line of the board
        int colIdx = col - 97;

        // CHECK 1. is the starting position already having a letter
        boolean occupied = Character.isAlphabetic(GameBoard.getBoardGridContent(row, colIdx).charAt(0));
        if (occupied) return null;

        // First fill up the row/column (depends on the direction) skipping the grid already had a letter
        // var newCreatedWordUndone - The word composed of letters from the first tile to the end, maybe it is the new
        // word on its own, maybe it is the substring of the new word constructed with other next letters in the same row/col
        Pair<String, int[]> word_and_idx = buildWordUsingTileLetters(inputLetters, row, colIdx, direction);
        if (word_and_idx == null) return null;
        //System.out.println("CHECK 1 done"); //debug


        // CHECK 2. is there one and only one legal word constructed on the current row (if right)/ col (if down)?
        // if there are more than one or zero, return false;
        String preWord = word_and_idx.getKey();
        int[] tilesSetInto = word_and_idx.getValue();

        int endIdxRow = row + preWord.length() - 1;
        int endIdxCol = colIdx + preWord.length() - 1;

        ArrayList<Object> res = new ArrayList<>();
        if (direction.equals("right")) {
            //System.out.println(preWord + " " + row + " " + colIdx + " " + endIdxCol);//debug
            res = multiWordsOrNoneRow(preWord, row, colIdx, endIdxCol);
        }
        if (direction.equals("down")) {
            res = multiWordsOrNoneCol(preWord, row, colIdx, endIdxRow);
        }
        if (res == null) return null;
        //System.out.println("CHECK 2 done"); //debug

        // Check 3. is there any new legal word constructed on each tile's right angle direction because of this tile?
        // if yes, return null;

        String newWord = (String)res.get(0);
        int startFrom = (int)res.get(1);
        int endAT = (int)res.get(2);
        List<Integer> idxOfNewWord = calNewWordIdx(row, colIdx, startFrom, endAT, direction);

        boolean rightAngleCheck = isAnyRightAngleNewWord(direction, tilesSetInto, row, colIdx);
        if (rightAngleCheck) {
            return null;
        }
        //System.out.println("CHECK 3 done"); //debug
        // Till here, can ensure that there is not more than one word constructed
        // due to the new adding tiles both horizontally and vertically.


        // CHECK 4. not allowed to place a word at right angles to a word already on the board without an overlap

        boolean rightAngleExistWordNoOverlap = isRightAngleExistWordNoOverlap(direction, idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3));
        if (rightAngleExistWordNoOverlap) return null;

        //System.out.println("CHECK 4 done"); //debug

        // CHECK 5. not allowed to place a complete word parallel immediately next to a word already played

       // If the board does not have the new created word played, then no need to check the following steps for parallel.
        Pair<Pair<String, List<Integer>>, int[]> forReturn = new Pair<Pair<String, List<Integer>>, int[]>(new Pair<>(newWord, idxOfNewWord), tilesSetInto);
        if (!words_on_board.containsValue(newWord)) {
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
            ArrayList<ArrayList<Integer>> idxes = reverseMap.get(newWord);
            for (ArrayList<Integer> list : idxes) {

                int exWord_rowStart = list.get(0);
                int exWord_colStart = list.get(1);
                int exWord_rowEnd = list.get(2);
                int exWord_colEnd = list.get(3);

                boolean nextToParallelExistWord = isNextToParallelExistWord(direction,
                        idxOfNewWord.get(0), idxOfNewWord.get(1), idxOfNewWord.get(2), idxOfNewWord.get(3),
                        exWord_rowStart, exWord_colStart, exWord_rowEnd, exWord_colEnd);
                if (nextToParallelExistWord) return null;
            }

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

    public static Pair<String, int[]> buildWordUsingTileLetters(String inputLetters, int rowIdx, int colIdx, String direction) {
        String newCreatedWordUndone = "";
        int[] tilesSetInto = new int[inputLetters.length()];
        int bitCounter = 0;
        int gridCounter = 0;
        int bound1 = 0, bound2 = 0;

        while (bitCounter < inputLetters.length() && bound1 < GameBoard.size && bound2 < GameBoard.size) {

            if (direction.equals("down")) {
                char ch = GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx).charAt(0);
                if (!Character.isAlphabetic(ch)) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx);
                    newCreatedWordUndone += curLetter;
                    GameBoard.reviseBoard(rowIdx + gridCounter, colIdx, testContent);
                    tilesSetInto[bitCounter] = rowIdx + gridCounter;
                    //not score, only letter for testing
                    //if move is valid, board content needs to add the corresponding scores
                    //e.g. (3) is changed into G(3) at this stage, will be change into G2 if it's valid move when it's executed
                    //if move is invalid, board grid contents need to recover (cut the letter of the head)
                    //e.g. (3) is changed into G(3) at this stage, will be reset into (3)
                    bitCounter++;
                } else {
                    newCreatedWordUndone += ch;
                }
            }

            if (direction.equals("right")) {
                char ch = GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter).charAt(0);
                if (!Character.isAlphabetic(ch)) {
                    char curLetter = inputLetters.charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter);
                    int temp = colIdx + gridCounter;
                    newCreatedWordUndone += curLetter;
                    //same as above in "down" case
                    GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
                    tilesSetInto[bitCounter] = colIdx + gridCounter;
                    bitCounter++;
                } else {
                    newCreatedWordUndone += ch;
                }
            }
            gridCounter++;
            if (direction.equals("down")) bound1 = rowIdx + gridCounter;
            if (direction.equals("right")) bound2 = colIdx + gridCounter;
        }

        if (bitCounter < inputLetters.length()) {
            return null;  // out of legal bounds before using all the tiles
        }

        return new Pair<String, int[]>(newCreatedWordUndone, tilesSetInto);
    }


    public static ArrayList<Object> multiWordsOrNoneRow(String preWord, int rowIdx, int colIdx, int endIdxCol) {

        // 1. find out this row from where to where (index) having consistent letters connected with the current word
        // 2. then use this below way to check how many legal word it can construct including the tiles word as a part

        //check each col the tile is set in
        //first find out the consist word with the tile
        int allFilledFrom = 0;
        int allFilledTo = 0;
        for (int left = colIdx - 1; left >= 0; left--) {
            char letter = GameBoard.getBoardGridContent(rowIdx, left).charAt(0);
            if (!Character.isAlphabetic(letter)) {
                allFilledFrom = left + 1;
                break;
            }
        }

        for (int right = colIdx + preWord.length(); right < GameBoard.size; right++) {
            char letter = GameBoard.getBoardGridContent(rowIdx, right).charAt(0);
            if (!Character.isAlphabetic(letter)) {
                allFilledTo = right - 1;
                break;
            }
        }

        int legalWordsCounter = 0;
        String newWord = "";
        int startFrom = 0, endAT = 0;

        for (int i = allFilledFrom; i <= colIdx; i++ ){ //try each word with longer head letters including tiles
            for (int j = endIdxCol; j <= allFilledTo; j++) { //try each word with longer tail letters including tiles
                String curTry = "";
                for (int k = i; k <= j; k++) {  //make word composed of each bit letter
                    char letter = GameBoard.getBoardGridContent(rowIdx, k).charAt(0);
                    //need to set first for validate
                    //if not valid, reset, backtrace
                    curTry += letter;
                }
                //System.out.println(curTry); //debug
                if (WordList.validateWord(curTry.toLowerCase())) {
                    legalWordsCounter++;
                    newWord = curTry;
                    startFrom = i;
                    endAT = j;
                }
                //System.out.println(legalWordsCounter); //debug
                if (legalWordsCounter > 1) return null;
            }
        }
        ArrayList<Object> res = new ArrayList<>();
        res.add(newWord); res.add(startFrom); res.add(endAT);
        return legalWordsCounter == 0 ? null : res;
    }


    public static ArrayList<Object> multiWordsOrNoneCol(String preWord, int rowIdx, int colIdx, int endIdxRow) {

        int allFilledFrom = 0;
        int allFilledTo = 0;
        for (int up = rowIdx - 1; up >= 0; up--) {
            char letter = GameBoard.getBoardGridContent(up, colIdx).charAt(0);
            if (!Character.isAlphabetic(letter)) {
                allFilledFrom = up + 1;
                break;
            }
        }

        for (int down = rowIdx + preWord.length(); down < GameBoard.size; down++) {
            char letter = GameBoard.getBoardGridContent(down, colIdx).charAt(0);
            if (!Character.isAlphabetic(letter)) {
                allFilledTo = down - 1;
                break;
            }
        }

        int legalWordsCounter = 0;
        String newWord = "";
        int startFrom = 0, endAT = 0;

        for (int i = allFilledFrom; i <= rowIdx; i++ ){ //try each word with longer head letters including tiles

            for (int j = endIdxRow; j <= allFilledTo; j++) { //try each word with longer tail letters including tiles
                String curTry = "";
                for (int k = i; k <= j; k++) {  //make word composed of each bit letter
                    char letter = GameBoard.getBoardGridContent(k, colIdx).charAt(0);
                    //need to set first for validate
                    //if not valid, reset, backtrace
                    curTry += letter;
                }
                if (WordList.validateWord(curTry.toLowerCase())) {
                    legalWordsCounter++;
                    newWord = curTry;
                    startFrom = i;
                    endAT = j;
                }
                if (legalWordsCounter > 1) return null;
            }
        }
        ArrayList<Object> res = new ArrayList<>();
        res.add(newWord); res.add(startFrom); res.add(endAT);
        return legalWordsCounter == 0 ? null : res;
    }


    public static boolean isAnyRightAngleNewWord(String direction, int[] tilesSetInto, int rowIdx, int colIdx) {

        for (int i : tilesSetInto) {
            String curTry = "";
            int allFilledFrom = 0, allFilledTo = GameBoard.size - 1;
            if (direction.equals("right")) {
                //check each col the tile is set in
                //first find out the consist word with the tile
                for (int up = rowIdx - 1; up >= 1; up--) {
                    char letter = GameBoard.getBoardGridContent(up, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = up + 1;
                        break;
                    }
                }
                for (int down = rowIdx + 1; down <= GameBoard.size; down++) {
                    char letter = GameBoard.getBoardGridContent(down, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledTo = down - 1;
                        break;
                    }
                }

                for (int a = allFilledFrom; a <= rowIdx; a++) {
                    for (int b = rowIdx; b <= allFilledTo; b++) {
                        for (int c = a; c <= b; c++) {
                            curTry += (GameBoard.getBoardGridContent(c, i).charAt(0));
                        }
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
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
                    }
                }
                for (int right = colIdx + 1; right <= GameBoard.size; right++) {
                    char letter = GameBoard.getBoardGridContent(i, right).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledTo = right - 1;
                        break;
                    }
                }
                for (int a = allFilledFrom; a <= colIdx; a++) {
                    for (int b = colIdx; b <= allFilledTo; b++) {
                        for (int c = a; c <= b; c++) {
                            curTry += (GameBoard.getBoardGridContent(i, c).charAt(0));
                        }
                        if (WordList.validateWord(curTry.toLowerCase())) return true;
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
    public static boolean isRightAngleExistWordNoOverlap(String direction, int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol) {

        boolean rightAngleNoOverlap = false;
        int startRow = 0, startCol = 0, endRow = 0, endCol = 0;
        for (ArrayList<Integer> idxes : words_on_board.keySet()) {
            startRow = idxes.get(0);
            startCol = idxes.get(1);
            endRow = idxes.get(2);
            endCol = idxes.get(3);

            if (direction.equals("right")) {
                if ( ((startCol == nwStartCol - 1 || startCol == nwStartCol + 1) && (nwStartRow >= startRow && nwStartRow <= endRow))
                        || ((startRow == nwStartRow + 1 || endRow == nwStartRow - 1) && (startCol >= nwStartCol && startCol <= nwEndCol)) )
                    rightAngleNoOverlap = true;
            }
            if (direction.equals("down")) {
                if ( ((endCol == nwStartCol - 1 || startCol == nwStartCol + 1) && (nwStartRow <= startRow && nwEndRow >= startRow))
                        || ((startRow == nwStartRow - 1 || startRow == nwEndRow + 1) && (startCol >= nwStartCol && endCol <= nwStartCol)) )
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

    public static boolean isNextToParallelExistWord (String direction,
    int nwStartRow, int nwStartCol, int nwEndRow, int nwEndCol,
    int startRow, int startCol,
    int endRow, int endCol){
        boolean nextTo = false;
        if (direction.equals("right")) {
            if ( ((endCol == nwStartCol - 1 || startCol == nwEndCol + 1) && (startRow == nwStartRow && startRow == endRow))
                    || ((startRow == nwStartRow + 1 || startRow == nwStartRow - 1 && startRow == endRow) &&
                    ((startCol >= nwStartCol && startCol <= nwEndCol) || (endCol >= nwStartCol && endCol <= nwEndCol))) )
                nextTo = true;
        }
        if (direction.equals("down")) {
            if ( ((endRow == nwStartRow - 1 || startRow == nwEndRow + 1) && (startCol == nwStartCol && startCol == endCol))
                    || ((startCol == nwStartCol + 1 || startCol == nwStartCol - 1 && startCol == endCol) &&
                    ((startRow >= nwStartRow && startRow <= nwEndRow) || (endRow >= nwStartRow && endRow <= nwEndRow))) )
                nextTo = true;
        }
        return nextTo;
    }

}