package pij.main;


/**
 * Start the game process from here.
 *
 * @author Haomeng Wang
 * @version 1.1
 */

public class ScraBBKle {
    private GameBoard board;
    private Player human;
    private Player computer;
    public static boolean gameOver = false;
    boolean hmSkip = false;
    boolean pcSkip = false;
    boolean bothBagRackEmpty;
    private TileRack humanRack;
    public LetterPoints letterPoints;
    public TileBag tileBag;
    public WordsOnBoard wordsOnBoard;


    public ScraBBKle(GameBoard board) {
        this.board = board;
    }

    public void startGame(Player human, Player computer, TileRack humanRack, LetterPoints letterPoints,
                          TileBag tileBag, WordsOnBoard wordsOnBoard) {
        this.human = human;
        this.computer = computer;
        this.humanRack = humanRack;
        this.letterPoints = letterPoints;
        this.tileBag = tileBag;
        this.wordsOnBoard = wordsOnBoard;
    }

    public void gameSteps() {

        // The game ends when the tile bag is empty and one of the player has an empty tile rack.
        // The game also ends if both players pass twice in a row.

        while (!gameOver) {

            GameBoard.printBoard();
            humanRack.displayTiles();
            HumanAction hmAction = new HumanAction(human);
            if (!hmAction.skipped) {
                hmScoringOperation(hmAction);
                if (bothBagRackEmpty) break;

            } else { //human skip to Computer's turn
                hmSkip = true;
                // If before human's skip, computer already skipped once, now here is 2 skips in a row, game over.
                if (pcSkip) break;

                ComputerAction computerAction = new ComputerAction(computer);
                if (!computerAction.skipped) pcScoringOperation(computerAction);
                else break; // both computer and human skipped, so game over
            }

            // If human skipped, means pc already played its move in the else block on top,
            // so now it should be the human's turn, i.e. go back to the beginning of the while loop.
            if (hmSkip) continue;

            ComputerAction computerAction = new ComputerAction(computer);
            if (!computerAction.skipped) {
                pcScoringOperation(computerAction);
                if (bothBagRackEmpty) break;

            }
            else pcSkip = true;
        }

        // Now game over, calculate scores and display the winner
        // use static removeScoresFromRemainedTiles(player) method in Scoring class
        System.out.println("Game Over!");
        Scoring.removeScoresFromRemainedTiles(human);
        Scoring.removeScoresFromRemainedTiles(computer);
        int humanScore = human.getScore();
        int pcScore = computer.getScore();
        System.out.println("The human player scored " + humanScore + " points.");
        System.out.println("The computer player scored " + pcScore + " points.");

        if (humanScore > pcScore) {
            System.out.println("The human player wins!");
        } else if (humanScore < pcScore) {
            System.out.println("The computer player wins!");
        } else {
            System.out.println("It's a draw!");
        }

    }

    public void hmScoringOperation(HumanAction hmAction) {
        hmSkip = false;
        // Add scores to human, print out the move
        Move hmMove = hmAction.getMove();
        System.out.println();
        Scoring scoring = new Scoring(hmMove);
        human.addScore(scoring.calculateMoveScore());
        System.out.println(hmMove);
        System.out.println();
        // if move execute return false means tiles bag is empty and one of the player's rack is empty too
        bothBagRackEmpty = hmMove.execute();
        System.out.println();
        System.out.println(human);
        System.out.println(computer);
        System.out.println();
        GameBoard.printBoard();
        System.out.println();
    }

    public void pcScoringOperation(ComputerAction computerAction) {
        pcSkip = false;
        Move pcMove = computerAction.getMove();
        System.out.println();
        Scoring scoring = new Scoring(pcMove);
        computer.addScore(scoring.calculateMoveScore());

        System.out.println(pcMove);
        System.out.println("\nThe result is:");
        System.out.println(human);
        System.out.println(computer);
        bothBagRackEmpty = pcMove.execute();

    }

}
