package pij.main.GameSettings;
import java.io.*;
import static java.util.Arrays.copyOf;

public class BoardFile {

    private final int size;
    private String[][] boardArr;
    private final File f;

    public BoardFile(String userFilePath) {
        this.f = new File(userFilePath);
        this.size = setSize();
        setArr();
        //printBoard();
    }

    public int setSize() {
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
            return size;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int getSize() {
        return this.size;
    }

    /**
     *
     */
    public void setArr() {
        boardArr = new String[size+1][size];
        boardArr[0][0] = String.valueOf(size);

        int c = 0; int row = 0; int col = 0;
        try {
            FileReader fr = new FileReader(f);
            try (BufferedReader br = new BufferedReader(fr)) {
                while ( (c = br.read()) != -1 ) {
                    // If it is not the first row which shows the size number of the board
                    if (row != 0) {
                        // When it meets a dot
                        if (Character.compare((char) c, '.') == 0) {
                            boardArr[row][col] = ".";
                        }

                        // When it meets a Premium Word Square
                        if (Character.compare((char) c, '{') == 0) {
                            boardArr[row][col] = "{";
                            while (Character.compare((char)(c = br.read()), '}') != 0) {
                                boardArr[row][col] += (char) c;
                            }
                            boardArr[row][col] += "}";
                        }

                        // When it meets a Premium Letter Square
                        if (Character.compare((char) c, '(') == 0) {
                            boardArr[row][col] = "(";
                            while (Character.compare((char)(c = br.read()), ')') != 0) {
                                boardArr[row][col] += (char) c;
                            }
                            boardArr[row][col] += ")";
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

    /**
     * Copy boardArr
     */
    public void cpyArray(String[][] aDestination) {
        for (int i = 0; i <= size; i++) {
            aDestination[i] = copyOf(boardArr[i], boardArr[i].length);
            //System.out.println(aDestination[i]);
        }
    }
}
