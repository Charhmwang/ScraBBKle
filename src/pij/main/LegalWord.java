package pij.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LegalWord {
    public static List<String> LegalWordList;

    private Scanner sc = new Scanner(new File("./resources/wordlist.txt"), StandardCharsets.UTF_8);

    public LegalWord() throws IOException {
        LegalWordList = new ArrayList<>();
        while (sc.hasNextLine()) {
            String word = sc.nextLine();
            //eg "tapsters"
            int legalTimes = 0;
            for (int i = 0; i < word.length(); i++) {
                String cur = word.substring(i, i+1);
                for (int j = i+1; j < word.length(); j++) {
                    cur += word.substring(j,j+1);
                    if (WordList.validateWord(cur)) legalTimes++;
                }
            }
            if (legalTimes == 1) LegalWordList.add(word);
        }
    }
}
