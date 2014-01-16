package games;

import backend.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.ArrayList;

public class Connect4 extends AbstractGameEngine implements GameEngine {
    final int colNumber = 7, lineNumber = 6;
    int winner = -1;

    final ArrayList<ArrayList<Integer>> cols
        = new ArrayList<ArrayList<Integer>>(colNumber);

    public Connect4() {
        super(2);

        for(int i = 0; i < colNumber; ++i) {
            cols.add(new ArrayList<Integer>(lineNumber));
        }
    }

    @Override
    public JsonNode getState() {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        for(ArrayList<Integer> col : cols) {
            ArrayNode jsonCol = JsonNodeFactory.instance.arrayNode();

            for(int player : col) {
                jsonCol.add(player);
            }

            result.add(jsonCol);
        }

        return result;
    }

    /** A Connect-4 move is just an integer representing the column the player
        puts its token in. */
    @Override
    public boolean play(JsonNode move) throws InvalidMoveException {
        incCurrentTurn();

        if(move.isInt()) {
            int givenCol = move.getIntValue();

            ArrayList<Integer> col;
            if(0 <= givenCol && givenCol < colNumber
            && (col = cols.get(givenCol)).size() <= lineNumber-1) {
                int player = getCurrentPlayer();
                col.add(player);

                if(hasWon(givenCol)) {
                    winner = player;
                    return true;
                }
                else {
                    return getCurrentTurn() == colNumber*lineNumber;
                }
            }
            else {
                throw new InvalidMoveException(
                    "Cannot play in column " + givenCol
                );
            }
        }
        else {
            throw new InvalidMoveException(
                "A Connect-4 move shall be an integer"
            );
        }
    }

    @Override
    public int getScore(int player) {
        return player == winner ? 1 : 0;
    }

    private boolean hasWon(int lastCol) {
        int line = cols.get(lastCol).size()-1;
        int me = cols.get(lastCol).get(line);

        // todo: find an elegant way to check whether we won or not

        return false;
    }
}

