package backend;
import org.codehaus.jackson.JsonNode;

public interface GameEngine {
    public JsonNode getState();

    /** @return Returns whether the game is over or not. */
    public boolean play(JsonNode move) throws InvalidMoveException;

    public int getCurrentPlayer();
    public int getCurrentTurn();
    public int getScore(int player);
}

