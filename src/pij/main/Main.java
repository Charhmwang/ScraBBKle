package pij.main;

import java.io.IOException;

/**
 * A shipment consists of 1 or more parcels.
 * A parcel consists of 1 or more Products.
 * Objects of this class can currently only store the contents of a Shipment,
 * provide a String representation, and allow limited access to information
 * about the contents.
 *
 * Further functionality may be provided in a later version of class Shipment.
 *
 * @author Carsten Fuhs
 * @version 0.1
 */
public class Main {
    public static void main(String[] args) throws IOException {

        // Initiate board and game
        GameBoard board = GameBoard.getInstance();
        board.chooseBoard();
        ScraBBKle play = ScraBBKle.getInstance();

        // Initiate players
        Player human = new Player(true);
        Player computer = new Player(false);

        // Initiate racks and set into class players
        TileRack humanRack = new TileRack();
        human.setTileRack(humanRack);
        TileRack pcRack = new TileRack();
        computer.setTileRack(pcRack);

        // Set the player, tile rack into game class Scrabble
        play.startGame(human, computer);

    }
}
