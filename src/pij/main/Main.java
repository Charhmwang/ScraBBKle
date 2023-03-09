package pij.main;

import java.io.IOException;

//javac -cp junit-platform-console-standalone-1.9.1.jar:. pij/main/*.java
//java pij/main/*.java
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

        // Initiate LetterPoints, TileBag, WordList, WordsOnBoard
        LetterPoints letterPoints = new LetterPoints();
        TileBag tileBag = TileBag.getInstance();
        WordList wordList = new WordList();
        WordsOnBoard wordsOnBoard = WordsOnBoard.getInstance();

        // Set the player, tile rack into game class Scrabble
        play.startGame(human, computer, humanRack, letterPoints, tileBag, wordsOnBoard);

        // Start the game steps
        play.gameSteps();

    }
}
