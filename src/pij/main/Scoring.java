package pij.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Scoring class is to score a specific valid move.
 * Object of this class are immutable: after an object of this class has been created,
 * one cannot change the value of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class Scoring {

    /** The score of the given move. Value assigned in constructor.
     * Always non-null after object creation. Immutable. */
    private final int score;

    /** The specific move to be scored.
     * Always non-null after object creation. Immutable. */
    private final Move move;


    /**
     * Constructs a Scoring instance to calculate score for a valid move.
     *
     * @param move the specific move supposed to be scored; must not be null
     */
    public Scoring(Move move) {
        this.move = move;
        this.score = calculateMoveScore();
    }


    /**
     * Returns the score of the move gained from this Scoring.
     *
     * @return the score of the move gained
     */
    public int getScore() { return score; }


    /**
     * Calculates the move gaining score. Each case is commented in the following code.
     *
     * @return the calculated score
     */
    public int calculateMoveScore() {
        int totalScoreOfThisMove;
        int scoreWithoutPremiumWord = 0;
        int wordSize = move.getMadeNewWord().length();
        int startRow = move.get_start_and_endPosOfNewWord().get(0);
        int startCol = move.get_start_and_endPosOfNewWord().get(1);
        boolean hasPremiumWordSqr  = false;
        List<Integer> factorInPremiumWordSqr = new ArrayList<>();

        for (int i = 0; i < wordSize; i++) {
            String square;
            if (move.getDirection().equals("right")) {
                square = GameBoard.getBoardSquareContent(startRow, startCol + i);
            }
            else {
                square = GameBoard.getBoardSquareContent(startRow + i, startCol);
            }

            // Checks the square contents,
            // if it's new added letter, contents in form of "G{3}", "G(2)", "T." or "t." or "t{3}" (if using wildcard)
            // if it's existed letter, contents in form of "G2" "I1" "g3" (if using wildcard)
            if (Move.isSquareCoveredByTile(square) != null) { // already covered by a tile
                scoreWithoutPremiumWord += getNumber(square);
            } else { // new tile maybe with premium factors
                char letter = square.charAt(0);
                if (square.charAt(1) == '.') {
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.getMap().get(letter);
                    else scoreWithoutPremiumWord += 3;
                }
                if (square.charAt(1) == '(') {  // premium letter, multiply factor of the current letter
                    int factor = getNumber(square);
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.getMap().get(letter) * factor;
                    else scoreWithoutPremiumWord += 3 * factor;
                }
                if (square.charAt(1) == '{') {  // !premium word, multiply the whole word value with factor
                    hasPremiumWordSqr = true;
                    if (Character.isUpperCase(letter))
                        scoreWithoutPremiumWord += LetterPoints.getMap().get(letter);
                    else scoreWithoutPremiumWord += 3;
                    factorInPremiumWordSqr.add(getNumber(square));
                }
            }
        }

        if (hasPremiumWordSqr) {
            // Calculates the product in the list of premium word square factors
            int factorProduct = getWordFactorProduct(factorInPremiumWordSqr);
            totalScoreOfThisMove = scoreWithoutPremiumWord * factorProduct;
        }
        else totalScoreOfThisMove = scoreWithoutPremiumWord;

        // Checks whether player used all 7 tiles in this move to get awarded 70 extra points
        if (move.getTilesSetInto().size() == 7) totalScoreOfThisMove += 70;

        return totalScoreOfThisMove;
    }


    /**
     * Calculates the total factor from the premium word square(s).
     *
     * @param factorInPremiumWordSqr list of premium word square factors; must not be null
     * @return the total factor from the premium word square(s)
     */
    public int getWordFactorProduct(List<Integer> factorInPremiumWordSqr) {
        int res = 1;
        for (int i : factorInPremiumWordSqr) {
            res *= i;
        }
        return res;
    }


    /**
     * Returns the factor number in a premium letter or word square.
     *
     * @param square the contents of a premium letter or word square; must not be null or empty string
     * @return the factor number in a premium letter or word square
     */
    public int getNumber(String square) {
        String factorStr = "";
        for (int i = 0; i < square.length(); i++) {
            char cur = square.charAt(i);
            if (Character.isDigit(cur)) {
                factorStr += cur;
            }
        }
        if (factorStr.isEmpty()) return 0;
        else return Integer.parseInt(factorStr);
    }


    /**
     * Reduce the sum of the values of the player's own un-played tiles from its score.
     *
     * @param player the specific player to get score reduced; must not be null
     */
    public static void removeScoresFromRemainedTiles(Player player) {
        int remove = getSumValuesOfRack(player.getTileRack());
        player.reduceScore(remove);
    }


    /**
     * Returns the sum of the values of all the tiles on the rack.
     *
     * @param tileRack the TileRack to be checked; must not be null
     * @return sum of the values of all the tiles on the rack
     */
    public static int getSumValuesOfRack(TileRack tileRack) {
        int sum = 0;
        for (Tile t : tileRack.getTiles()) {
            if (Character.isLowerCase(t.getLetter())) sum += 3;
            else sum += LetterPoints.getMap().get(t.getLetter());
        }
        return sum;
    }

}
