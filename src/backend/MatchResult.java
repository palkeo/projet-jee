package backend;

public class MatchResult
{
    private int winner;
    private Exception error;

    public MatchResult(int winner, Exception error)
    {
        this.winner = winner;
        this.error = error;
    }

    public int getWinner()
    {
        return winner;
    }

    public Exception getError()
    {
        return error;
    }
}
