package pij.main;

import java.io.*;
import java.lang.Integer;
import java.util.function.DoubleToIntFunction;

/**
 * Validate the user uploaded board file.
 *
 * @author Haomeng Wang
 * @version 1.0
 */

public class ValidateUserBoard {

    private FileInputStream boardFileInpStream;
    public ValidateUserBoard(FileInputStream fin) {
        this.boardFileInpStream = fin;
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
                    while ( (char)i != '\n') {
                        s += (char)i;
                    }
                    try {
                        number = Integer.parseInt(s);
                        if (number < 12 || number > 26) return false;
                        else {
                            i++;
                            continue;
                        }
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                }
                if (colCounter > number) return false; // Columns exceed the expecting scale.
                if (rowCounter > number) return false; // Rows exceed the expecting scale.
                // Read the board design.
                // When it meets Premium Word Square.
                if ( (char)i == '{' ) {
                    String s = "";
                    int n;
                    int counter = 1;
                    while ( (char)i != '}' && counter <= 3) {
                        s += (char)i;
                        i = boardFileInpStream.read();
                        counter++;
                    }
                    if ( (char)i == '}' ) {
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
                else if ( (char)i == '(' ) {
                    String s = "";
                    int n;
                    int counter = 1;
                    while ( (char)i != ')' && counter <= 3) {
                        s += (char)i;
                        i = boardFileInpStream.read();
                        counter++;
                    }
                    if ( (char)i == ')' ) {
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
                else if ((char)i == '.') ;
                else if ((char)i == '\n') {
                    // There are not supposed count of columns on this row.
                    if (colCounter != number) return false;
                    else {
                        colCounter = 0;
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

        if (rowCounter != number) return false;
        return true;
    }
}
