package pij.main;

public class Tile {
    private char letter;
    int points;
    boolean isWildCard;

    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        isWildCard = letter == '?';
    }

    @Override
    public String toString() {
        return "[" + letter + points + "]";
    }

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
