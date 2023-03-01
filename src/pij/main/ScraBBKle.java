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
        GameBoard.printBoard();
        humanRack.displayTiles();

        //The game ends when the tile bag is empty and one of the player has an empty tile rack.
        // The game also ends if both players pass twice in a row.
        Move hmMove= hmAction.promptMove(); //the move will must get a valid one, unless the human admit to finish game.
        boolean isGameOver = hmMove.execute();
        // if game over, calculate scores


    }



}
