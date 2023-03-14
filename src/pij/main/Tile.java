package pij.main;

import java.util.Objects;

public class Tile {
    private char letter;
    private final int points;
    private final boolean wildCard;

    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        wildCard = letter == '?';
    }

    public char getLetter() { return this.letter; }
    public boolean isWildCard() { return wildCard; }

    @Override
    public String toString() {
        return "[" + letter + points + "]";
    }

    @Override
    public int hashCode() { return Objects.hash(letter, points, wildCard); }

    public void wildCardChar() {
        boolean correctInput = false;
        while(!correctInput) {
            System.out.print("Choose a letter for wildcard (lowercase letter from 'a' to 'z'): ");
            String choice = System.console().readLine();
            if (choice.length() == 1) {
                char ch = choice.charAt(0);
                if (Character.isAlphabetic(ch) && Character.getType(choice.charAt(0)) == Character.LOWERCASE_LETTER) {
                    correctInput = true;
                    letter = ch;
                }
            } else System.out.println("\"" + choice + "\" is an invalid letter input for wildcard!");
        }
    }
}
