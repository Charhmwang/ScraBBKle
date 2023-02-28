package pij.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordsOnBoard {

    // String is word, ArrayList [0] is "right" or "down", [1] is row, [2] is col of the starting grid
    private Map<String, ArrayList<Object>> words_on_board = new HashMap<>();

    private WordsOnBoard() {  }
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

    public boolean validateWord(String word, int row, char col, String direction) {

        // 1. is the starting position already having a letter
        if ( Character.isAlphabetic(GameBoard.getBoardGridContent(row, col).charAt(0)) ) return false;

        // First fill up the row/column (depends on the direction) skipping the grid already had a letter
        // var newCreatedWordUndone - The word composed of letters from the first tile to the end, maybe it is the new
        // word on its own, maybe it is the substring of the new word constructed with other next letters in the same row
        String newCreatedWordUndone = "";
        int bitCounter = 0;
        int gridCounter = 0;
        int rowIdx = row - 1;
        int colIdx = col - 1;
        int bound1 = row + gridCounter, bound2 = col + gridCounter;
        int[] tilesSetInto = new int[word.length()];

        while (bitCounter < word.length() && bound1 < words_on_board.size() && bound2 < words_on_board.size()) {
            if ( !Character.isAlphabetic(GameBoard.getBoardGridContent(rowIdx, colIdx).charAt(0)) ) {
                newCreatedWordUndone = newCreatedWordUndone.concat(String.valueOf(word.charAt(bitCounter)));
                tilesSetInto[bitCounter] = rowIdx + gridCounter;
                bitCounter++;
            } else {
                if (direction.equals("down")) {
                    newCreatedWordUndone = newCreatedWordUndone.
                            concat(String.valueOf(GameBoard.getBoardGridContent(rowIdx + gridCounter, colIdx).charAt(0)));
                } else {
                    newCreatedWordUndone = newCreatedWordUndone.
                            concat(String.valueOf(GameBoard.getBoardGridContent(rowIdx, colIdx + gridCounter).charAt(0));
                }
            }
            gridCounter++;
        }
        if (bound1 == words_on_board.size() || bound2 == words_on_board.size()) return false;  // out of legal bounds

        // 2. after going through with the supposed direction on the board using ALL the tiles word,
        // is there one and only one legal word constructed on the current row (if right)/ col (if down)?
        // if there are more than one, return false; if there is zero, return false;
        int endIdxRow = rowIdx + newCreatedWordUndone.length(), endIdxCol = colIdx + newCreatedWordUndone.length();
        // row - right
        int legalWordsCounter = 0;
        String newWord = "";
        int startFrom = 0, endAT = 0;
        if (direction.equals("right")) {
            for (int i = 0; i <= colIdx; i++) {
                String curTry = "";
                for (int j = endIdxCol; j < words_on_board.size(); j++) {
                    for (int k = i; k < colIdx; k++) {
                        char letter = GameBoard.getBoardGridContent(rowIdx, k).charAt(0);
                        if (!Character.isAlphabetic(letter)) curTry = "";
                        else curTry = curTry.concat(String.valueOf(letter));
                    }
                    curTry = curTry.concat(newCreatedWordUndone);
                    if (j != endIdxCol) {
                        char letter = GameBoard.getBoardGridContent(rowIdx, j).charAt(0);
                        if (!Character.isAlphabetic(letter)) break;
                        else curTry = curTry.concat(String.valueOf(letter));
                    }
                    if (WordList.validateWord(curTry)) {
                        legalWordsCounter++;
                        newWord = curTry;
                        startFrom = i;
                        endAT = j;
                    }
                    if (legalWordsCounter > 1) return false;
                }
            }
        } else { // col - down
            for (int i = 0; i <= rowIdx; i++) {
                String curTry = "";
                for (int j = endIdxRow; j < words_on_board.size(); j++) {
                    for (int k = i; k < rowIdx; k++) {
                        char letter = GameBoard.getBoardGridContent(k, colIdx).charAt(0);
                        if (!Character.isAlphabetic(letter)) curTry = "";
                        else curTry = curTry.concat(String.valueOf(letter));
                    }
                    curTry = curTry.concat(newCreatedWordUndone);
                    if (j != endIdxCol) {
                        char letter = GameBoard.getBoardGridContent(j, colIdx).charAt(0);
                        if (!Character.isAlphabetic(letter)) break;
                        else curTry = curTry.concat(String.valueOf(letter));
                    }
                    if (WordList.validateWord(curTry)) {
                        legalWordsCounter++;
                        newWord = curTry;
                        startFrom = i;
                        endAT = j;
                    }
                    if (legalWordsCounter > 1) return false;
                }
            }
        }
        if (legalWordsCounter == 0) return false;

        // THEN, check the cross direction each bit if there is any legal word constructed, return false;
        // is there any new legal word constructed on each tile's right angle direction because of this tile?
        // if yes, return false;
        if (direction.equals("right")) {
            for (int i : tilesSetInto) {
                //check each col the tile is set in
                //first find out the consist word with the tile
                int allFilledFrom = 0, allFilledTo = words_on_board.size() - 1;
                for (int up = rowIdx; up >= 0; up--) {
                    char letter = GameBoard.getBoardGridContent(up, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = up + 1;
                        break;
                    }
                }
                for (int down = rowIdx; down < words_on_board.size(); down++){
                    char letter = GameBoard.getBoardGridContent(down, i).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = down - 1;
                        break;
                    }
                }
                for (int a = allFilledFrom; a < rowIdx; a++) {
                    String curTry = "";
                    for (int b = rowIdx; b <= allFilledTo; b++) {
                        for (int c = a; c < b; c++) {
                            curTry = curTry.concat(String.valueOf(GameBoard.getBoardGridContent(c, i).charAt(0)));
                        }
                        curTry = curTry.concat(String.valueOf(GameBoard.getBoardGridContent(b, i).charAt(0)));
                        if (WordList.validateWord(curTry)) return false;
                    }
                }
            }
        } else {
            for (int i : tilesSetInto) {
                //check each row the tile is set in
                //first find out the consist word with the tile
                int allFilledFrom = 0, allFilledTo = words_on_board.size() - 1;
                for (int left = colIdx; left >= 0; left--) {
                    char letter = GameBoard.getBoardGridContent(i, left).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = left + 1;
                        break;
                    }
                }
                for (int right = rowIdx; right < words_on_board.size(); right++){
                    char letter = GameBoard.getBoardGridContent(i, right).charAt(0);
                    if (!Character.isAlphabetic(letter)) {
                        allFilledFrom = right - 1;
                        break;
                    }
                }
                for (int a = allFilledFrom; a < colIdx; a++) {
                    String curTry = "";
                    for (int b = colIdx; b <= allFilledTo; b++) {
                        for (int c = a; c < b; c++) {
                            curTry = curTry.concat(String.valueOf(GameBoard.getBoardGridContent(i, c).charAt(0)));
                        }
                        curTry = curTry.concat(String.valueOf(GameBoard.getBoardGridContent(i, b).charAt(0)));
                        if (WordList.validateWord(curTry)) return false;
                    }
                }
            }
        }   // Till here, can ensure that there is not more than one word constructed
            // due to the new adding tiles both horizontally and vertically.


        ArrayList<Object> list = words_on_board.get(newWord);  // if not null, will be used in the following if condition

        int exWord_rowStart = (char) list.get(1);
        int exWord_colStart = (char) list.get(2);
        int exWord_rowEnd = exWord_rowStart + newWord.length();
        int exWord_colEnd = exWord_colStart + newWord.length();

        if (words_on_board.containsKey(newWord)) {
            // 3. not allowed to place a word at right angles to a word already on the board without an overlap
            // If the existing word is at the right angle to the new created word
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

                if (!overlap) return false;
            } // 4. not allowed to place a complete word parallel immediately next to a word already played
            else {
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
                    if (nextTo) return false;
                }
            }
        }
        return true;
    }
}
