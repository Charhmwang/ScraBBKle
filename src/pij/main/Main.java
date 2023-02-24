package pij.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        GameBoard board = new GameBoard();
        Game play = new Game(board);
        Player human = new Player(true);
        Player computer = new Player(false);
        TileRack tileRack1 = new TileRack(human);
        TileRack tileRack2 = new TileRack(computer);

        play.startGame(human, computer);

    }
}
