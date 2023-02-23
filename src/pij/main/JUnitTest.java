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
        System.out.println("Test 3: find words in Dictionary - class Dictionary, WordList");
        WordList wl = new WordList();
        boolean expected = true; // existing
        Assertions.assertEquals(expected, wl.validateWord("academic"));
    }

    @Test
    void testWord_in_Dictionary2() throws IOException {
        System.out.println("Test 4: find words in Dictionary - class Dictionary, WordList");
        WordList wl = new WordList();
        boolean expected = false; // not existing
        Assertions.assertEquals(expected, wl.validateWord("zza"));
    }


}
