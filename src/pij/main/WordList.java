package pij.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class WordList {
    public HashMap<String, Boolean> map;

    private Scanner sc = new Scanner(new File("./resources/wordlist.txt"), StandardCharsets.UTF_8);

    public WordList() throws IOException {
        map = new HashMap<>();
        while (sc.hasNextLine()) {
            String word = sc.nextLine();
            map.put(word, true);
        }
    }
}
