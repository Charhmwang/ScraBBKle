package pij.main;


/**
 * Start the game process from here.
 *
 * @author Haomeng Wang
 * @version 1.1
 */

public class ScraBBKle {
    private GameBoard board;
    private Player human;
    private Player computer;
    private TileRack humanRack;
    private TileRack pcRack;
    private HumanAction hmAction;
    public static boolean gameOver = false;


    public ScraBBKle(GameBoard board) {
        this.board = board;
    }

    public void startGame(Player human, Player computer, TileRack humanRack, TileRack pcRack, HumanAction hmAction) {
        this.human = human;
        this.computer = computer;
        this.humanRack = humanRack;
        this.pcRack = pcRack;
        this.hmAction = hmAction;

    }

    public void gameOperations() {


        // The game ends when the tile bag is empty and one of the player has an empty tile rack.
        // The game also ends if both players pass twice in a row.
        boolean gameOver = false;
        while (!gameOver) {

            // If it is human's turn, print board and current tiles from the rack, and prompt user to enter the move
            GameBoard.printBoard();
            humanRack.displayTiles();
            Move hmMove = hmAction.promptMove();
            boolean hmSkip = false;
            boolean pcSkip = false;

            // The move will must get a valid one, unless the human admit to skip.
            if (hmMove.isValid) {
                // Add scores to human, print out the move
                Scoring scoring = new Scoring(hmMove, human);
                human.addScore(scoring.calculateMoveScore());
                System.out.println(hmMove);
                boolean bothBagRackEmpty = hmMove.execute();
                hmMove.recoverBoardGridContent();
                System.out.println(human);
                System.out.println(computer);

                if (bothBagRackEmpty) break;
            } else { //human skip
                // Computer's turn
                hmSkip = true;

                //Move pcMove =
                // The move will must get a valid one, unless the pc choose to skip.
                //if pc also skips here, means game over, break
                //otherwise, execute pc's move and add the score for pc,
                // and print out the move to show human also both sides scores

                //Scoring scoring = new Scoring(pcMove, computer);
                //...

            }

            // if human skip boolean is true, means the else on top was executed,
            // so, if both skip values are true means game over -> break;
            // otherwise continue to the next for loop turn to ask human's move.

            // if human skip boolean is false, means this round pc not moved yet, so execute the following steps
            //Move pcMove =
            // The move will must get a valid one, unless the pc choose to skip.
            //if pc skip, continue to the next for loop turn prompt human's move action
            //otherwise, execute pc's move and add the score for pc, and print out the move to show human
            // and print out the move to show human also both sides scores

            //Scoring scoring = new Scoring(pcMove, computer);
            //...

        }

        // Now game over, calculate scores and display the winner
        // use static removeScoresFromRemainedTiles(player) method in Scoring class

    }

}
