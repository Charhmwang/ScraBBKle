package pij.main;

public abstract class Action {

    protected final Player player;
    protected final boolean firstMove;
    protected Move move;
    protected Boolean skipped;


    public Action(Player player, boolean firstMove) {
        this.player = player;
        this.firstMove = firstMove;
    }

    public void setMove() {}
    public void setSkipped() { skipped = move == null; }
    public Move getMove() { return this.move; }
    public Boolean getSkipped() { return this.skipped; }
}
