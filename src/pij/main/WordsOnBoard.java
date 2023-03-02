package pij.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;

public class WordsOnBoard {

    // String is word, ArrayList [0] is "right" or "down", [1] is row, [2] is col of the starting grid
    private static Map<String, ArrayList<Object>> words_on_board = new HashMap<>();

    private WordsOnBoard() {}
    private static WordsOnBoard instance;

    public synchronized static WordsOnBoard getInstance() {
        if (instance == null) {
            instance = new WordsOnBoard();
        }
        return instance;
    }

    public void addWord(String word, int row, char col, String direction) {  // !!row and col is the index
        ArrayList<Object> list = new ArrayList<>();
        list.add(0, direction);
        list.add(1, row - 1);
        list.add(2, col - 1);
        words_on_board.put(word, list);
    }

    public static Pair<Boolean, int[]> validateWord(String inputLetters, int row, char col, String direction) {
        //change return from boolean to pair <boolean, int[]>

        int rowIdx = row - 1;
        int colIdx = col - 97;

        // CHECK 1. is the starting position already having a letter
        boolean occupied = Character.isAlphabetic(GameBoard.getBoardGridContent(rowIdx, colIdx).charAt(0));
        if (occupied) return null;

        // First fill up the row/column (depends on the direction) skipping the grid already had a letter
        // var newCreatedWordUndone - The word composed of letters from the first tile to the end, maybe it is the new
        // word on its own, maybe it is the substring of the new word constructed with other next letters in the same row/col
        Pair<String, int[]> word_and_idx = buildWordUsingTileLetters(inputLetters, rowIdx, colIdx, direction);
        if (word_and_idx == null) return null;


        // CHECK 2. is there one and only one legal word constructed on the current row (if right)/ col (if down)?
        // if there are more than one or zero, return false;
        String preWord = word_and_idx.getKey();
        int[] tilesSetInto = word_and_idx.getValue();

        int endIdxRow = rowIdx + preWord.length() - 1;
        int endIdxCol = colIdx + preWord.length() - 1;

        ArrayList<Object> res = new ArrayList<>();
        if (direction.equals("right")) {
            res = multiWordsOrNoneRow(preWord, rowIdx, colIdx, endIdxCol);
        }
        if (direction.equals("down")) {
            res = multiWordsOrNoneCol(preWord, rowIdx, colIdx, endIdxRow);
        }
        if (res == null) return null;


        // Check 3. is there any new legal word constructed on each tile's right angle direction because of this tile?
        // if yes, return null;

        String newWord = (String)res.get(0);
        int startFrom = (int)res.get(1);
        int endAT = (int)res.get(2);

        boolean rightAngleCheck = isAnyRightAngleNewWord(direction, tilesSetInto, rowIdx, colIdx);
        if (rightAngleCheck) return null;
        // Till here, can ensure that there is not more than one word constructed
        // due to the new adding tiles both horizontally and vertically.


        // CHECK 4. not allowed to place a word at right angles to a word already on the board without an overlap
        // If the existing word is at the right angle to the new created word

        ArrayList<Object> list = words_on_board.get(newWord);  // if not null, will be used in the following if condition
        // if null, means the board now is empty, then no need to check the following steps
        if (list == null) return new Pair<Boolean, int[]>(true, tilesSetInto);

        int exWord_rowStart = (char) list.get(1);
        int exWord_colStart = (char) list.get(2);
        int exWord_rowEnd = exWord_rowStart + newWord.length();
        int exWord_colEnd = exWord_colStart + newWord.length();

        boolean rightAngleExistWordNoOverlap = isRightAngleExistWordNoOverlap(direction, list, startFrom, endAT,
                rowIdx, colIdx, exWord_rowStart, exWord_colStart, exWord_colEnd, exWord_rowEnd);
        if (rightAngleExistWordNoOverlap) return null;

        System.out.println("check 4 done");


        // CHECK 5. not allowed to place a complete word parallel immediately next to a word already played
        boolean nextToParallelExistWord = isNextToParallelExistWord(direction, list, startFrom, endAT,
                rowIdx, colIdx, exWord_rowStart, exWord_colStart, exWord_colEnd, exWord_rowEnd);
        if (nextToParallelExistWord) return null;

        return new Pair<Boolean, int[]>(true, tilesSetInto);
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
                    newCreatedWordUndone += curLetter;
                    //same as above in "down" case
                    GameBoard.reviseBoard(rowIdx, colIdx + gridCounter, testContent);  //not score, only letter for testing
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
        String curTry = "";
        for (int i : tilesSetInto) {
            int allFilledFrom = 0, allFilledTo = GameBoard.size - 1;
            if (direction.equals("right")) {
                //check each col the tile is set in
                //first find out the consist word with the tile
                for (int up = rowIdx - 1; up >= 0; up--) {
                    char letter = GameBoard.getBoardGridContent(up, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = up + 1;
                        break;
                    }
                }
                for (int down = rowIdx + 1; down < GameBoard.size; down++) {
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
                for (int right = colIdx + 1; right < GameBoard.size; right++) {
                    char letter = GameBoard.getBoardGridContent(i, right).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledTo = right - 1;
                        break;
                    }
                }
                for (int a = allFilledFrom; a <= rowIdx; a++) {
                    for (int b = rowIdx; b <= allFilledTo; b++) {
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


    public static boolean isRightAngleExistWordNoOverlap(String direction, ArrayList<Object> list,
                                                         int startFrom, int endAT, int rowIdx, int colIdx,
                                                         int exWord_rowStart, int exWord_colStart,
                                                         int exWord_colEnd, int exWord_rowEnd) {

        if ( ((String)list.get(0)).equals("right") && direction.equals("down") ||
                (((String)list.get(0)).equals("down") && direction.equals("right")) )  {
            boolean overlap = false;

            // If the new word vertical and has cross with the existing word which direction is right, it needs to
            // have col number in between the existing word's col limit,
            // and the existing word row number in between the new word's row limit.
            if (((String)list.get(0)).equals("right")) {
                if ((colIdx >= exWord_colStart && colIdx <= exWord_colEnd)
                        && (exWord_rowStart >= startFrom && exWord_rowStart <= endAT))
                    overlap = true;
            }
            // If the new word vertical and has cross with the existing word which direction is down, it needs to
            // have row number in between the existing word's row limit,
            // and the existing word col number in between the new word's col limit.
            else {
                if ((rowIdx >= exWord_rowStart && rowIdx <= exWord_rowEnd)
                        && (exWord_colStart >= startFrom && exWord_colStart <= endAT))
                    overlap = true;
            }

            if (!overlap) return true;
        }
        return false;
    }

    public static boolean isNextToParallelExistWord(String direction, ArrayList<Object> list,
                                                    int startFrom, int endAT, int rowIdx, int colIdx,
                                                    int exWord_rowStart, int exWord_colStart,
                                                    int exWord_colEnd, int exWord_rowEnd) {
        // to have a new word next to an existing word(same content word)
        // case: right
        // a) if the new word row is in the same row with the existing word: col start is one after the old word collimit
        // or the old word col start is one after the new word col limit
        // b) if the new word row is one above or below the existing word:
        // the new word starting col is between the old word col limit or the new word ending col is between that

        if ( ((String)list.get(0)).equals("right") && direction.equals("right") ||
                (((String)list.get(0)).equals("down") && direction.equals("down")) ) {
            boolean nextTo = false;

            // case: right
            if (((String)list.get(0)).equals("right")) {
                // same row
                if (rowIdx == (int)list.get(1)) {
                    if (startFrom == exWord_colEnd + 1 || endAT == exWord_colStart - 1)
                        nextTo = true;
                } else if ( rowIdx == (int)list.get(1) + 1 ||  rowIdx == (int)list.get(1) - 1) { // diff row that one row above or bellow
                    if ( (startFrom >= exWord_colStart && startFrom <= exWord_colEnd)
                            || (endAT >= exWord_colStart && endAT <= exWord_colEnd) )
                        nextTo = true;
                }
            }

            // case: down
            // 1. if the new word col is in the same col with the existing word: row start is one after the old word row limit
            // or the old word row start is one after the new word row limit
            // 2. if the new word col is one left or right beside the existing word:
            // the new word starting row is between the old word row limits or the new word ending row is between that

            else {
                // same column
                if (colIdx == (int)list.get(2)) {
                    if (startFrom == exWord_rowEnd + 1 || endAT == exWord_rowStart - 1)
                        nextTo = true;
                } else if (colIdx == (int)list.get(2) + 1 || colIdx == (int)list.get(2) - 1) { // diff column next to the existing word's collumn
                    if ( (startFrom >= exWord_rowStart && startFrom <= exWord_rowEnd)
                            || (endAT >= exWord_rowStart && endAT <= exWord_rowEnd) )
                        nextTo = true;
                }
            }
            if (nextTo) return true;
        }
        return false;
    }

}
