package pij.main;


import java.io.IOException;

/**
 * Represent the game playing process. Singleton Class.
 * There are two Player attributes representing human and pc, two boolean attributes recording players
 * skip conditions, one boolean attribute recording the status of the TileBag along with TileRacks.
 *
 * This class object is mutable: after an object of class ScraBBKle has been created,
 * its attributes values are changeable in the game process.
 *
 * @author Haomeng Wang
 * @version 1.1
 */
public class ScraBBKle {

    /** Human player. Always non-null after object creation. */
    private final Player human;

    /** Computer player. Always non-null after object creation. */
    private final Player computer;

    /** The state whether human player skipped the move. Initialized to be false.
     * Mutable in the game process. */
    private boolean hmSkip = false;

    /** The state whether computer player skipped the move. Initialized to be false.
     * Mutable in the game process. */
    private boolean pcSkip = false;

    /** The state whether the tile bag is empty and one of the player has an empty tile rack.
     * Initialized to be false. Mutable in the game process. */
    private boolean BagRackNotEmpty = false;

    /** ScraBBKle instance, set as null initially.
     * Private to be hidden from outside the ScraBBKle class.
     */
    private static ScraBBKle scraBBKleInstance;


    /**
     * Constructs a ScraBBKle instance with a human player and a computer player.
     * Private constructor ensures instance can only be initiated inside the class.
     */
    private ScraBBKle(Player human, Player computer) throws IOException {
        TileBag tileBag = TileBag.getInstance();
        WordList wordList = WordList.getInstance("../resources/wordlist.txt");
        LetterPoints letterPoints = LetterPoints.getInstance();
        WordsOnBoard wordsOnBoard = WordsOnBoard.getInstance();
        this.human = human;
        this.computer = computer;

        gameSteps();
    }


    /**
     * For Main class getting the ScraBBKle instance.
     * If the instance has never been created, initiate one then return.
     * If the instance has already been initiated, then return the created one.
     * Ensures the ScraBBKle instance can be created once only in the program.
     *
     * @param human the human player; must not be null
     * @param computer the computer player; must not be null
     * @return the sole ScraBBKle instance
     * @throws IOException when there is a Runtime Exception
     */
    public synchronized static ScraBBKle getInstance(Player human, Player computer) throws IOException {
        if (scraBBKleInstance == null) {
            scraBBKleInstance = new ScraBBKle(human, computer);
        }
        return scraBBKleInstance;
    }


    /**
     * Main process of the game.
     * Game ends when the tile bag is empty and one of the player has an empty tile rack.
     * The game also ends if both players pass twice in a row.
     * Conditions are commented in the following code.
     */
    public void gameSteps() {

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

            if (!hmAction.getSkipped()) {
                firstMoveDone = true;
                hmScoringOperation(hmAction);
                if (!BagRackNotEmpty) break;
            } else {
                // Human skip to Computer's turn
                System.out.println("You skipped!\n");
                hmSkip = true;
                // If before human's skip, computer already skipped the last move,
                // now here are 2 skips in a row leading the game end.
                if (pcSkip) {
                    System.out.println("Computer skipped!");
                    break;
                }

                System.out.println("Computer's turn...\nPlease wait for a few seconds...");
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
                    break; // Both computer and human skipped leading the game end.
                }
            }

            // If human skipped, means pc already played its move in the else block,
            // so it should be the human's turn now, i.e. go back to the beginning of the while loop.
            if (hmSkip) continue;

            System.out.println("Computer's turn...\nPlease wait for a few seconds...");
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

        // Now game over, calculate scores and display the winner.
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


    /**
     * Scores the human player's move and add to the player's scores record,
     * then execute the move, i.e. revise the board targeting squares contents,
     * take off the using tiles from its rack and fill up the rack.
     *
     * @param hmAction the HumanAction that made the valid move; always non-null
     */
    public void hmScoringOperation(HumanAction hmAction) {
        hmSkip = false;
        // Add scores to human, print out the move
        Move hmMove = hmAction.getMove();
        Scoring scoring = new Scoring(hmMove);
        human.addScore(scoring.getScore());
        System.out.println(hmMove);
        System.out.println();
        // If move execute return false means tiles bag is empty and one of the player's rack is empty too
        BagRackNotEmpty = hmMove.execute();
        System.out.println(human);
        System.out.println(computer);
        System.out.println();
        GameBoard.printBoard();
        System.out.println();
    }


    /**
     * Scores the computer player's move and add to the player's scores record,
     * then execute the move, i.e. revise the board targeting squares contents,
     * take off the using tiles from its rack and fill up the rack.
     *
     * @param computerAction the ComputerAction that made the valid move; always non-null
     */
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
