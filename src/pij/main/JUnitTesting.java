package pij.main;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test scrabble functions.
 *
 * @author Haomeng Wang
 */
public class JUnitTesting {

    // Test 1 - An I/O Error Occurred
    @Test
    void testValidateUserBoard1() {
        String userFilePath = "../test_resources/invalidBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = false;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be invalid.");
    }

    // Test 2 - An I/O Error Occurred
    @Test
    void testValidateUserBoard2() {
        String userFilePath = "../resources/defaultBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = true;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be valid.");
    }



}

