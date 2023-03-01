package pij.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        GameBoard board = new GameBoard();
        ScraBBKle play = new ScraBBKle(board);

        Player human = new Player(true);
        Player computer = new Player(false);

        TileRack humanRack = new TileRack(human);
        human.setTileRack(humanRack);
        TileRack pcRack = new TileRack(computer);
        computer.setTileRack(pcRack);

        HumanAction hmAction = new HumanAction(human);

        play.startGame(human, computer, humanRack, pcRack, hmAction);

    }
}
