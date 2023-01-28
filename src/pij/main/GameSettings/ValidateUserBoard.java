package pij.main.GameSettings;
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
            System.out.println("E1");
            return false;
        }

        int c = 0; int S = 0;
        int rowCounter = 0;
        int colCounter = 0;
        try {
            FileReader fr = new FileReader(f);
            try (BufferedReader br = new BufferedReader(fr)) {
                // Read the first line of the board file to recognize the board scale(S x S).
                String s = "";
                while ((c = br.read()) != -1 && Character.compare((char)c, '\n') != 0) {
                    s += (char)c;
                }
                S = Integer.parseInt(s);
                if (!(S >= 12 && S <= 26)) {
                    System.out.println("E2");
                    return false;
                }

                // Comes to the main board part
                // Read char by char until the end of file
                while ((c = br.read()) != -1) {
                    // Read char by char until a new line
                    while (c != -1 && Character.compare((char)c, '\n') != 0) {
                        // Meet a Premium Word Square
                        if ( Character.compare((char)c, '{') == 0 ) {
                            s = "";
                            int n;
                            int counter = 0;

                            while ( Character.compare( (char)(c = br.read()), '}') != 0) {
                                // If the number inside brackets is more than 2 digits
                                if (counter > 3) {
                                    System.out.println("E3");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }
                            n = Integer.parseInt(s);
                            if (!(n >= -9 && n <= 99)) {
                                System.out.println("E4");
                                return false;
                            }
                        }
                        // Meet a Premium Letter Square
                        else if ( Character.compare((char)c, '(') == 0 ) {
                            s = "";
                            int n;
                            int counter = 0;

                            while ( Character.compare( (char)(c = br.read()), ')') != 0) {
                                // If the number inside brackets is more than 2 digits
                                if (counter > 3) {
                                    System.out.println("E5");
                                    return false;
                                }

                                s += (char)c;
                                counter++;
                            }
                            n = Integer.parseInt(s);
                            if (!(n >= -9 && n <= 99)) {
                                System.out.println("E6");
                                return false;
                            }
                        }
                        // Meet a dot
                        else if (Character.compare((char)c, '.') == 0 ) ;
                            // else is some char not expected then return false
                        else {
                            System.out.println("E7");
                            return false;
                        }
                        colCounter++;
                        c = br.read();
                    }
                    if (colCounter != S) {
                        System.out.println("E8");
                        return false;
                    }
                    colCounter = 0;
                    rowCounter++;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error! File not found!");
            }
        } catch(IOException e) {
            System.out.println("An I/O Error Occurred");
        }
        return (rowCounter == S);
    }
}
