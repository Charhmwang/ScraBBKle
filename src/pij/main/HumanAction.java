package pij.main;

/**
 * Subclass of Action. Represents human player's action of making one move.
 *
 * @author Haomeng
 * @version 1.0
 */
public class HumanAction extends Action{

    /**
     * Constructs a new HumanAction with player's role, and whether first move.
     * Assign values to the attributes move and skipped in base class Action.
     *
     * @param human the role of the Player; must not be null
     * @param firstMove whether the first move of game; must not be null
     */
    public HumanAction(Player human, boolean firstMove) {
        super(human, firstMove);
    }


    /**
     * Procedure of human player making a move decision and assign to attribute move.
     * If the player's input format is illegal, or using letters not from its own rack tiles,
     * or the created word violates the game rules, the move will be rejected and requests another input.
     * This continues until the user provides a valid file, or choose to skip.
     */
    @Override
    public void setMove() {
        String[] strArr;
        boolean valid = false;
        Move move = null;
        while (!valid) {
            System.out.println("Please enter your move with letter sequence, position, " +
                    "and direction (d for down, r for right) separated by commas." +
                    "\nEntering just two commas passes. " +
                    "(Type two commas as \",,\" to skip the move): ");
            String input = System.console().readLine();
            if (input.equals(",,")) break;

            if (input.chars().filter(ch -> ch == ',').count() == 2) {
                strArr = input.split(",");
                String letters = strArr[0], position = strArr[1], direction = strArr[2];

                // Check is it in the legal format
                if (!validateInputFormat(position, direction)) {
                    System.out.println("Wrong input format! This is not a valid move");
                    continue;
                }

                // Check is it using tiles from rack
                boolean tilesFromRack = TileRack.validateTilesFromRack(player.getTileRack(), letters);
                if (!tilesFromRack) {
                    System.out.println("You must use tiles from your own rack ONLY! This is not a valid move");
                    continue;
                }

                // Check is it the first move of game, if yes, the inputs must cover the center square
                if (firstMove) {
                    char col = (char) ('a' + GameBoard.getCenterSquare().get(1));
                    if (!Move.coveredCenterSquares(letters, position, direction)) {
                        System.out.println("First move must cover the center square - " +
                                col + GameBoard.getCenterSquare().get(0) + ". This is not a valid move");
                        continue;
                    }
                    move = new Move(player, true, letters, position, direction);
                } else {
                    move = new Move(player, false, letters, position, direction);
                }

                if (move.getIsValid()) valid = true;
                else {
                    // Reject invalid move and recover the board if and only if the board content has been revised
                    // while validating - See explanation in method buildWordUsingTileLetters in Move class
                    // For example recover the square content from "G{3}" into "{3}", or "T." into ".", etc.
                    System.out.println("This is not a valid move");
                    if (GameBoard.isSquareRevised(move.getRow(), move.getCol()))
                        move.recoverBoardSquareContentToInitial();
                }
            } else {
                System.out.println("Wrong input format! This is not a valid move");
            }
        }
        this.move = move;
    }


    /**
     * Validates the human player's input whether in the format of three elements after being split by a comma,
     * and each one is in the supposed format.
     *
     * @param position the position of the move target square; must not be null
     * @param direction the direction of reading the created word; must not be null
     * @return a boolean result whether the player's input format is legal
     */
    public boolean validateInputFormat(String position, String direction) {

        int size = GameBoard.getSize();
        if (position.length() >= 2 && position.length() <= 3 &&
                direction.length() == 1 && (direction.charAt(0) == 'r' || direction.charAt(0) == 'd')) {

            int row = 0;
            try {
                row = Integer.parseInt(position.substring(1));
            } catch (NumberFormatException e) {
                return false;
            }
            char col = position.charAt(0);
            return col >= 'a' && col < ('a' + size) && row >= 1 && row <= size;
        } else return false;
    }

}
