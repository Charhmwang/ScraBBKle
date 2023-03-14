package pij.main;

import java.util.*;

public class ComputerAction {
    private final Player computer;
    private final Move move;
    private final boolean firstMove;
    private final Boolean skipped;

    public ComputerAction(Player computer, boolean firstMove) {
        this.computer = computer;
        this.firstMove = firstMove;
        this.move = autoMove();
        if (move == null) skipped = true;
        else {
            skipped = false;
            // To recover the chosen move showing letters on the board leading the factor or a dot - for scoring
            int bitCounter = 0;
            for (int i : move.getTilesSetInto()) {
                if (move.getDirection().equals("right")) {
                    char curLetter = move.getInputLetters().charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(move.getRow(), i);
                    GameBoard.reviseBoard(move.getRow(), i, testContent);
                }
                if (move.getDirection().equals("down")) {
                    char curLetter = move.getInputLetters().charAt(bitCounter);
                    String testContent = curLetter + GameBoard.getBoardGridContent(i, move.getCol());
                    GameBoard.reviseBoard(i, move.getCol(), testContent);
                }
                bitCounter++;
            }
            //System.out.print("Computer choose the move: "); //debug
            //System.out.println("Computer built new word: " + move.madeNewWord); //debug
            //System.out.println(move); //debug
            //System.out.println("The chosen move is shown like this for scoring before recover to the normal form:");//debug
            //GameBoard.printBoard(); //debug
        }
    }

    public Move getMove() { return move; }


    public Boolean getSkipped() { return this.skipped; }

    public Move autoMove() {

        // Create a list to store the possible moves, choose one of them randomly as the return:

        // From the most up left corner grid on the board, choose each grid which has not been covered by tile as the start
        // position to try tiles, use all the tiles on the rack to build input as 1 bit or 2 bits or 3 bits etc until the
        // maximum size of the tiles amount on the rack (less than 7 if the rack cannot be fully filled up)
        // use different letter sequences as input letters, choose the current grid as position, and "r" or "d" as direction,
        // player as computer as 4 arguments to build a new move. Validate the move, if valid, add into the valid move list.

        // skip if there is not an invalid case found

        // Find one of these moves randomly (if hard mode can choose the highest score possible move)
        // Recover the content of the grid and execute to take tiles out and refill

        List<Move> validMoves = new ArrayList<>();

        for (int i = 1; i <= GameBoard.getSize(); i++) { //row
            for (int j = 0; j < GameBoard.getSize(); j++) { //col

                int rackTilesNum = computer.getTileRack().getTiles().size();
                String curGrid = GameBoard.getBoardGridContent(i,j);
                //System.out.println(curGrid);  //debug

                //If the current try grid already covered a tile, then no need to check
                if (Move.isGridCoveredByTile(curGrid) != null) continue;

//                Random rd = new Random();
//                int useTilesAmount = rd.nextInt(1, rackTilesNum + 1);

                //System.out.println("PC choosing " + useTilesAmount + " tiles for this move.");// debug

                //System.out.println("Computer tile rack:" + computer.getTileRack());  //debug
                List<Tile> tiles = computer.getTileRack().getTiles();
                List<String> allTheLetterSequences = new ArrayList<>();
                //System.out.println("rack tiles num: " + rackTilesNum);

                while (rackTilesNum-- > 0) {
                    //switch (useTilesAmount) {
                    switch (rackTilesNum) {
                        case 1 -> {
                            getSeqFor1Bit(tiles, allTheLetterSequences);
                            break;
                        }
                        case 2 -> {
                            getSeqFor2Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        case 3 -> {
                            getSeqFor3Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        case 4 -> {
                            getSeqFor4Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        case 5 -> {
                            getSeqFor5Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        case 6 -> {
                            getSeqFor6Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        case 7 -> {
                            getSeqFor7Bits(tiles, allTheLetterSequences);
                            break;
                        }
                        default -> {
                        }
                    }
                }

                for (String s : allTheLetterSequences) {
                    String pos = "" + (char)('a' + j) + i;  // try each grid on the board to be the starting point
                    //System.out.println(s + " " + pos);  //debug
                    if (this.firstMove) {
                        if (Move.coveredCenterSquares(s, pos, "r")) {
                            Move tryMoveRight = new Move(computer, true, s, pos, "r");
                            ifValidMove(tryMoveRight, validMoves);
                        }
                        if (Move.coveredCenterSquares(s, pos, "d")) {
                            Move tryMoveDown = new Move(computer, true, s, pos, "d");
                            ifValidMove(tryMoveDown, validMoves);
                        }
                    } else {
                        Move tryMoveRight = new Move(computer, false, s, pos, "r");
                        ifValidMove(tryMoveRight, validMoves);
                        Move tryMoveDown = new Move(computer, false, s, pos, "d");
                        ifValidMove(tryMoveDown, validMoves);
                    }
               }
            }
        }
        int validMovesAmount = validMoves.size();
        //System.out.println("There are " + validMovesAmount + " valid moves");  //debug
        //System.out.println("They are: "); //debug
//        for(Move m : validMoves) {
//            System.out.println(m); //debug
//        }
        // if there was no valid move found, return null meaning skip
        if (validMovesAmount > 0) {
            Random rdm = new Random();
            int choose = rdm.nextInt(0, validMovesAmount);
            //System.out.println(validMoves.get(choose)); //debug
            return validMoves.get(choose);
        } else
            return null;
    }

    public void ifValidMove(Move tryMove, List<Move> validMoves) {
        if (tryMove.getIsValid()) {
            tryMove.recoverBoardGridContentForInvalidMove();
            //because it's need to be recovered as original for other potential possible moves validation
            validMoves.add(tryMove);
        } else {
            if (tryMove.getTilesSetInto() != null)
                tryMove.recoverBoardGridContentForInvalidMove();
        }
    }

    public char randomChar() {
        Random random = new Random();
        int rd = random.nextInt(0,26);
        return (char)('a' + rd);
    }

    public void getSeqFor1Bit(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            allTheLetterSequences.add(each);    
        }
    }

    public void getSeqFor2Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    allTheLetterSequences.add(each);
                    break;
                }
            }
        }
    };

