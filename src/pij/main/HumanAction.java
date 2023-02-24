package pij.main;

public class HumanAction {

    private final Player human;
    private Move move;
    private final String[] inputNoComma;
    public HumanAction(Player human) {
        this.human = human;
        inputNoComma = promptMove();
    }
    public String[] promptMove() {
        String[] strArr = new String[3];
        boolean rightForm = false;
        while (!rightForm) {
            System.out.println("Your move: ");
            String input = System.console().readLine();
            if (input.chars().filter(ch -> ch == ',').count() == 2) {
                strArr = input.split(",");
                move = new Move(human, strArr[0], strArr[1], strArr[2]);
                if (move.isValid) {
                    rightForm = true;
                    System.out.println(move.toString());
                }
            } else {
                System.out.println("Invalid move! Re-enter your move decision in the next line.");
            }
        }
        return strArr;
    }

    String[] getInputNoComma() { return inputNoComma; }

    Move getMove()  { return move; }
}
