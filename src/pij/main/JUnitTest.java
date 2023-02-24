package pij.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTest {
    @Test
    void testValidateUserBoard1() {
        System.out.println("Test 1: test an invalid board - class ValidateUserBoard");
        String userFilePath = "./test_res/invalidBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = false;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be invalid.");
    }

    @Test
    void testValidateUserBoard2() {
        System.out.println("Test 2: test a valid board - class ValidateUserBoard");
        String userFilePath = "./resources/defaultBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = true;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be valid.");
    }

    @Test
    void testWord_in_WordList1() throws IOException {
        System.out.println("Test 3: find words in class WordList");
        WordList wl = new WordList();
        boolean expected = true; // existing
        Assertions.assertEquals(expected, wl.validateWord("academic"));
    }

    @Test
    void testWord_in_WordList2() throws IOException {
        System.out.println("Test 4: find words in class WordList");
        WordList wl = new WordList();
        boolean expected = false; // not existing
        Assertions.assertEquals(expected, wl.validateWord("zza"));
    }

    @Test
    void test_Tile_toString() {
        System.out.println("Test 5: test Tile class and method toString");
        Tile tile = new Tile('Q', 10);
        String expected = "[Q10]";
        String actual = tile.toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_TileBag() {
        System.out.println("Test 6: test TileBag class and method isEmpty");
        TileBag tb = new TileBag();
        boolean expected = false;
        boolean actual = tb.tilesInBag.isEmpty();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_TileBag_takeOutTile() {
        System.out.println("Test 7: test TileBag class and method takeOutTile");
        TileBag tb = new TileBag();
        Tile tile = new Tile('Q', 10);
        Tile getTile = tb.takeOutTile();
        Class<? extends Tile> actual = getTile.getClass();
        Class<? extends Tile> expected = tile.getClass();
        Assertions.assertEquals(expected, actual);
    }

    @Test
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
}
