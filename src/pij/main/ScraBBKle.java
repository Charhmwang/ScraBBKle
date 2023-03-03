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
            boolean hmSkip = false, pcSkip = false;
            // The move will must get a valid one, unless the human admit to skip.
            if (hmMove.isValid) {
                // change the board content and remove tiles also refill the rack,
                // add scores to human, print out the move
                boolean isGameOver = hmMove.execute();
                if (isGameOver) break;
                System.out.println(hmMove);
            } else { //human skip
                // Computer's turn
                hmSkip = true;
            }
            //Move pcMove =
            // The move will must get a valid one, unless the pc choose to skip.
            // if pc skipped, set the boolean pcSkip to true, and check if both pc and human skipped, set boolean game over as true
            // else, execute pc's move and if pc's execute is not valid means game over due to tiles bag empty, so break


        }
        // if game over, check bonus and calculate scores and display the winner

    }

}
