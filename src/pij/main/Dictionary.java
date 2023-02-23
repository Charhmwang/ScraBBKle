package pij.main;

import java.util.HashMap;

public class Dictionary {
    private HashMap<Character, Integer> letterPoints;
    private final WordList wordList;
    public Dictionary(WordList wordList) {
        this.wordList = wordList;
    }

    public int getWordScore(String word){
        int score = 0;
        for (char c : word.toCharArray()){
            c = Character.toUpperCase(c); // In case user choose a lower-case letter for wildcard
            score += letterPoints.get(c);
        }
        return score;
    }

    public boolean validateWord(String word) { return wordList.map.containsKey(word); }

    public void setupLetterScores() {
        /*
        (1 point)-A, E, I, O, U, L, N, S, T, R.
        (2 points)-D, G.
        (3 points)-B, C, M, P.
        (4 points)-F, H, V, W, Y.
        (5 points)-K.
        (8 points)- J, X.
        (10 points)-Q, Z.
         */
        letterPoints = new HashMap<>();
        letterPoints.put('A', 1);
        letterPoints.put('B', 3);
        letterPoints.put('C', 3);
        letterPoints.put('D', 2);
        letterPoints.put('E', 1);
        letterPoints.put('F', 4);
        letterPoints.put('G', 2);
        letterPoints.put('H', 4);
        letterPoints.put('I', 1);
        letterPoints.put('J', 8);
        letterPoints.put('K', 5);
        letterPoints.put('L', 1);
        letterPoints.put('M', 3);
        letterPoints.put('N', 1);
        letterPoints.put('O', 1);
        letterPoints.put('P', 3);
        letterPoints.put('Q', 10);
        letterPoints.put('R', 1);
        letterPoints.put('S', 1);
        letterPoints.put('T', 1);
        letterPoints.put('U', 1);
        letterPoints.put('V', 4);
        letterPoints.put('W', 4);
        letterPoints.put('X', 8);
        letterPoints.put('Y', 4);
        letterPoints.put('Z', 10);
    }
}
