package pij.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A SettingBoard has a size(S x S),  a boardArr in form of 2D string array, and
 * Objects of this class are immutable, created to read the designated file to set
 * the passed in object of GameBoard.
 *
 * @author Haomeng Wang
 * @version 1.1
 */
public class SettingBoard {

    /** The name of the File instance by converting the user given or default pathname string.
     * Always non-null after object creation. */
    private final File f;

    /**
     * Constructs a new SettingBoard with pathname and GameBoard object,
     * feed the board by contents in the userFilePath file.
     *
     * @param userFilePath the name of the board file; must not be null
     */
    public SettingBoard(String userFilePath) {
        this.f = new File(userFilePath);
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
        int size = 0;
        try {
            FileReader fr = new FileReader(f);
            try (BufferedReader br = new BufferedReader(fr)) {
                int c = 0;
                // Read the first line of the board file to recognize the board scale(S x S).
                String s = "";
                // Read char by char until the new line
                while (Character.compare((char)(c = br.read()), '\n') != 0) {
                    s += (char) c;
                }
                size = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        GameBoard.size = size;
    }

    /**
     * Read the board each grid content from the designated file,
     * and set into the GameBoard object's board feature.
     *
     * @catch IOException if reading the file unsuccessfully
     * @catch FileNotFoundException if the file is not found
     */
    public void setArr() {
        int size  = GameBoard.size;
        GameBoard.board = new String[size+1][size];
        GameBoard.board[0][0] = String.valueOf(size);

        int c = 0; int row = 0; int col = 0;
        try {
            FileReader fr = new FileReader(f);
            try (BufferedReader br = new BufferedReader(fr)) {
                while ( (c = br.read()) != -1 ) {
                    // If it is not the first row which shows the size number of the board
                    if (row != 0) {
                        // When it meets a dot
                        if (Character.compare((char) c, '.') == 0) {
                            GameBoard.board[row][col] = ".";
                        }

                        // When it meets a Premium Word Square
                        if (Character.compare((char) c, '{') == 0) {
                            GameBoard.board[row][col] = "{";
                            while (Character.compare((char)(c = br.read()), '}') != 0) {
                                GameBoard.board[row][col] += (char) c;
                            }
                            GameBoard.board[row][col] += "}";
                        }

                        // When it meets a Premium Letter Square
                        if (Character.compare((char) c, '(') == 0) {
                            GameBoard.board[row][col] = "(";
                            while (Character.compare((char)(c = br.read()), ')') != 0) {
                                GameBoard.board[row][col] += (char) c;
                            }
                            GameBoard.board[row][col] += ")";
                        }
                        col++;
                        // When it meets a \n
                        if (Character.compare((char) c, '\n') == 0) {
                            row++;
                            col = 0;
                        }
                    }
                    // else it is the first row which shows the size number of the board
                    else {
                        while (Character.compare((char)(c = br.read()), '\n') != 0) ;
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
        GameBoard.CenterSquares = new ArrayList<>();
        int s = GameBoard.size;
        // If game board size is odd, there is only one center square, else there are 4.
        if (s % 2 == 1) {
            List<Integer> centerIdx = List.of( (s + 1)/2, (s - 1)/2 );
            GameBoard.CenterSquares = List.of(centerIdx);
        } else {
            List<Integer> center1idx = List.of( s/2, (s - 2)/2 );
            List<Integer> center2idx = List.of( s/2, s/2 );
            List<Integer> center3idx = List.of( (s + 2)/2, (s - 2)/2 );
            List<Integer> center4idx = List.of( (s + 2)/2, s/2 );
            GameBoard.CenterSquares = List.of(center1idx, center2idx, center3idx, center4idx);
        }
    }
}
