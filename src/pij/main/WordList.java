package pij.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Wordlist class reads a txt file adding each line word into the program's wordlist record.
 * Singleton class, has a map attribute for recording the legal words.
 * Object of this class is immutable: after an object of class WordList
 * has been created, one cannot change the values of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class WordList {

    /** The list recording legal words. Always non-null after object creation */
    private static List<String> wordList;

    /** WordList instance, set as null initially.
     * Private to be hidden from outside the WordList class.
     */
    private static WordList WordListInstance;


    /**
     * For other classes getting the WordList instance.
     * If the instance has never been created, initiate one then return.
     * If the instance has already been initiated, then return the created one.
     * Ensures the WordList instance can be created once only in the program.
     *
     * @return the sole WordList instance; always non-null
     */
    public synchronized static WordList getInstance(String fileName) throws IOException {
        if (WordListInstance == null) {
            WordListInstance = new WordList(fileName);
        }
        return WordListInstance;
    }


    /**
     * Constructs a WordList instance.
     * Private constructor ensures instance can only be initiated inside the class.
     *
     * @param fileName the word list resource file path name; must be not null or empty string
     * @throws IOException if the file is not found
     */
    private WordList(String fileName) throws IOException {
        Scanner sc = new Scanner(new File(fileName), StandardCharsets.UTF_8);
        wordList = new ArrayList<>();
        while (sc.hasNextLine()) {
            String word = sc.nextLine();
            wordList.add(word);
        }
    }


    /**
     * Returns a boolean result whether the word is a legal word found in WordList.
     *
     * @param word the validating word; must not be null
     * @return a boolean result whether the word is legal
     */
    public static boolean validateWord(String word) {
        return wordList.contains(word);
    }
}
