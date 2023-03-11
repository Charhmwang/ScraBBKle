package pij.main;

import java.util.List;

/**
 * Initialize the game board.
 * A GameBoard has a board in form of 2D string array, and a size(S x S).
 * Objects of this class are mutable: GameBoard has been created,
 * during the game process, players can change the board strings content
 * by placing the tiles they choose, size can be set by SettingBoard class.
 *
 * @author Haomeng Wang
 * @version 1.1
 */
public class GameBoard {

    /** The name of the GameBoard. Always non-null after object creation. */
    public static String[][] board;

    /** The size of a GameBoard. Must between (including) 12 and 26. */
    public static int size;

    public static List<Integer> CenterSquare;

    /**
     * Constructs a new GameBoard with no parameter.
     */
    public GameBoard() {
        this.chooseBoard();
    }

    /**
     * Create and initialize a two-dimensional array of string to represent the board for the game.
     * Prompt user input to choose using default board or a local file.
     *
     * For user input letter d, the default board will be read from the file ../resources/defaultBoard.txt.
     * For user input letter l, the program will ask user for the file name in form of the correct filepath.
     * If it is the user's own file, it will be validated whether it is syntactically correct as a specified form board.
     *
     * If the file is not valid, the program will ask the user to provide another file until successfully validated.
     * If it is the default file or a validated file, 2D string array board will be created and fulfilled the content
     * as the provided file.
     */
    public void chooseBoard() {

        // User input to make a choice using default board or uploading a board file
        boolean correctInput = false;
        String userFilePath = "";
        do {
            System.out.println("Would you like to _l_oad a board or use the _d_efault board?");
            System.out.print("Please enter your choice (l/d): ");
            String choice = System.console().readLine();

            // Use a default file.
            if (choice.equals("d")) {
                correctInput = true;
                userFilePath = "../resources/defaultBoard.txt";
            }
            // Load a file.
            else if (choice.equals("l")) {
                System.out.print("Please enter the file name of the board: ");
                userFilePath = System.console().readLine();
                ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
                if (!tester.test()) {
                    System.out.print("This is not a valid file. ");
                    continue;
                }
                correctInput = true;
            } else {
                System.out.println("Invalid choice! You must enter \"l\" or \"d\".");
            }
        } while (!correctInput);

        // Now the valid board for the game is confirmed.
        // Pass the GameBoard object into SettingBoard to do all the initialisation.

        SettingBoard settings = new SettingBoard(userFilePath);
    }

    /**
     * Print board with line tags and spaces.
     */
    public static void printBoard() {
        // Print the col numbers as the first line
        System.out.print("    ");
        char letter = 'a';
        for (int i = 1; i <= size; i++, letter++) {
            System.out.print(" ");
            System.out.print(letter);
            System.out.print("  ");
        }
        System.out.println();

        for (int i = 1; i <= size; i++) {
            // Each board line starts with the row number
            if (i < 10) System.out.print(" " + i + " ");
            else System.out.print(i + " ");

            for (int j = 0; j < size; j++) {
                // When it meets a dot
                if (board[i][j].equals("."))
                    System.out.print("  " + board[i][j] + " ");
                else if (board[i][j].startsWith("{") || board[i][j].startsWith("(")) {
                    if (board[i][j].length() > 3) System.out.print("" + board[i][j]);
                    else System.out.print(" " + board[i][j]);
                }
                else System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }


    /**
     * Edit board square contents.
     *
     * @param row the targeting row
     * @param col the targeting column
     * @param letter the new content
     */
    public static void reviseBoard(int row, int col, String letter) {

        board[row][col] = letter;
    }

    public static String getBoardGridContent(int row, int col) { return board[row][col]; }

    public String[][] getBoard() { return board; }

}
