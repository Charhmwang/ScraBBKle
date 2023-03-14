package pij.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTest {

    @Test
    @Order(1)
    void testValidateUserBoard1() {
        System.out.println("Test 1: test an invalid board - class ValidateUserBoard");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;

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
        boolean expected = true; // existing
        Assertions.assertEquals(expected, WordList.validateWord("academic"));
    }

    @Test
    @Order(4)
    void testWord_in_WordList2() throws IOException {
        System.out.println("Test 4: find words in class WordList");
        boolean expected = false; // not existing
        Assertions.assertEquals(expected, WordList.validateWord("zza"));
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
        boolean expected = false;
        boolean actual = TileBag.isEmpty();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    void test_TileBag_takeOutTile() {
        System.out.println("Test 7: test TileBag class and method takeOutTile");
        Tile tile = new Tile('Q', 10);
        Tile getTile = TileBag.takeOutTile();
        assert getTile != null;
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
    void test_HumanAction_coveredCenterSquares() throws IOException {
        System.out.println("Test 10: test HumanAction class coveredCenterSquares method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        GameBoard.printBoard();
        String letters = "GIT";
        String position = "f8";
        String direction = "r";
        boolean expected = true;
        boolean actual = Move.coveredCenterSquares(letters, position, direction);
        Assertions.assertEquals(expected, actual);
    }



    @Test
    @Order(11)
    void test_Score_addScore() throws IOException {
        System.out.println("Test 11: test Scores class addScore method");

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
    void test_Move_recoverBoardGridContent() throws IOException {
        System.out.println("Test 13: test Scoring WordsOnBoard class multiWordsOrNoneRow method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        System.out.println("\n===Before the game===");
        GameBoard.printBoard();
        Player human = new Player(true);
        Move move = new Move(human, true,"GIT", "f8", "r");
        System.out.println("\n===After the move, before the recover===");
        GameBoard.printBoard();
        move.recoverBoardGridContent();
        System.out.println("\n===After the recover===");
        GameBoard.printBoard();

    }

    @Test
    @Order(14)
    void test_Move_multiWordsOrNoneRow_isAnyRightAngleNewWord() throws IOException {
        System.out.println("Test 14: test Scoring Move class multiWordsOrNoneRow method and" +
                " isAnyRightAngleNewWord method");
        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        GameBoard.printBoard();
        Player human = new Player(true);
        Move move1 = new Move(human, true,"GIT", "f8", "r");
        move1.recoverBoardGridContent();
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"SAR", "h7", "d");
        move2.recoverBoardGridContent();
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, false, "TG", "g9", "r");

        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }



    @Test
    @Order(15)
    void test_Move_validate_direction_recognise() throws IOException {
        System.out.println("Test 15: test valid move reading through the specialized direction");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        GameBoard.printBoard();
        Player human = new Player(true);
        Move move1 = new Move(human, true,"GIT", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"GIT");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"SAR", "h7", "d");
        move2.recoverBoardGridContent();
        WordsOnBoard.addWord(7, 5, 10, 5,"STAR");
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, false,"R", "i9", "r");
        System.out.println("\n=== After move3 and recovered ===");
        move3.recoverBoardGridContent();
        GameBoard.printBoard();

        // ar is a legal word but it's read to left direction
        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(16)
    void test_Move_recoverBoardGridContentForInvalidMove() throws IOException {
        System.out.println("Test 16: test Move class recoverBoardGridContentForInvalidMove method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;

        GameBoard.printBoard();
        Player human = new Player(true);
        Move move1 = new Move(human, true,"GIT", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"GIT");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"SAR", "h7", "d");
        move2.recoverBoardGridContent();
        WordsOnBoard.addWord(7, 5, 10, 5,"STAR");
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();
        Move move3 = new Move(human, false,"TG", "g9", "r");
        System.out.println("\n=== After move3 and recovered ===");
        move3.recoverBoardGridContentForInvalidMove();
        GameBoard.printBoard();

        boolean expected = false;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(17)
    void test_ComputerAction_autoMove() throws IOException {
        System.out.println("Test 17: test ComputerAction class autoMove method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();


        GameBoard.printBoard();
        Player human = new Player(true);
        Move hmMove = new Move(human, true, "GIT", "f8", "r");
        hmMove.recoverBoardGridContent();
        GameBoard.printBoard();

        Player computer = new Player(false);
        TileRack pcRack = new TileRack(computer);
        computer.setTileRack(pcRack);

        ComputerAction pcAction = new ComputerAction(computer, false);
        if (!pcAction.skipped) {
            Move pcMove = pcAction.getMove();
            pcMove.recoverBoardGridContent();
            System.out.println("\n=== After PC move and recovered ===");
            GameBoard.printBoard();
            System.out.println(pcMove.inputLetters);
            boolean expected = true;
            boolean actual = pcMove.isValid;
            Assertions.assertEquals(expected, actual);
        } else {
            System.out.println("Computer skipped");
            boolean expected = true;
            boolean actual = pcAction.skipped;
            Assertions.assertEquals(expected, actual);
        }
    }


    @Test
    @Order(18)
    void test_Move_multiWordsOrNoneRow2() throws IOException {
        System.out.println("Test 18: test WordsOnBoard class multiWordsOrNoneCol method 2");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        Player human = new Player(true);
        Move move1 = new Move(human, true, "LIE", "h15", "r");
        move1.recoverBoardGridContent();
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"DE", "i13", "d");
        GameBoard.printBoard();

        boolean expected = false;
        boolean actual = move2.isValid;
        Assertions.assertEquals(expected, actual);
    }



    @Test
    @Order(19)
    void test_Move_validate_inputOneLetter() throws IOException {
        System.out.println("Test 19: test move validation");
        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        GameBoard.printBoard();
        Player human = new Player(true);
        Move move1 = new Move(human, true,"BAN", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"BAN");
        System.out.println("\n=== After move1 and recovered ===");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"T", "g7", "d");
        move2.recoverBoardGridContent();
        System.out.println("\n=== After move2 and recovered ===");
        GameBoard.printBoard();

        boolean expected = true;
        boolean actual = move2.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(20)
    void test_Scoring_calculateMoveScore1() throws IOException {
        System.out.println("Test 20: test Scoring class calculateMoveScore method 1");
        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        Player human = new Player(true);
        Move move1 = new Move(human, true,"GIT", "f8", "r");
        move1.recoverBoardGridContent();
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"SAR", "h7", "d");
        Scoring scoring = new Scoring(move2);
        int expected = 4;
        int actual = scoring.getScore();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(21)
    void test_Move_isRightAngleExistWordNoOverlap() throws IOException {
        System.out.println("Test 21: test Move class isRightAngleExistWordNoOverlap method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        Player human = new Player(true);
        Move move1 = new Move(human, true,"NO", "h8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 7, 8, 8,"NO");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"To", "i7", "d");
        move2.recoverBoardGridContent();
        GameBoard.printBoard();
        WordsOnBoard.addWord(9, 7, 9, 9,"TOo");
        Move move3 = new Move(human, false,"SD", "g8", "r");
        move3.recoverBoardGridContent();
        GameBoard.printBoard();


        boolean expected = true;
        boolean actual = move3.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(22)
    void test_Move_isNextToParallelPlayedWord() throws IOException {
        System.out.println("Test 22: test Move class isNextToParallelPlayedWord method");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./resources/defaultBoard.txt");
        GameBoard.size = 15;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        Player human = new Player(true);
        Move move1 = new Move(human, true,"BAN", "f8", "r");
        move1.recoverBoardGridContent();
        WordsOnBoard.addWord(8, 5, 8, 7,"BAN");
        GameBoard.printBoard();
        Move move2 = new Move(human, false,"BAN", "h9", "r");
        move2.recoverBoardGridContent();
        GameBoard.printBoard();

        boolean expected = false;
        boolean actual = move2.isValid;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(23)
    void test_Scoring_calculateMoveScore2() throws IOException {
        System.out.println("Test 23: test Scoring class calculateMoveScore method 2");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./test_res/26.txt");
        GameBoard.size = 26;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        System.out.println("Original board:");
        GameBoard.printBoard();
        Player human = new Player(true);
        Move move = new Move(human, true,"CODE", "m13", "r"); //3121
        Scoring scoring = new Scoring(move);
        move.recoverBoardGridContent();
        GameBoard.printBoard();
        int expected = 90;
        int actual = scoring.getScore();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(24)
    void test_Scoring_calculateMoveScore3() throws IOException {
        System.out.println("Test 20: test Scoring class calculateMoveScore method 3");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./test_res/26.txt");
        GameBoard.size = 26;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        System.out.println("Original board:");
        GameBoard.printBoard();
        Player human = new Player(true);
        Move move = new Move(human, true,"COdE", "m13", "r");
        Scoring scoring = new Scoring(move);
        move.recoverBoardGridContent();
        GameBoard.printBoard();
        int expected = 110;
        int actual = scoring.getScore();
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @Order(25)
    void test_Scoring_calculateMoveScore4() throws IOException {
        System.out.println("Test 20: test Scoring class calculateMoveScore method 4");

        GameBoard gameBoard = GameBoard.getInstance();
        gameBoard.chooseBoard("./test_res/26.txt");
        GameBoard.size = 26;
        WordList wordList = WordList.getInstance("./resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();

        System.out.println("Original board:");
        GameBoard.printBoard();
        Player human = new Player(true);
        Move move = new Move(human, true,"PREMIUM", "m13", "r"); //plus 70 extra
        Scoring scoring = new Scoring(move);
        move.recoverBoardGridContent();
        GameBoard.printBoard();
        int expected = 240;
        int actual = scoring.getScore();
        Assertions.assertEquals(expected, actual);
    }

}
