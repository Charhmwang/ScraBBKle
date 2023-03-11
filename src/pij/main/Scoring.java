package pij.main;

import java.util.ArrayList;
import java.util.List;

// Present the scores getting from each move
public class Scoring {

    private int score;
    private final Move move;

    public Scoring(Move move) {
        this.move = move;
        this.score = calculateMoveScore();
    }

    public int getScore() { return score; }

    public int calculateMoveScore() {
        int totalScoreOfThisMove = 0;
        int scoreWithoutPremiumWord = 0;

        int wordSize = move.madeNewWord.length();
        //( (newWord, idxOfNewWord), tilesSetInto )
        int startRow = move.start_and_endPosOfNewWord.get(0), startCol = move.start_and_endPosOfNewWord.get(1);
        //System.out.println("startRow: " + startRow + "; startCol: " + startCol);  //debug
        boolean hasPremiumWordSqr  = false;
        List<Integer> factorInPremiumWordSqr = new ArrayList<>();
        for (int i = 0; i < wordSize; i++) {
            String grid = "";
            if (move.direction.equals("right"))
                grid = GameBoard.getBoardGridContent(startRow, startCol + i);
            else grid = GameBoard.getBoardGridContent(startRow + i, startCol);

            System.out.println(grid);
            // check grid content, if it's new added letter, content is in form of "G{3}", "G(2)", "T." or "t." or "t{3}"(if used wildcard)
            // if it's existed letter, content is in form of "G2" "I1" "g3"(if used wildcard)
            if (Move.isGridCoveredByTile(grid) != null) { // already covered by a tile
                scoreWithoutPremiumWord += getNumber(grid);
            } else { // new tile maybe with premium factors
                char letter = grid.charAt(0);
                if (grid.charAt(1) == '.') {
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.letterMap.get(letter);
                    else scoreWithoutPremiumWord += 3;
                }
                if (grid.charAt(1) == '(') {  // premium letter, multiply factor of the current letter
                    int factor = getNumber(grid);
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.letterMap.get(letter) * factor;
                    else scoreWithoutPremiumWord += 3 * factor;
                }
                if (grid.charAt(1) == '{') {  // !premium word, multiply the whole word value with factor
                    hasPremiumWordSqr = true;
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.letterMap.get(letter);
                    else scoreWithoutPremiumWord += 3;
                    factorInPremiumWordSqr.add(getNumber(grid));
                }
            }
        }

        if (hasPremiumWordSqr) {
            // Calculate the product in list
            int factorProduct = getWordFactorProduct(factorInPremiumWordSqr);
            totalScoreOfThisMove = score * factorProduct;
        }
        else totalScoreOfThisMove = scoreWithoutPremiumWord;

        // Check whether player used all 7 tiles in this move to get awarded 70 extra points
        if (move.tilesSetInto.size() == 7) totalScoreOfThisMove += 70;

        return totalScoreOfThisMove;
    }

    public int getWordFactorProduct(List<Integer> factorInPremiumWordSqr) {
        int res = 1;
        for (int i : factorInPremiumWordSqr) {
            res *= i;
        }
        return res;
    }

    public int getNumber(String grid) {
        String factorStr = "";
        for (int i = 0; i < grid.length(); i++) {
            char cur = grid.charAt(i);
            if (Character.isDigit(cur)) {
                factorStr += cur;
            }
        }
        if (factorStr.isEmpty()) return 0;
        else return Integer.parseInt(factorStr);
    }


    // At the end of the game, each player’s score is reduced by the sum of the values of their own un-played tiles.
    public static void removeScoresFromRemainedTiles(Player player) {
        int remove = getSumValuesOfRack(player.getTileRack());
        player.reduceScore(remove);
    }


    public static int getSumValuesOfRack(TileRack tileRack) {
        int sum = 0;
        for (Tile t : tileRack.getTiles()) {
            if (Character.isLowerCase(t.letter)) sum += 3;
            else sum += LetterPoints.letterMap.get(t.letter);
        }
        return sum;
    }

}
