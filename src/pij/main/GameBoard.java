package pij.main;

/**
 * Provide a board file. The first step of the ScraBBKle.
 *
 * @author Haomeng Wang
 * @version 1.0
 */

public class GameBoard {
    private String[][] board;

    private int size;

    public GameBoard() {
        this.chooseBoard();
    }

    /**
     * Returns a two dimensional arraylist of string to represent the board for the game,
     * user's input to make a choice using default board or uploading a board file.
     *
     * For example, if the user inputs letter d, the default board will be loaded from the file defaultBoard.txt.
     * If the user inputs l, the program will ask for the file name.
     * Then the user uploaded file will be validated whether it is syntactically correct as specified.
     * If the file is not valid, the program will ask the user to provide another file.
     *
     * @return a valid board from the file chosen by the user
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
            if (choice.compareTo("d") == 0) {
                correctInput = true;
                userFilePath = "../resources/defaultBoard.txt";
            }
            // Load a file.
            else if (choice.compareTo("l") == 0) {
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
        // Copy the board array from reading the txt file.

        BoardFile validBoard = new BoardFile(userFilePath);
        this.size = validBoard.getSize();
        this.board = new String[size+1][size];
        validBoard.cpyArray(this.board);
    }

    public void printBoard() {
        // Print the col numbers as the first line
        System.out.print("   ");
        char letter = 'a';
        for (int i = 1; i <= size; i++, letter++) {
            System.out.print(" ");
            System.out.print(letter);
            System.out.print(" ");
        }
        System.out.println();

        for (int i = 1; i <= size; i++) {
            // Each board line starts with the row number
            if (i < 10) System.out.print(" " + i + " ");
            else System.out.print(i + " ");

            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
}
