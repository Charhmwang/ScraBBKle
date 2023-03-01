package pij.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

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
    void test_Player_with_TileRack_takeOutTile() {
        System.out.println("Test 9: test method takeOutTileFromRack in TileRack class");
        Player human = new Player(true);
        TileRack myRack = new TileRack(human);
        myRack.takeOutTileFromRack('G');
        if (myRack.isTileExisting('G') != null) {
            char expected = 'G';
            char actual = myRack.takeOutTileFromRack('G').letter;
            Assertions.assertEquals(expected, actual);
        } else {
            Tile expected = null;
            Tile actual = myRack.takeOutTileFromRack('G');
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    @Order(10)
    void test_Move_HumanAction() {
        System.out.println("Test 10: test Move class");
        GameBoard.size = 15;
        Player human = new Player(true);
        TileRack myRack = new TileRack(human);
        myRack.displayTiles();
        List<Tile> tiles = myRack.getTiles();
        Random rand = new Random();

        // Take 3 random tiles away from rack but not wildcard
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
            if (t2 != t1) sameTile = false;
        } while (isWildCard || sameTile);

        do {
            isWildCard = true;
            sameTile = true;
            t3 = tiles.get(rand.nextInt(tiles.size()));
            if (t3.letter != '?') isWildCard = false;
            if (t3 != t1 && t3 != t2) sameTile = false;
        } while (isWildCard || sameTile);

        String word = String.valueOf(t1.letter) + String.valueOf(t2.letter) + String.valueOf(t3.letter);
        String position = "d5";
        String direction = "r";
        Move move = new Move(human, word, position, direction);
        System.out.println(move.toString());

        boolean expected = true;
        boolean actual = move.isValid;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(11)
    void test_Score_addScore() {
        System.out.println("Test 11: test Scores class addScore method");
        Player human = new Player(true);
        Scoring humanScore = new Scoring(human);
        int expected = 10;
        humanScore.addScore(10);
        int actual = humanScore.getScore();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(12)
    void test_Score_toString() {
        System.out.println("Test 12: test Scores class toString method");
        Player human = new Player(true);
        Scoring humanScore = new Scoring(human);
        String expected = "Human player score:\t" + 10;
        humanScore.addScore(10);
        String actual = humanScore.toString();
        Assertions.assertEquals(expected, actual);
    }

}
