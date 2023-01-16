package pij.main;
import java.io.*;

public class BoardFile {

        private int size;
        private String[][] boardArr;
        private FileInputStream boardFileInpStream;

        public BoardFile(String userFilePath) {
            try {
                this.boardFileInpStream = new FileInputStream(userFilePath);
            } catch (FileNotFoundException exc) {
                System.out.println("File Not Found");
            }
            this.size = setSize();
            setArr();
            //printBoard();
        }

        public int setSize() {
            int size = 0;
            try {
                int i = boardFileInpStream.read();
                // Read the first line of the board file to recognize the board scale(S x S).
                String s = "";
                while (Character.compare((char) i, '\n') != 0) {
                    s += (char) i;
                    i = boardFileInpStream.read();
                }
                //debug- System.out.println(Integer.parseInt(s));
                return size = Integer.parseInt(s);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public int getSize() {
            return this.size;
        }

        public void setArr() {
            boardArr = new String[size][size + 1];
            boardArr[0][0] = String.valueOf(size);

            int rowCount = 0;
            int i; int row = 1; int col = 0;
            try {
                i = boardFileInpStream.read();
                while (i != -1) {
                    if (rowCount != 0) {
                        // When it meets a dot
                        if (Character.compare((char) i, '.') == 0)
                            boardArr[row][col] = ".";
                        // When it meets a Premium Word Square
                        if (Character.compare((char) i, '{') == 0) {
                            String s = "{";
                            do {
                                if (Character.compare((char) i, '{') != 0)
                                    s += (char) i;
                                try {
                                    i = boardFileInpStream.read();
                                } catch (IOException e) {
                                    System.out.println("An I/O Error Occurred");
                                }
                            } while (Character.compare((char) i, '}') != 0);
                            boardArr[row][col] = s + "}";
                        }
                        // When it meets a Premium Letter Square
                        if (Character.compare((char) i, '(') == 0) {
                            String s = "(";
                            do {
                                if ((Character.compare((char) i, '(') != 0)) {
                                    s += (char) i;
                                }
                                try {
                                    i = boardFileInpStream.read();
                                } catch (IOException e) {
                                    System.out.println("An I/O Error Occurred");
                                }
                            } while (Character.compare((char) i, ')') != 0);
                            boardArr[row][col] = s + ")";
                        }
                        col++;
                        // When it meets a \n
                        if (Character.compare((char) i, '\n') == 0) {
                            row++;
                            col = 0;
                        }
                    }
                    else row++;
                    try {
                        i = boardFileInpStream.read();
                    } catch (IOException e) {
                        System.out.println("An I/O Error Occurred");
                    }
                }
            } catch (IOException e) {
                System.out.println("An I/O Error Occurred");
            }
        }

        public void printBoard() {
            System.out.println(size);
            for (int i = 1; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.print(boardArr[i][j]);
                }
                System.out.println();
            }
        }

        public String[][] getArray() {
            return boardArr;
        }
}
