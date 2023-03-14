package pij.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

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

    private GameBoard() {}
    private File file;
    private static GameBoard gameBoardInstance;
    public synchronized static GameBoard getInstance() {
        if (gameBoardInstance == null) {
            gameBoardInstance = new GameBoard();
        }
        return gameBoardInstance;
    }

    /** The name of the GameBoard. Always non-null after object creation. */
    private static String[][] board;

    /** The size of a GameBoard. Must between (including) 12 and 26. */
    private static int size;

    private static List<Integer> CenterSquare;

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

        this.file = new File(userFilePath);

        // Now the valid board for the game is confirmed.
        // Pass the GameBoard object into SettingBoard to do all the initialisation.
        settingBoard();
    }

    // For JUnit tests
    public void chooseBoard(String userFilePath) {
        this.file = new File(userFilePath);
        settingBoard();
    }


    public void settingBoard() {
        setSize();
        setArr();
        setCenter();
    }

    /**
     * Read the size number from the first line of the designated file,
     * and set as the GameBoard object's size feature.
     *
     * @throws RuntimeException if accessing the file unsuccessfully
     * @throws NumberFormatException if reading the size number in the file unsuccessfully
     */
    public void setSize() {

        try (var sc = new Scanner(file, StandardCharsets.UTF_8)) {
            String s = sc.nextLine();
            size = Integer.parseInt(s);
        } catch(RuntimeException | IOException e){
            System.out.println("Error at reading board size from txt file");
            exit(1);
        }
    }


    public void setSize(int s) { size = s; }


    /**
     * Read the board each grid content from the designated file,
     * and set into the GameBoard object's board feature.
     *
     * @catch IOException if reading the file unsuccessfully
     * @catch FileNotFoundException if the file is not found
     */
    public void setArr() {

        board = new String[size+1][size];
        board[0][0] = String.valueOf(size);

        int c = 0, row = 0, col = 0;
        try {
            FileReader fr = new FileReader(file);
            try (BufferedReader br = new BufferedReader(fr)) {
                while ( (c = br.read()) != -1 ) {
                    // If it is not the first row which shows the size number of the board
                    if (row != 0) {
                        // When it meets a dot
                        if ((char) c == '.') {
                            board[row][col] = ".";
                        }

                        // When it meets a Premium Word Square
                        if ((char) c == '{') {
                            board[row][col] = "{";
                            while (Character.compare((char)(c = br.read()), '}') != 0) {
                                board[row][col] += (char) c;
                            }
                            board[row][col] += "}";
                        }

                        // When it meets a Premium Letter Square
                        if ((char) c == '(') {
                            GameBoard.board[row][col] = "(";
                            while ((char)(c = br.read()) != ')') {
                                board[row][col] += (char) c;
                            }
                            board[row][col] += ")";
                        }
                        col++;
                        // When it meets a \n
                        if ((char)c == '\n') {
                            row++;
                            col = 0;
                        }
                    }
                    // else it is the first row which shows the size number of the board
                    else {
                        while ( (char)(c = br.read()) != '\n') ;
                        row++;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error! File not found!");
            }
        } catch (IOException e) {
            System.out.println("An I/O Error Occurred");
        }
    }


    public void setCenter() {

        CenterSquare = new ArrayList<>();
        // If game board size is odd, there is only one center square, else they are four.
        List<Integer> centerIdx;
        if (size % 2 == 1) {
            centerIdx = List.of((size + 1) / 2, (size - 1) / 2);
        } else {
            centerIdx = List.of(size / 2, (size - 2) / 2);
        }
        CenterSquare = centerIdx;
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


    public static int getSize() { return size; }

    public static List<Integer> getCenterSquare() { return CenterSquare; }


    /**
     * Edit board square contents.
     *
     * @param row the targeting row
     * @param col the targeting column
     * @param letter the new content
     */
    public static void reviseBoard(int row, int col, String letter) { board[row][col] = letter; }

    public static boolean isGridRevised(int row, int col) {
        String gridContent = board[row][col];
        return (gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '(');
    }

    public static String getBoardGridContent(int row, int col) { return board[row][col]; }

}
