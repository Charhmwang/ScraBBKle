package pij.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class reads a txt file adding each line word into the program's wordlist record.
 * Singleton class.
 *
 * @author Haomeng Wang
 */

public class WordList {
    private static HashMap<String, Boolean> map;
    private static WordList WordListInstance = null;
    public synchronized static WordList getInstance(String fileName) throws IOException {
        if (WordListInstance == null) {
            WordListInstance = new WordList(fileName);
        }
        return WordListInstance; }

    private WordList(String fileName) throws IOException {
        Scanner sc = new Scanner(new File(fileName), StandardCharsets.UTF_8);
        map = new HashMap<>();
        while (sc.hasNextLine()) {
            String word = sc.nextLine();
            map.put(word, true);
        }
    }

    public static boolean validateWord(String word) { return map.containsKey(word); }
}
