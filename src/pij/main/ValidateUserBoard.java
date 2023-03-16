package pij.main;


import java.io.*;
import java.lang.Integer;

/**
 * ValidateUserBoard class is to validate the user uploaded board file.
 * Object of this class are immutable: after an object of this class has been created,
 * one cannot change the value of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */

public class ValidateUserBoard {

    /** The File object of the uploaded board file. Always non-null after object creation. */
    private final File f;


    /**
     * Constructs a new board validation with the given user file path.
     * Assign File attribute value using the user file path name.
     *
     * @param userFilePath the board resource file path name
     */
    public ValidateUserBoard(String userFilePath) {
        this.f = new File(userFilePath);
    }


    /**
     * Returns a boolean result whether the user uploaded board file is valid.
     * If it is invalid, print an explanation on the console.
     *
     * @return a boolean result whether the user uploaded board file is valid
     */
    public boolean test() {
        int c, S;
        int rowCounter = 0;
        int colCounter = 0;
        try {
            FileReader fr = new FileReader(this.f);
            try (BufferedReader br = new BufferedReader(fr)) {
                // Read the first line of the board file to recognize the board scale(S x S).
                String s = "";
                int bitCounter = 0;
                while ((c = br.read()) != -1 && (char) c != '\n' && bitCounter < 3) {
                    s += (char)c;
                    bitCounter++;
                }
                if (bitCounter == 3) {
                    System.out.println("Invalid board! The first line of board file " +
                            "should be maximum 2 digits number: Error 0");
                    return false;
                }

                try {
                    S = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid board! The first line of board file should be an integer - Error 1");
                    return false;
                }
                if (!(S >= 12 && S <= 26)) {
                    System.out.println("Invalid board! The first line of board file should be " +
                            "the size number that value supposed between 12 and 26 (included): Error 2");
                    return false;
                }

                // Comes to the main board part
                // Read char by char until the end of file
                while ((c = br.read()) != -1) {
                    // Read char by char until a new line
                    while (c != -1 && (char)c != '\n') {
                        // Meet a Premium Word Square
                        if ((char)c == '{') {
                            s = "";
                            int n;
                            int counter = 0;

                            while ((char)(c = br.read()) != '}') {
                                // If the number inside brackets is more than 2 digits
                                if (counter > 1) {
                                    System.out.println("Invalid board! There is a factor number inside " +
                                            "one premium word square out of range between -9 and 99: Error 3");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }

                            try {
                                n = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid board! There is a factor inside of one premium word" +
                                        "square is not integer - Error 4");
                                return false;
                            }
                        }
                        // Meet a Premium Letter Square
                        else if ((char)c == '(') {
                            s = "";
                            int n;
                            int counter = 0;

                            while ((char)(c = br.read()) != ')') {
                                // If the number inside brackets is more than 2 digits
                                if (counter > 1) {
                                    System.out.println("Invalid board! There is a factor number inside " +
                                            "one premium letter square out of range between -9 and 99: Error 5");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }
                            try {
                                n = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid board! There is a factor inside of one premium letter" +
                                        "square is not integer - Error 6");
                                return false;
                            }
                        }
                        // Meet a dot
                        else if ((char)c == '.') ;
                            // else is some char not expected then return false
                        else {
                            System.out.println("Invalid board! Each line of the board should only have dot and" +
                                    " premium squares, not any other form - Error 7");
                            return false;
                        }
                        colCounter++;
                        c = br.read();
                    }
                    if (colCounter != S) {
                        System.out.println("Invalid board! Board size not equal to columns amount - Error 8");
                        return false;
                    }
                    colCounter = 0;
                    rowCounter++;
                }
                if (rowCounter != S) {
                    System.out.println("Invalid board! Board size not equal to rows amount - Error 9");
                    return false;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error! File not found!");
                return false;
            }
        } catch(IOException e) {
            System.out.println("An I/O Error Occurred");
            return false;
        }
        return true;
    }

}