    public void getSeqFor3Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard() ? "" + randomChar() : "" + tiles.get(c).getLetter();
                            allTheLetterSequences.add(each);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    };

    public void getSeqFor4Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard() ? "" + randomChar() : "" + tiles.get(c).getLetter();
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard() ? "" + randomChar() : "" + tiles.get(d).getLetter();
                                    allTheLetterSequences.add(each);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void getSeqFor5Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard() ? "" + randomChar() : "" + tiles.get(c).getLetter();
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard() ? "" + randomChar() : "" + tiles.get(d).getLetter();
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard() ? "" + randomChar() : "" + tiles.get(e).getLetter();
                                            allTheLetterSequences.add(each);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void getSeqFor6Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard() ? "" + randomChar() : "" + tiles.get(c).getLetter();
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard() ? "" + randomChar() : "" + tiles.get(d).getLetter();
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard() ? "" + randomChar() : "" + tiles.get(e).getLetter();
                                            for (int f = 0; f < 7; f++) {
                                                if (f != a && f != b && f != c && f != d && f != e) {
                                                    each += tiles.get(f).isWildCard() ? "" + randomChar() : "" + tiles.get(f).getLetter();
                                                    allTheLetterSequences.add(each);
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void getSeqFor7Bits(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard() ? "" + randomChar() : "" + tiles.get(b).getLetter();
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard() ? "" + randomChar() : "" + tiles.get(c).getLetter();
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard() ? "" + randomChar() : "" + tiles.get(d).getLetter();
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard() ? "" + randomChar() : "" + tiles.get(e).getLetter();
                                            for (int f = 0; f < 7; f++) {
                                                if (f != a && f != b && f != c && f != d && f != e) {
                                                    each += tiles.get(f).isWildCard() ? "" + randomChar() : "" + tiles.get(f).getLetter();
                                                    for (int g = 0; g < 7; g++) {
                                                        if (g != a && g != b && g != c && g != d && g != e && g != f) {
                                                            each += tiles.get(g).isWildCard() ? "" + randomChar() : "" + tiles.get(g).getLetter();
                                                            allTheLetterSequences.add(each);
                                                            break;
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}
