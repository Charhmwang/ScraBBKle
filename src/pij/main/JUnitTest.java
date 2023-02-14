package pij.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTest {
    @Test
    void testValidateUserBoard1() {
        System.out.println("Test 1: test an invalid board - ValidateUserBoard.class");
        String userFilePath = "./test_resources/invalidBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = false;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be invalid.");
    }

    @Test
    void testValidateUserBoard2() {
        System.out.println("Test 2: test a valid board - ValidateUserBoard.class");
        String userFilePath = "./resources/defaultBoard.txt";
        ValidateUserBoard tester = new ValidateUserBoard(userFilePath);
        boolean expected = true;
        boolean actual = tester.test();
        assertEquals(expected, actual, "The txt file should be valid.");
    }
    @Test
    void testDictionary_Trie_TrieNode() throws IOException {
        System.out.println("Test 3: numbers of words - Dictionary.class  TrieNode.class  Trie.class");
        WordTrie wordTrie = new WordTrie();
        Dictionary dictionary = new Dictionary(wordTrie);
        dictionary.readWordlist("./resources/wordlist.txt");
        int expectedWords = 267753; // 267753 words
        Assertions.assertEquals(expectedWords, wordTrie.num_words);
    }
}
