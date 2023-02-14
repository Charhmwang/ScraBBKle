package pij.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        GameBoard board = new GameBoard();
        Game play = new Game(board);
        TileRack tileRack1 = new TileRack();
        TileRack tileRack2 = new TileRack();
        Player human = new Player(true, tileRack1);
        Player computer = new Player(false, tileRack2);
        play.startGame(human, computer);

    }
}
