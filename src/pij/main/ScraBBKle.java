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

        String[] hmInput= hmAction.promptMove();
        Move hmMove = new Move(human, hmInput[0], hmInput[1], hmInput[2]);
        boolean validMove = hmMove.validateMove();
        if (validMove) {
            // 1. take tiles out of human rack and refill
            // 2. revise board content
            // 3. give human score
        } else {
            // give a reminder, re-enter
        }


    }



}
