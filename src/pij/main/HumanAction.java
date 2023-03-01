package pij.main;

public class HumanAction {

    private final Player human;
    private Move move;


    public HumanAction(Player human) {
        this.human = human;
        move = promptMove();
        //move.execute();
    }


    public Move promptMove() {
        String[] strArr = new String[3];
        boolean rightForm = false;
        Move move = null;
        while (!rightForm) {
            System.out.println("Your move: ");
            String input = System.console().readLine();
            if (input.chars().filter(ch -> ch == ',').count() == 2) {
                strArr = input.split(",");
                move = new Move(human, strArr[0], strArr[1], strArr[2]);
                if (move.isValid) {
                    rightForm = true;
                    System.out.println(move);
                } else {
                    // Reject invalid move
                    System.out.println("Invalid move! Re-enter your move decision in the next line.");
                }
            } else {
                System.out.println("Invalid input form! Re-enter your move decision in the next line.");
            }
        }
        return move;
    }




}
