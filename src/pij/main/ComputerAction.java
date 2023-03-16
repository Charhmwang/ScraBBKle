package pij.main;

import java.util.*;

/**
 * Subclass of Action. Represents computer player's action of making one move.
 *
 * @author Haomeng
 * @version 1.0
 */
public class ComputerAction extends Action {

    /**
     * Constructs a new ComputerAction with player's role, and whether first move.
     * Assign values to the attributes move and skipped in base class Action.
     *
     * @param computer the role of the Player; must not be null
     * @param firstMove whether the first move of game; must not be null
     */
    public ComputerAction(Player computer, boolean firstMove) {
        super(computer, firstMove);
        if (!skipped) reviseBoardContentForTheChosenMove();
    }


    /**
     * To show letters on the board leading the factor or a dot,
     * based on the chosen move and the targeting squares' contents.
     * This change is made for scoring of the valid move.
     */
    private void reviseBoardContentForTheChosenMove() {
        int bitCounter = 0;
        for (int i : move.getTilesSetInto()) {
            if (move.getDirection().equals("right")) {
                char curLetter = move.getInputLetters().charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardSquareContent(move.getRow(), i);
                GameBoard.reviseBoard(move.getRow(), i, testContent);
            }
            if (move.getDirection().equals("down")) {
                char curLetter = move.getInputLetters().charAt(bitCounter);
                String testContent = curLetter + GameBoard.getBoardSquareContent(i, move.getCol());
                GameBoard.reviseBoard(i, move.getCol(), testContent);
            }
            bitCounter++;
        }
    }

    /**
     * Scanning each vacant square of the board as the position, try to add different amount of tiles,
     * and each with all possible sequences as moves to be validated. Add all the valid moves to a storing list,
     * randomly choose one as the return at the end;
     * if no valid move found, set move as null value.
     */
    @Override
    public void setMove() {
        List<Move> validMoves = new ArrayList<>();

        for (int i = 1; i <= GameBoard.getSize(); i++) { //row
            for (int j = 0; j < GameBoard.getSize(); j++) { //col

                int rackTilesNum = player.getTileRack().getTiles().size();
                String curGrid = GameBoard.getBoardSquareContent(i,j);

                //If the current square has already been covered a tile, go to the next one
                if (Move.isGridCoveredByTile(curGrid) != null) continue;
                List<Tile> tiles = player.getTileRack().getTiles();
                List<String> allTheLetterSequences = new ArrayList<>();

                while (rackTilesNum-- > 0) {
                    switch (rackTilesNum) {
                        case 1 -> getSeqFor1Bit(tiles, allTheLetterSequences);
                        case 2 -> getSeqFor2Bits(tiles, allTheLetterSequences);
                        case 3 -> getSeqFor3Bits(tiles, allTheLetterSequences);
                        case 4 -> getSeqFor4Bits(tiles, allTheLetterSequences);
                        case 5 -> getSeqFor5Bits(tiles, allTheLetterSequences);
                        case 6 -> getSeqFor6Bits(tiles, allTheLetterSequences);
                        case 7 -> getSeqFor7Bits(tiles, allTheLetterSequences);
                        default -> {}
                    }
                }

                for (String s : allTheLetterSequences) {
                    String pos = "" + (char)('a' + j) + i;  // try each vacant square as the starting position
                    if (this.firstMove) {
                        if (Move.coveredCenterSquares(s, pos, "r")) {
                            Move tryMoveRight = new Move(player, true, s, pos, "r");
                            ifValidMove(tryMoveRight, validMoves);
                        }
                        if (Move.coveredCenterSquares(s, pos, "d")) {
                            Move tryMoveDown = new Move(player, true, s, pos, "d");
                            ifValidMove(tryMoveDown, validMoves);
                        }
                    } else {
                        Move tryMoveRight = new Move(player, false, s, pos, "r");
                        ifValidMove(tryMoveRight, validMoves);
                        Move tryMoveDown = new Move(player, false, s, pos, "d");
                        ifValidMove(tryMoveDown, validMoves);
                    }
               }
            }
        }
        int validMovesAmount = validMoves.size();
        if (validMovesAmount > 0) {
            Random rdm = new Random();
            int choose = rdm.nextInt(0, validMovesAmount);
            move = validMoves.get(choose);
        } else move = null;
    }


    /**
     * After each move built, remove the board change for the next move try.
     * If it is a valid move, add to the list of storing valid moves.
     *
     * @param tryMove the currently trying move
     * @param validMoves list of valid moves
     */
    public void ifValidMove(Move tryMove, List<Move> validMoves) {
        tryMove.recoverBoardSquareContentToInitial();
        if (tryMove.getIsValid()) validMoves.add(tryMove);
    }


    /**
     * Chooses a random lowercase alphabet letter while using a wildcard.
     *
     * @return character of a random lowercase alphabet letter
     */
    public char randomChar() {
        Random random = new Random();
        int rd = random.nextInt(0,26);
        return (char)('a' + rd);
    }


    /**
     * Adds each possible string made of 1 tile from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
    public void getSeqFor1Bit(List<Tile> tiles, List<String> allTheLetterSequences) {
        for (int a = 0; a < 7; a++) {
            String each = tiles.get(a).isWildCard() ? "" + randomChar() : "" + tiles.get(a).getLetter();
            allTheLetterSequences.add(each);    
        }
    }


    /**
     * Adds each possible string made of 2 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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
    }


    /**
     * Adds each possible string made of 3 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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
    }


    /**
     * Adds each possible string made of 4 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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


    /**
     * Adds each possible string made of 5 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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


    /**
     * Adds each possible string made of 6 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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


    /**
     * Adds each possible string made of 7 tiles from tile rack to the list of storing strings of different sequences.
     *
     * @param tiles the tiles on rack currently
     * @param allTheLetterSequences list of saving all the possible built input strings
     */
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
