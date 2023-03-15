package pij.main;


import java.io.IOException;

/**
 * Start the game process from here.
 *
 * @author Haomeng Wang
 * @version 1.1
 */

public class ScraBBKle {
    private Player human;
    private Player computer;
    private boolean hmSkip = false;
    private boolean pcSkip = false;
    private boolean BagRackNotEmpty = false;
    private static ScraBBKle scraBBKleInstance;
    private ScraBBKle() {}
    public synchronized static ScraBBKle getInstance() {
        if (scraBBKleInstance == null) {
            scraBBKleInstance = new ScraBBKle();
        }
        return scraBBKleInstance;
    }

    public void startGame(Player human, Player computer) throws IOException {
        TileBag tileBag = TileBag.getInstance();
        WordList wordList = WordList.getInstance("../resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();
        WordsOnBoard wordsOnBoard = WordsOnBoard.getInstance();
        this.human = human;
        this.computer = computer;

        gameSteps();
    }

    public void gameSteps() {

        // The game ends when the tile bag is empty and one of the player has an empty tile rack.
        // The game also ends if both players pass twice in a row.
        boolean firstMoveDone = false;

        while (true) {
            // If pc skipped, there is no need to print the same game board again
            GameBoard.printBoard();

            human.getTileRack().displayTiles();
            HumanAction hmAction;
            // If the first step of game not done by any player yet
            if (!firstMoveDone) {
                hmAction = new HumanAction(human, true);
            } else {
                hmAction = new HumanAction(human, false);
            }
            //BUG IS IGNORED THE SCENARIO THAT HUMAN PLAYER TRIED INVALID MOVE AND THEN SKIP



            if (!hmAction.getSkipped()) {
                firstMoveDone = true;
                hmScoringOperation(hmAction);
                if (!BagRackNotEmpty) break;
            } else {
                //human skip to Computer's turn
                System.out.println("You skipped!\n\nComputer's turn... ");
                hmSkip = true;
                // If before human's skip, computer already skipped once, now here is 2 skips in a row, game over.
                if (pcSkip) {
                    System.out.println("Computer skipped!");
                    break;
                }

                ComputerAction computerAction;
                if (!firstMoveDone) {
                    computerAction = new ComputerAction(computer, true);
                } else {
                    computerAction = new ComputerAction(computer, false);
                }
                if (!computerAction.getSkipped()) {
                    firstMoveDone = true;
                    pcScoringOperation(computerAction);
                    if (!BagRackNotEmpty) break;
                } else {
                    System.out.println("Computer skipped!");
                    break; // both computer and human skipped, so game over
                }
            }

            // If human skipped, means pc already played its move in the else block on top,
            // so now it should be the human's turn, i.e. go back to the beginning of the while loop.
            if (hmSkip) continue;

            ComputerAction computerAction = new ComputerAction(computer, false);
            if (!computerAction.getSkipped()) {
                pcScoringOperation(computerAction);
                if (!BagRackNotEmpty) break;
            }
            else {
                System.out.println("Computer skipped!");
                pcSkip = true;
            }
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
        Scoring scoring = new Scoring(hmMove);
        human.addScore(scoring.getScore());
        System.out.println(hmMove);
        System.out.println();
        // if move execute return false means tiles bag is empty and one of the player's rack is empty too
        BagRackNotEmpty = hmMove.execute();
        System.out.println(human);
        System.out.println(computer);
        System.out.println();
        GameBoard.printBoard();
        System.out.println();
    }

    public void pcScoringOperation(ComputerAction computerAction) {
        pcSkip = false;
        Move pcMove = computerAction.getMove();
        Scoring scoring = new Scoring(pcMove);
        computer.addScore(scoring.getScore());
        System.out.println(pcMove);
        System.out.println("\nThe result is:");
        System.out.println(human);
        System.out.println(computer);
        BagRackNotEmpty = pcMove.execute();

    }

}
