package pij.main;

import java.io.*;
import java.util.Scanner;

/**
 * Provide a board file. The first step of the ScraBBKle.
 *
 * @author Haomeng Wang
 * @version 1.0
 */

public class GameBoard {
    private FileInputStream fin;

    public GameBoard(){
        this.fin = this.chooseBoard();
    }

    /**
     * Returns a txt file of board for the game,
     * user's input to make a choice using default board or uploading a board file.
     *
     * For example, if the user inputs letter d, the default board will be loaded from the file defaultBoard.txt.
     * If the user inputs l, the program will ask for the file name.
     * Then the user uploaded file will be validated whether it is syntactically correct as specified.
     * If the file is not valid, the program will ask the user to provide another file.
     *
     * @return a valid board file chosen by the user
     */
    public FileInputStream chooseBoard() {

        // User input to make a choice using default board or uploading a board file
        boolean correctInput = false;
        FileInputStream fin;
        do {
            System.out.println("Would you like to _l_oad a board or use the _d_efault board?");
            System.out.print("Please enter your choice (l/d): ");
            String choice = System.console().readLine();

            if (choice == "l") {
                System.out.print("Please enter the file name of the board: ");
                String userFilePath = System.console().readLine();
                try {
                    fin = new FileInputStream(userFilePath);
                } catch (FileNotFoundException exc) {
                    System.out.println("File Not Found");
                    continue;
                }

                ValidateUserBoard boardTester = new ValidateUserBoard(fin);
                if (boardTester.test()) {
                    return fin;
                } else {
                    System.out.println("Invalid board file! Provide another one.");
                }
            } else if (choice == "d") {
                try {
                    fin = new FileInputStream("../../resources/defaultBoard.txt");
                } catch (FileNotFoundException exc) {
                    System.out.println("File Not Found");
                    continue;
                }
                return fin;
            } else {
                System.out.println("Invalid choice! You must enter \"l\" or \"d\".");
            }
        } while (!correctInput);

        return null;
    }

}
