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
    private File f;

    public ValidateUserBoard(String userFilePath) {
        this.f = new File(userFilePath);
    }

    public boolean test() {
        // If file not found
        if (f == null) {
            System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E1");
            return false;
        }

        int c = 0; int S = 0;
        int rowCounter = 0;
        int colCounter = 0;
        try {
            FileReader fr = new FileReader(this.f);
            try (BufferedReader br = new BufferedReader(fr)) {
                // Read the first line of the board file to recognize the board scale(S x S).
                String s = "";
                int bitCounter = 0;
                while ((c = br.read()) != -1 && Character.compare((char)c, '\n') != 0 && bitCounter < 3) {
                    s += (char)c;
                    bitCounter++;
                }
                if (bitCounter == 3) return false;

                try {
                    S = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E2.1");
                    return false;
                }
                if (!(S >= 12 && S <= 26)) {
                    System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E2.2");
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
                                if (counter > 3) {
                                    System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E3");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }

                            try {
                                n = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E4");
                                return false;
                            }

//                            if (!(n >= -9 && n <= 99)) {
//                                System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E4");
//                                return false;
//                            }
                        }
                        // Meet a Premium Letter Square
                        else if ((char)c == '(') {
                            s = "";
                            int n;
                            int counter = 0;

                            while ((char)(c = br.read()) != ')') {
                                // If the number inside brackets is more than 2 digits
                                if (counter > 3) {
                                    System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E5");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }
                            try {
                                n = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E6");
                                return false;
                            }
//                            if (!(n >= -9 && n <= 99)) {
//                                System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E6");
//                                return false;
//                            }
                        }
                        // Meet a dot
                        else if ((char)c == '.') ;
                            // else is some char not expected then return false
                        else {
                            System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E7");
                            return false;
                        }
                        colCounter++;
                        c = br.read();
                    }
                    if (colCounter != S) {
                        System.out.println("Invalid board! See test() in ValidateUserBoard class debug - E8");
                        return false;
                    }
                    colCounter = 0;
                    rowCounter++;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error! File not found!");
                return false;
            }
        } catch(IOException e) {
            System.out.println("An I/O Error Occurred");
            return false;
        }
        return (rowCounter == S);
    }
}
