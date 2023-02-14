package pij.main;


/**
 * Start the game process from here.
 *
 * @author Haomeng Wang
 * @version 1.1
 */

public class Game {
    private GameBoard board;
    private Player human;
    private Player computer;

    public Game(GameBoard board) {
        this.board = board;
    }

    public void startGame(Player human, Player computer) {
        this.human = human;
        this.computer = computer;
    }
}
