package pij.main;

import java.sql.SQLOutput;
import java.util.*;

public class ComputerAction {
    private final Player computer;
    private Move move;
    public Boolean skipped = false;

    public ComputerAction(Player computer) {
        this.computer = computer;
        move = autoMove();
        if (move == null) skipped = true;
        else move.execute();
    }

    public Move getMove() { return move; }

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
        // randomly choose how many tiles computer will use for this move
        int rackTilesNum = computer.getTileRack().getTiles().size();

        for (int i = 1; i <= GameBoard.size; i++) { //row
            for (int j = 0; j < GameBoard.size; j++) { //col
                if (Character.isAlphabetic(GameBoard.getBoardGridContent(i,j).charAt(0))) {
                    continue;  //already covered a tile
                }

                //========================================================================
                Random rd = new Random();
                // change 0->4 for debugging
                int useTilesAmount = rd.nextInt(0, rackTilesNum + 1); //if 0, pc skip for this move
                //int useTilesAmount = 7; //debug
                System.out.println("PC choosing " + useTilesAmount + " tiles for this move.");// debug

                System.out.println(computer.getTileRack());
                List<Tile> tiles = computer.getTileRack().getTiles();
                List<String> allTheLetterSequences = new ArrayList<>();

                switch (useTilesAmount) {
                    case 1 -> {
                        allTheLetterSequences = getSeqFor1Bit(tiles); break;
                    }
                    case 2 -> {
                        allTheLetterSequences = getSeqFor2Bits(tiles); break;
                    }
                    case 3 -> {
                        allTheLetterSequences = getSeqFor3Bits(tiles); break;
                    }
                    case 4 -> {
                        allTheLetterSequences = getSeqFor4Bits(tiles); break;
                    }
                    case 5 -> {
                        allTheLetterSequences = getSeqFor5Bits(tiles); break;
                    }
                    case 6 -> {
                        allTheLetterSequences = getSeqFor6Bits(tiles); break;
                    }
                    case 7 -> {
                        allTheLetterSequences = getSeqFor7Bits(tiles); break;
                    }
                    default -> {
                    }
                }

                // TODO: Don't give chance to computer to use 7 tiles for now - Brute force out of memory - fix later
               for (String s : allTheLetterSequences) {
                   String pos = "" + (char)('a' + j) + i;  // try each grid on the board to be the starting point
//                   System.out.println(s + " " + pos);  //debug
                   Move tryMoveRight = new Move(computer, s, pos, "r");
                   ifValidMove(tryMoveRight, validMoves);
                   Move tryMoveDown = new Move(computer, s, pos, "d");
                   ifValidMove(tryMoveDown, validMoves);
               }
            }
        }

        int validMovesAmount = validMoves.size();
        System.out.println("There are " + validMovesAmount + " valid moves");  //debug
        System.out.println("They are: "); //debug
        for(Move m : validMoves) {
            System.out.println(m); //debug
        }
        // if there was no valid move found, return null meaning skip
        if (validMovesAmount > 0) {
            Random rdm = new Random();
            int choose = rdm.nextInt(0, validMovesAmount);
            System.out.println(validMoves.get(choose)); //debug
            return validMoves.get(choose);
        } else
            return null;
    }

    public void ifValidMove(Move tryMove, List<Move> validMoves) {
        if (tryMove.isValid) {
            tryMove.recoverBoardGridContentForInvalidMove();
            //because it's need to be recovered as original for other potential possible moves validation
            validMoves.add(tryMove);
        } else {
            tryMove.recoverBoardGridContentForInvalidMove();
        }
    }

    public char randomChar() {
        Random random = new Random();
        int rd = random.nextInt(0,26);
        return (char)('a' + rd);
    }

    public List<String> getSeqFor1Bit(List<Tile> tiles) {
        List<String> res = new ArrayList<>();
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            res.add(each);
        }
        return res;
    };

    public List<String> getSeqFor2Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    res.add(each);
                    break;
                }
            }
        }
        return res;
    };

    public List<String> getSeqFor3Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard ? "" + randomChar() : "" + tiles.get(c).letter;
                            res.add(each);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return res;
    };

    public List<String> getSeqFor4Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard ? "" + randomChar() : "" + tiles.get(c).letter;
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard ? "" + randomChar() : "" + tiles.get(d).letter;
                                    res.add(each);
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
        return res;
    }

    public List<String> getSeqFor5Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard ? "" + randomChar() : "" + tiles.get(c).letter;
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard ? "" + randomChar() : "" + tiles.get(d).letter;
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard ? "" + randomChar() : "" + tiles.get(e).letter;
                                            res.add(each);
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
        return res;
    }

    public List<String> getSeqFor6Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard ? "" + randomChar() : "" + tiles.get(c).letter;
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard ? "" + randomChar() : "" + tiles.get(d).letter;
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard ? "" + randomChar() : "" + tiles.get(e).letter;
                                            for (int f = 0; f < 7; f++) {
                                                if (f != a && f != b && f != c && f != d && f != e) {
                                                    each += tiles.get(f).isWildCard ? "" + randomChar() : "" + tiles.get(f).letter;
                                                    res.add(each);
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
        return res;
    }

    public List<String> getSeqFor7Bits(List<Tile> tiles) {
        List<String> res = new ArrayList<>();

        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard ? "" + randomChar() : "" + tiles.get(a).letter;
            for (int b = 0; b < 7; b++) {
                if (b != a) {
                    each += tiles.get(b).isWildCard ? "" + randomChar() : "" + tiles.get(b).letter;
                    for (int c = 0; c < 7; c++) {
                        if (c != a && c != b) {
                            each += tiles.get(c).isWildCard ? "" + randomChar() : "" + tiles.get(c).letter;
                            for (int d = 0; d < 7; d++) {
                                if (d != a && d != b && d != c) {
                                    each += tiles.get(d).isWildCard ? "" + randomChar() : "" + tiles.get(d).letter;
                                    for (int e = 0; e < 7; e++) {
                                        if (e != a && e != b && e != c && e != d) {
                                            each += tiles.get(e).isWildCard ? "" + randomChar() : "" + tiles.get(e).letter;
                                            for (int f = 0; f < 7; f++) {
                                                if (f != a && f != b && f != c && f != d && f != e) {
                                                    each += tiles.get(f).isWildCard ? "" + randomChar() : "" + tiles.get(f).letter;
                                                    for (int g = 0; g < 7; g++) {
                                                        if (g != a && g != b && g != c && g != d && g != e && g != f) {
                                                            each += tiles.get(g).isWildCard ? "" + randomChar() : "" + tiles.get(g).letter;
                                                            res.add(each);
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
        return res;
    }

}
