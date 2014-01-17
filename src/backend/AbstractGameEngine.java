package backend;

public abstract class AbstractGameEngine {
    private int turn = 0;
    private int playersNumber;

    protected AbstractGameEngine(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public void incCurrentTurn() {
        ++turn;
    }

    public int getCurrentPlayer() {
        return turn % playersNumber;
    }

    public int getCurrentTurn() {
        return turn;
    }
}

