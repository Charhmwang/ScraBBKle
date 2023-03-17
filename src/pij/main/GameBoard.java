package pij.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Initialize the game board. Singleton class.
 * A GameBoard has a board in form of 2D string array, and a size(S x S).
 * Object of this class is mutable: After GameBoard has been created, during the game process
 * players can change the board strings content by placing the tiles they choose.
 * Size, board contents and the centre square are initialized in method settingBoard.
 *
 * @author Haomeng Wang
 * @version 1.2
 */
public class GameBoard {

    /** The original resource file of the game board. Value assigned in method chooseBoard. */
    private File file;

    /** GameBoard instance, set as null initially.
     * Private to be hidden from outside the GameBoard class.
     */
    private static GameBoard gameBoardInstance;

    /** The contents of the GameBoard. Always non-null after object creation. */
    private static String[][] board;

    /** The size of a GameBoard. Must between (including) 12 and 26. */
    private static int size;

    /** The centre square of board. List must include two elements, one is row index, the other is column. */
    private static List<Integer> CentreSquare;


    /**
     * Constructs a GameBoard instance.
     * Private constructor ensures instance can only be initiated inside the class.
     */
    private GameBoard() {}


    /**
     * For other classes getting the GameBoard instance.
     * If the instance has never been created, initiate one then return.
     * If the instance has already been initiated, then return the created one.
     * Ensures the GameBoard instance can be created once only in the program.
     *
     * @return the sole GameBoard instance; always non-null
     */
    public synchronized static GameBoard getInstance() {
        if (gameBoardInstance == null) {
            gameBoardInstance = new GameBoard();
        }
        return gameBoardInstance;
    }


    /**
     * Creates and initializes a two-dimensional array of string to represent the board for the game.
     * Prompt user input to choose using default board or a local file.
     *
     * For user input letter d, the default board will be read from the file ../resources/defaultBoard.txt.
     * For user input letter l, the program will ask user for the file name in form of the correct filepath.
     * If it is the user's own file, it will be validated whether it is syntactically correct as a specified form board.
     *
     * If the file is not valid or not found from the input filepath, the program will ask the user to provide
     * another file until successfully validated.
     * If it is the default file or a validated file, 2D string array board will be created and contents fulfilled
     * from the provided file.
     */
    public void chooseBoard() {

        // User input to make a choice using default board or uploading a board file
        boolean correctInput = false;
        String userFilePath = "";
        do {
            System.out.println("Would you like to _l_oad a board or use the _d_efault board?");
            System.out.print("Please enter your choice (l/d): ");
            String choice = System.console().readLine();

            // Use a default file
            if (choice.equals("d")) {
                correctInput = true;
                userFilePath = "../resources/defaultBoard.txt";
            }
            // Load a file
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

        // Now the valid board for the game is confirmed
        // Set the board contents, size and centre square
        settingBoard();
    }


    /**
     * Sets the size, squares contents and centre square of the game board.
     */
    public void settingBoard() {
        setSize();
        setArr();
        setCenter();
    }


    /**
     * Reads the size number from the first line of the designated file,
     * and set as the attribute size of this class.
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


    /**
     * Reads the board each square's contents from the designated file,
     * and set into the attribute board of this class.
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
                        // When it meets a new line
                        if ((char)c == '\n') {
                            row++;
                            col = 0;
                        }
                    }
                    // Else it is the first row which shows the size of the board
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


    /**
     * Sets the centre square for game board.
     */
    public void setCenter() {

        CentreSquare = new ArrayList<>();
        List<Integer> centerIdx;
        if (size % 2 == 1)
            centerIdx = List.of((size + 1) / 2, (size - 1) / 2);
        else
            centerIdx = List.of(size / 2, (size - 2) / 2);

        CentreSquare = centerIdx;
    }


    /**
     * Prints board with line tags and spaces.
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
     * Returns the size of the game board.
     *
     * @return size of the game board; always included between 12 and 26
     */
    public static int getSize() { return size; }


    /**
     * Returns a list of integers representing where the centre square locates.
     *
     * @return a list of integers containing two elements,
     * representing the row and column index of the centre square.
     */
    public static List<Integer> getCenterSquare() { return CentreSquare; }


    /**
     * Edits board square contents.
     *
     * @param row the targeting row; must include between 1 and board size
     * @param col the targeting column; must include between 0 and board size minus 1
     * @param letter the new content; must non-null and non-empty
     */
    public static void reviseBoard(int row, int col, String letter) { board[row][col] = letter; }


    /**
     * To check whether the specific square's initial contents has been revised.
     *
     * @param row row index of the square; must include between 1 and board size
     * @param col column index of the square; must include between 0 and board size minus 1
     * @return a boolean result whether the square's initial contents has been revised
     */
    public static boolean isSquareRevised(int row, int col) {
        String gridContent = board[row][col];
        return (gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '(');
    }


    /**
     * To get the specific square's contents on the board.
     *
     * @param row row index of the square; must include between 1 and board size
     * @param col column index of the square; must include between 0 and board size minus 1
     * @return the square's contents as a string; always non-null and non-empty
     */
    public static String getBoardSquareContent(int row, int col) { return board[row][col]; }


    /**
     * Overloads chooseBoard method for the JUnit tests.
     *
     * @param userFilePath filepath of the board resource file; must non-null
     */
    public void chooseBoard(String userFilePath) {
        this.file = new File(userFilePath);
        settingBoard();
    }


    /**
     * Overloads setSize method for the JUnit tests.
     *
     * @param s size of the game board; must include between 12 and 26
     */
    public void setSize(int s) { size = s; }

}
