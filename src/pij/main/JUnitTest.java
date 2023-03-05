package pij.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    @Test
    @Order(1)
    void testValidateUserBoard1() {
        System.out.println("Test 1: test an invalid board - class ValidateUserBoard");
        String userFilePath = "./test_res/invalidBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = false;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be invalid.");
    }

    @Test
    @Order(2)
    void testValidateUserBoard2() {
        System.out.println("Test 2: test a valid board - class ValidateUserBoard");
        String userFilePath = "./resources/defaultBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = true;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be valid.");
    }

    @Test
    @Order(3)
    void testWord_in_WordList1() throws IOException {
        System.out.println("Test 3: find words in class WordList");
        WordList wl = new WordList();
        boolean expected = true; // existing
        Assertions.assertEquals(expected, wl.validateWord("academic"));
    }

    @Test
    @Order(4)
    void testWord_in_WordList2() throws IOException {
        System.out.println("Test 4: find words in class WordList");
        WordList wl = new WordList();
        boolean expected = false; // not existing
        Assertions.assertEquals(expected, wl.validateWord("zza"));
    }

    @Test
    @Order(5)
    void test_Tile_toString() {
        System.out.println("Test 5: test Tile class and method toString");
        Tile tile = new Tile('Q', 10);
        String expected = "[Q10]";
        String actual = tile.toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(6)
    void test_TileBag() {
        System.out.println("Test 6: test TileBag class and method isEmpty");
        TileBag tb = TileBag.getInstance();
        boolean expected = false;
        boolean actual = tb.isEmpty();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    void test_TileBag_takeOutTile() {
        System.out.println("Test 7: test TileBag class and method takeOutTile");
        TileBag tb = TileBag.getInstance();
        Tile tile = new Tile('Q', 10);
        Tile getTile = tb.takeOutTile();
        Class<? extends Tile> actual = getTile.getClass();
        Class<? extends Tile> expected = tile.getClass();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(8)
    void test_Player_with_TileRack() {
        System.out.println("Test 8: test Player class and TileRack class");
        Player human = new Player(true);
        TileRack myRack = new TileRack(human);
        myRack.displayTiles();
        int expected = 7;
        int actual = myRack.getTilesAmount();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(9)
    void test_Player_with_TileRack_isTileExisting() {
        System.out.println("Test 9: test method takeOutTileFromRack in TileRack class");
        Player human = new Player(true);
        TileRack myRack = new TileRack(human);
        Tile tileG = myRack.isTileExisting('G');
        if (tileG != null) {
            char expected = 'G';
            char actual = tileG.letter;
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    @Order(10)
    void test_Move_HumanAction() throws IOException {
        System.out.println("Test 10: test Move class");
        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.printBoard();
        GameBoard.size = 15;
        WordList wordList = new WordList();
        Player human = new Player(true);
        TileRack myRack = new TileRack(human);
        myRack.displayTiles();
        List<Tile> tiles = myRack.getTiles();
        Random rand = new Random();

        // Take 3 random tiles away from rack but not wildcard until it can compose a legal word
        boolean found = false;
        String legalWord = "";
        while (!found) {
            Tile t1, t2, t3;
            boolean isWildCard;
            do {
                isWildCard = true;
                t1 = tiles.get(rand.nextInt(tiles.size()));
                if (t1.letter != '?') isWildCard = false;
            } while (isWildCard);

            boolean sameTile;
            do {
                isWildCard = true;
                sameTile = true;
                t2 = tiles.get(rand.nextInt(tiles.size()));
                if (t2.letter != '?') isWildCard = false;
                if (t2.hashCode() != t1.hashCode()) sameTile = false;
            } while (isWildCard || sameTile);

            do {
                isWildCard = true;
                sameTile = true;
                t3 = tiles.get(rand.nextInt(tiles.size()));
                if (t3.letter != '?') isWildCard = false;
                if (t3.hashCode() != t1.hashCode() && t3.hashCode() != t2.hashCode()) sameTile = false;
            } while (isWildCard || sameTile);

            String beforeShuffle = String.valueOf(t1.letter) + String.valueOf(t2.letter) + String.valueOf(t3.letter);
            List<String> characters = Arrays.asList(beforeShuffle.split(""));
            Collections.shuffle(characters);
            String afterShuffle = "";
            for (String character : characters)
                afterShuffle += character;

            if (WordList.validateWord(afterShuffle.toLowerCase())) {
                found = true;
                legalWord = afterShuffle;
            }
        }
        String position = "d5";
        String direction = "r";
        System.out.println(legalWord);
        Move move = new Move(human, legalWord, position, direction);
        System.out.println(move.toString());

        boolean expected = true;
        boolean actual = move.isValid;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(11)
    void test_Score_addScore() throws IOException {
        System.out.println("Test 11: test Scores class addScore method");

        GameBoard.size = 15;
        WordList wordList = new WordList();
        Player human = new Player(true);

        int expected = 10;
        human.addScore(10);
        int actual = human.getScore();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(12)
    void test_Score_toString() {
        System.out.println("Test 12: test Scores class toString method");
        Player human = new Player(true);
        String expected = "Human player score:\t" + 10;
        human.addScore(10);
        String actual = human.toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(13)
    void test_Scoring_calculateMoveScore() throws IOException {
        System.out.println("Test 13: test Scoring class calculateMoveScore method");
        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        LetterPoints letterPoints= new LetterPoints();
        GameBoard.size = 15;
        WordList wordList = new WordList();
        Player human = new Player(true);
        Move move = new Move(human, "GIT", "f8", "r");
        Scoring scoring = new Scoring(move, human);
        int expected = 8;
        int actual = scoring.getScore();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(14)
    void test_Move_recoverBoardGridContent() throws IOException {
        System.out.println("Test 14: test Scoring WordsOnBoard class multiWordsOrNoneRow method");

        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = new WordList();

        System.out.println("\n===Before the game===");
        GameBoard.printBoard();
        Player human = new Player(true);
        Move move = new Move(human, "GIT", "f8", "r");
        System.out.println("\n===After the move, before the recover===");
        GameBoard.printBoard();
        move.recoverBoardGridContent();
        System.out.println("\n===After the recover===");
        GameBoard.printBoard();

    }

    @Test
    @Order(15)
    void test_WordsOnBoard_multiWordsOrNoneRow_isAnyRightAngleNewWord() throws IOException {
        System.out.println("Test 15: test Scoring WordsOnBoard class multiWordsOrNoneRow method and isAnyRightAngleNewWord method");

        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        GameBoard.printBoard();
        WordList wordList = new WordList();
        Player human = new Player(true);
        Move move1 = new Move(human, "GIT", "f8", "r");
        move1.recoverBoardGridContent();
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, "SAR", "h7", "d");
        move2.recoverBoardGridContent();
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, "TG", "g9", "r");

        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(16)
    void test_WordsOnBoard_isRightAngleExistWordNoOverlap1() throws IOException {
        System.out.println("Test 16: test Scoring WordsOnBoard class isRightAngleExistWordNoOverlap method 1");

        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        GameBoard.printBoard();
        WordList wordList = new WordList();
        LetterPoints letterPoints = new LetterPoints();
        Player human = new Player(true);
        Move move1 = new Move(human, "GIT", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"GIT");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, "SAR", "h7", "d");
        move2.recoverBoardGridContent();
        WordsOnBoard.addWord(7, 5, 10, 5,"STAR");
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, "IS", "i9", "r");

        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(17)
    void test_WordsOnBoard_isRightAngleExistWordNoOverlap2() throws IOException {
        System.out.println("Test 17: test Scoring WordsOnBoard class isRightAngleExistWordNoOverlap method 2");

        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        GameBoard.printBoard();
        LetterPoints letterPoints = new LetterPoints();
        WordList wordList = new WordList();
        Player human = new Player(true);
        Move move1 = new Move(human, "GIT", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"GIT");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, "SAR", "h7", "d");
        move2.recoverBoardGridContent();
        WordsOnBoard.addWord(7, 5, 10, 5,"STAR");
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, "R", "i9", "r");
        System.out.println("\n=== After move3 and recovered ===");
        move3.recoverBoardGridContent();
        GameBoard.printBoard();

        boolean expected = true;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(18)
    void test_Move_recoverBoardGridContentForInvalidMove() throws IOException {
        System.out.println("Test 18: test Move class recoverBoardGridContentForInvalidMove method");

        SettingBoard s = new SettingBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        GameBoard.printBoard();
        WordList wordList = new WordList();
        LetterPoints letterPoints = new LetterPoints();
        Player human = new Player(true);
        Move move1 = new Move(human, "GIT", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"GIT");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, "SAR", "h7", "d");
        move2.recoverBoardGridContent();
        WordsOnBoard.addWord(7, 5, 10, 5,"STAR");
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, "TG", "g9", "r");
        System.out.println("\n=== After move3 and recovered ===");
        move3.recoverBoardGridContentForInvalidMove();
        GameBoard.printBoard();

        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }

}
