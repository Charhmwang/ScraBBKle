package pij.main;

import java.io.IOException;

/**
 * Represent the ScraBBKle game overall.
 * Initiate the game board.
 * Create two players with tile racks set in, and pass into the created ScraBBle
 * class instance to start the game.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) throws IOException {

        // Initiate board
        GameBoard board = GameBoard.getInstance();
        board.chooseBoard();

        // Initiate players
        Player human = new Player(true);
        Player computer = new Player(false);

        // Initiate racks and set into class players
        TileRack humanRack = new TileRack();
        human.setTileRack(humanRack);
        TileRack pcRack = new TileRack();
        computer.setTileRack(pcRack);

        // Set the players into game class Scrabble
        ScraBBKle scraBBKle = ScraBBKle.getInstance(human, computer);

    }
}
