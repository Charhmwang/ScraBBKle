package pij.main;


import java.util.*;

/**
 * WordsOnBoard class is a record of the existing words on the game board,
 * has a map attribute for recording. Singleton class.
 * Object of this class is immutable: after an object of class WordsOnBoard
 * has been created, one cannot change the values of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class WordsOnBoard {

    /** The map recording words on the board and the corresponding indexes, consist of the row and column
     * indexes of the word's first and last letter square.
     * For example, if the word "GIT" is located on the board from the square [2][3] to [2][5],
     * there is a key "git" and corresponding value is an integer list of (2,3,2,5).
     * Always non-null after object creation */
    public static Map<ArrayList<Integer>, String> words_on_board = new HashMap<>();

    /**
     * Constructs a WordsOnBoard instance.
     * Private constructor ensures instance can only be initiated inside the class.
     */
    private WordsOnBoard() {}

    /** WordsOnBoard instance, set as null initially.
     * Private to be hidden from outside the WordsOnBoard class.
     */
    private static WordsOnBoard instance;

    /**
     * For other classes getting the WordsOnBoard instance.
     * If the instance has never been created, initiate one then return.
     * If the instance has already been initiated, then return the created one.
     * Ensures the WordsOnBoard instance can be created once only in the program.
     *
     * @return the sole WordsOnBoard instance
     */
    public synchronized static WordsOnBoard getInstance() {
        if (instance == null) {
            instance = new WordsOnBoard();
        }
        return instance;
    }

    /**
     * Adds the word with the corresponding starting and ending position indexes as key-value
     * pair into the record map.
     *
     * @param startRow the row of the word's first letter square
     * @param startCol the column of the word's first letter square
     * @param endRow the row of the word's last letter square
     * @param endCol the column of the word's last letter square
     * @param word the new created adding word
     */
    public static void addWord(int startRow, int startCol, int endRow, int endCol, String word) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0, startRow);
        list.add(1, startCol);
        list.add(2, endRow);
        list.add(3, endCol);
        words_on_board.put(list, word.toLowerCase());
    }

}