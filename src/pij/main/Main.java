package pij.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        // Initiate board and game
        GameBoard board = new GameBoard();
        ScraBBKle play = new ScraBBKle(board);

        // Initiate players
        Player human = new Player(true);
        Player computer = new Player(false);

        // Initiate racks and set into class players
        TileRack humanRack = new TileRack(human);
        human.setTileRack(humanRack);
        TileRack pcRack = new TileRack(computer);
        computer.setTileRack(pcRack);

        // Initiate human action for later use
        HumanAction hmAction = new HumanAction(human);

        // set the player, tile rack, human action etc. vars into game class Scrabble
        play.startGame(human, computer, humanRack, pcRack, hmAction);

        // start the game steps
        play.gameOperations();

    }
}
