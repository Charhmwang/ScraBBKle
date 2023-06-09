package pij.main;

/**
 * Abstract base class of HumanAction class and ComputerAction class.
 * An Action subclass object has a player, a boolean attribute to mark whether it is making the first move in game,
 * a move and a boolean attribute to mark whether this action is skipping the turn.
 *
 * Two attributes of this class are immutable:
 * after an object of subclass of Action has been created, the values of player and firstMove cannot be changed.
 *
 * @author Haomeng
 * @version 1.0
 */
public abstract class Action {

    /** The role of the Player. Always non-null after subclass object creation. */
    protected final Player player;

    /** Identify whether the first move of game. Always non-null after subclass object creation. */
    protected final boolean firstMove;

    /** The move decision made by player. Can be null after settings which represents player skipping the current turn. */
    protected Move move;

    /** The move status. Always non-null subclass object creation. */
    protected Boolean skipped;


    /**
     * Constructs a new Action with player's role, and whether first move.
     * Assign values to the attributes move and skipped.
     *
     * @param player the role of the Player; must not be null
     * @param firstMove whether the first move of game; must not be null
     */
    public Action(Player player, boolean firstMove) {
        this.player = player;
        this.firstMove = firstMove;
        setMove();
        setSkipped();
    }

    /**
     * Procedure of player making a move decision and assign to attribute move.
     * The implementations are vary in subclasses.
     */
    public void setMove() {}

    /**
     * Sets the attribute skipped based on the move assignment value after setMove method in the corresponding subclass.
     * If player skipped or tried some invalid moves then skipped, attribute skipped will be assigned as true.
     */
    public void setSkipped() {
        skipped = move == null || !move.getIsValid();
    }

    /**
     * Returns the move of this Action.
     *
     * @return the move; can be null
     */
    public Move getMove() { return this.move; }

    /**
     * Returns a boolean result of this Action whether skipped the move.
     *
     * @return a boolean result whether the move skipped
     */
    public Boolean getSkipped() { return this.skipped; }
}
