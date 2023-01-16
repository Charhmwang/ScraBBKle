package pij.main;
import java.io.*;
import java.lang.Integer;

/**
 * Validate the user uploaded board file.
 *
 * @author Haomeng Wang
 * @version 1.0
 */

public class ValidateUserBoard {
    private FileInputStream boardFileInpStream;

    public ValidateUserBoard(String userFilePath) {
        try {
            this.boardFileInpStream = new FileInputStream(userFilePath);
        } catch (FileNotFoundException exc) {
            System.out.println("File Not Found");
        }
    }

    public boolean test() {
        int colCounter = 0;
        int rowCounter = 0;
        int number = 0;
        try {
            int i = boardFileInpStream.read();
            while (i != -1) {
                // Read the first line of the board file to recognize the board scale(S x S).
                if (rowCounter == 0) {
                    String s = "";
                    while ( Character.compare( (char)i, '\n') != 0 ) {
                        s += (char)i;
                        i = boardFileInpStream.read();
                    }
                    try {
                        number = Integer.parseInt(s);
                        if (number < 12 || number > 26) return false;
                        else {
                            i = boardFileInpStream.read();
                            // [debug] // System.out.println((char)i);
                            rowCounter++;
                            continue;
                        }
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                }

                // Read the board design.
                if (colCounter > number) return false; // Columns exceed the expecting scale.
                if (rowCounter > number) return false; // Rows exceed the expecting scale.

                // When it meets Premium Word Square.
                if ( Character.compare((char)i, '{') == 0 ) {
                    String s = "";
                    int n;
                    int counter = 1;
                    i = boardFileInpStream.read();
                    while ( Character.compare( (char)i, '}') != 0 && counter < 3) {
                        s += (char)i;
                        i = boardFileInpStream.read();
                        counter++;
                    }
                    if ( Character.compare( (char)i, '}') == 0 ) {
                        try {
                            n = Integer.parseInt(s);
                        } catch (NumberFormatException exc) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                // When it meets Premium Letter Square.
                else if ( Character.compare((char)i, '(') == 0 ) {
                    String s = "";
                    int n;
                    int counter = 1;
                    i = boardFileInpStream.read();
                    while ( Character.compare( (char)i, ')') != 0 && counter < 3) {
                        s += (char)i;
                        i = boardFileInpStream.read();
                        counter++;
                    }
                    if ( Character.compare((char)i, ')') == 0 ) {
                        try {
                            n = Integer.parseInt(s);
                        } catch (NumberFormatException exc) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                // When it meets dot and enter key.
                else if (Character.compare((char)i, '.') == 0 ) ;
                else if (Character.compare((char)i, '\n') == 0 ) {
                    // There are not supposed count of columns on this row.
                    if (colCounter != number) return false;
                    else {
                        colCounter = -1;
                        rowCounter++;
                    }
                }
                // When it meets a character should not appear on the specialized board.
                else return false;
                colCounter++;
                i = boardFileInpStream.read();
            }
        } catch (IOException exc) {
            System.out.println("An I/O Error Occurred");
        }

        if (rowCounter != number+1) return false;
        else return true;
    }

}
