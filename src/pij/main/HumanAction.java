package pij.main;

import java.util.ArrayList;
import java.util.List;

public class HumanAction {

    private final Player human;
    private Move move;
    private boolean firstMove;
    public Boolean skipped;


    public HumanAction(Player human, boolean firstMove) {
        this.human = human;
        this.firstMove = firstMove;
        this.move = promptMove();
        this.skipped = move == null;
    }


    public Move getMove() { return move; }

    public Move promptMove() {
        String[] strArr = new String[3];
        boolean rightForm = false;
        Move move = null;
        while (!rightForm) {
            System.out.println("Please enter your move with letter sequence, position, and direction (d for down," +
                    " r for right) separated by commas. Entering just two commas passes. (Type \'S\' to skip): ");
            String input = System.console().readLine();
            if (input.equals("S")) return null;

            if (input.chars().filter(ch -> ch == ',').count() == 2) {
                strArr = input.split(",");
                String letters = strArr[0], position = strArr[1], direction = strArr[2];

                // Check is it in the right form and set into position in the bound of board indexes
                if (!validInputForm(position, direction)) {
                    System.out.println("Wrong input form! This is not a valid move");
                    continue;
                }

                // Check is it the first move of game, if yes, the inputs must cover at least one center square
                if (firstMove) {
                    if (!coveredCenterSquares(letters, position, direction)) {
                        System.out.println("First move must a cover center square! This is not a valid move");
                        continue;
                    }
                    move = new Move(human, true, letters, position, direction);
                } else {
                    move = new Move(human, false, letters, position, direction);
                }

                if (move.isValid) {
                    rightForm = true;
                } else {
                    // Reject invalid move and recover the board content IF the board has been revised while validating,
                    // see explanation in method buildWordUsingTileLetters in WordsOnBoard class
                    // need to update the grid content from such as "G{3}" into "{3}" or "T." into "."
                    System.out.println("This is not a valid move");
                    // Check if the grid content was revised
                    String gridContent = GameBoard.getBoardGridContent(move.row, move.col);
                    if ((gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                            && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '('))
                     move.recoverBoardGridContentForInvalidMove();
                }
            } else {
                System.out.println("Wrong input form! This is not a valid move");
            }
        }
        return move;
    }

    // Validate player's input position and move direction
    public static boolean validInputForm(String position, String direction) {

        int size = GameBoard.size;
        if (position.length() >= 2 && position.length() <= 3 &&
                direction.length() == 1 && (direction.charAt(0) == 'r' || direction.charAt(0) == 'd')) {

            int row = 0;
            // Initiate then use try catch to prevent player enter in a wrong form
            // such like "8f" instead of the supposed form "f8"
            try {
                row = Integer.parseInt(position.substring(1));
            } catch (NumberFormatException e) {
                return false;
            }
            char col = position.charAt(0);
            return col >= 'a' && col < ('a' + size) && row >= 1 && row <= size;
        } else return false;
    }


    public static boolean coveredCenterSquares(String letters, String position, String direction) {
        List<List<Integer>> coveringSquares = new ArrayList<>();
        int row = Integer.parseInt(position.substring(1));
        int col = position.charAt(0) - 'a';
        if (direction.equals("r")) {
            for (int i = 0; i < letters.length(); i++) {
                List<Integer> currentSquareIdx = List.of(row, col + i);
                coveringSquares.add(currentSquareIdx);
            }
        } else {
            for (int i = 0; i < letters.length(); i++) {
                List<Integer> currentSquareIdx = List.of(row + i, col);
                coveringSquares.add(currentSquareIdx);
            }
        }

        for (List<Integer> center : GameBoard.CenterSquares) {
            if (coveringSquares.contains(center))
                return true;
        }
        return false;
    }

}
