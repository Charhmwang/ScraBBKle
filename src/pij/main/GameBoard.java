package pij.main;
import java.io.*;
import java.util.ArrayList;

/**
 * Provide a board file. The first step of the ScraBBKle.
 *
 * @author Haomeng Wang
 * @version 1.0
 */


public class GameBoard {
    private String[][] board;

    public GameBoard() {
        this.board = chooseBoard();
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
     * @throws IOException
     */
    public String[][] chooseBoard() {

        // User input to make a choice using default board or uploading a board file
        boolean correctInput = false;
        FileInputStream finOfFile = null;
        String userFilePath = "";
        do {
            System.out.println("Would you like to _l_oad a board or use the _d_efault board?");
            System.out.print("Please enter your choice (l/d): ");
            String choice = System.console().readLine();

            // Load a file.
            if (choice.compareTo("l") == 0) {
                correctInput = true;
                System.out.print("Please enter the file name of the board: ");
                userFilePath = System.console().readLine();
            }
            // Use a default file.
            else if (choice.compareTo("d") == 0) {
                correctInput = true;
                userFilePath = "../defaultBoard.txt";
            } else {
                System.out.println("Invalid choice! You must enter \"l\" or \"d\".");
            }

            try {
                finOfFile = new FileInputStream(userFilePath);
            } catch (FileNotFoundException exc) {
                System.out.println("File Not Found");
                correctInput = false;
            }

            //if (choice.compareTo("l") == 0) {
            ValidateUserBoard tester = new ValidateUserBoard(finOfFile);
            if (!tester.test()) {
                System.out.println("Not a Valid board");
                correctInput = false;
            }
            else System.out.println("Valid board");
            //}
        } while (!correctInput);

        // Save the valid gameboard from the file into 2-D arraylist variable board
        // ...

        // Now the valid board for the game is confirmed.
        // Create an arraylist copy from the txt file and return to the constructor calling function.


        try {
            finOfFile = new FileInputStream(userFilePath);
        } catch (FileNotFoundException exc) {
            System.out.println("File Not Found");
            correctInput = false;
        }

        ValidateUserBoard tester = new ValidateUserBoard(finOfFile);
        int col = tester.getSize();
        int row = col + 1;
        System.out.println(col + row);
        // String[][] resArray = new String[col][row];
        // System.out.println("col: " + col + ", row: " + row);
        //System.out.println(userFilePath);
        return null;
    }

    /*
     * Print the two dimensional arraylist board.
     */
    public void printBoard() {

    }

}