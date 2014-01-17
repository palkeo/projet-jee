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
        int player = getCurrentPlayer();
        incCurrentTurn();

        if(move.isInt()) {
            int givenCol = move.getIntValue();

            ArrayList<Integer> col;
            if(0 <= givenCol && givenCol < colNumber
            && (col = cols.get(givenCol)).size() <= lineNumber-1) {
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

    private boolean hasWon(int lastPlayedCol) {
        int lastPlayedLine = cols.get(lastPlayedCol).size()-1;
        int me = cols.get(lastPlayedCol).get(lastPlayedLine);

        // how ugly is that?

        // check horizontal
        int firstCheckedCol = Math.max(0, lastPlayedCol-3);
        for(int i = firstCheckedCol; i < firstCheckedCol+4; ++i) {
            try {
                if(cols.get(i+0).get(lastPlayedLine) == me
                && cols.get(i+1).get(lastPlayedLine) == me
                && cols.get(i+2).get(lastPlayedLine) == me
                && cols.get(i+3).get(lastPlayedLine) == me
                ) {
                    return true;
                }
            }
            catch(IndexOutOfBoundsException e) {}
        }

        // check vertical
        int firstCheckedLine = Math.max(0, lastPlayedLine-3);
        for(int i = firstCheckedLine; i < firstCheckedLine+4; ++i) {
            try {
                if(cols.get(lastPlayedCol).get(i+0) == me
                && cols.get(lastPlayedCol).get(i+1) == me
                && cols.get(lastPlayedCol).get(i+2) == me
                && cols.get(lastPlayedCol).get(i+3) == me
                ) {
                    return true;
                }
            }
            catch(IndexOutOfBoundsException e) {}
        }

        // check /
        for(int i = 0; i < colNumber-4; ++i) {
            for(int j = 0; j < lineNumber-4; ++j) {
                try {
                    if(cols.get(i+0).get(j+0) == me
                    && cols.get(i+1).get(j+1) == me
                    && cols.get(i+2).get(j+2) == me
                    && cols.get(i+3).get(j+3) == me
                    ) {
                        return true;
                    }
                }
                catch(IndexOutOfBoundsException e) {}
            }
        }

        // check \
        for(int i = 3; i < colNumber; ++i) {
            for(int j = 0; j < lineNumber-4; ++j) {
                try {
                    if(cols.get(i+0).get(j+0) == me
                    && cols.get(i-1).get(j+1) == me
                    && cols.get(i-2).get(j+2) == me
                    && cols.get(i-3).get(j+3) == me
                    ) {
                        return true;
                    }
                }
                catch(IndexOutOfBoundsException e) {}
            }
        }

        return false;
    }
}

