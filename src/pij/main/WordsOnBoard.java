package pij.main;


import java.util.*;


public class WordsOnBoard {

    // String is word, ArrayList [0]start row idx [1]start col idx, [2]end row idx, [3]end col idx
    public static Map<ArrayList<Integer>, String> words_on_board = new HashMap<>();
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
        words_on_board.put(list, word.toLowerCase());
    }

}