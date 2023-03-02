package pij.main;

public class HumanAction {

    private final Player human;
    private Move move;
    public Boolean skipped;


    public HumanAction(Player human) {
        this.human = human;
        move = promptMove();
        if (move == null) skipped = true;
        else move.execute();
    }


    public Move promptMove() {
        String[] strArr = new String[3];
        boolean rightForm = false;
        Move move = null;
        while (!rightForm) {
            System.out.println("Your move: (Type \'S\' to skip)");
            String input = System.console().readLine();
            if (input.equals("S")) return null;

            if (input.chars().filter(ch -> ch == ',').count() == 2) {
                strArr = input.split(",");
                move = new Move(human, strArr[0], strArr[1], strArr[2]);
                if (move.isValid) {
                    rightForm = true;
                    System.out.println(move);
                } else {
                    // Reject invalid move and recover the board content IF the board has been revised while validating,
                    // see explanation in method buildWordUsingTileLetters in WordsOnBoard class
                    // need to update the grid content from such as "G{3}" into "{3}" or "T." into "."
                    System.out.println("Invalid move! Re-enter your move decision in the next line.");

                    int row = strArr[1].charAt(1) - '1';
                    int col = strArr[1].charAt(0) - 'a';
                    // Check if the grid content was revised
                    String gridContent = GameBoard.getBoardGridContent(row, col);
                    if ((gridContent.charAt(0) != '.' && gridContent.charAt(0) != '{' && gridContent.charAt(0) != '(')
                            && (gridContent.charAt(1) == '.' || gridContent.charAt(1) == '{' || gridContent.charAt(1) == '(')) {

                        gridContent = gridContent.substring(1);
                        GameBoard.reviseBoard(row, col, gridContent);
                    }
                }
            } else {
                System.out.println("Invalid input form! Re-enter your move decision in the next line.");
            }
        }
        return move;
    }

}
