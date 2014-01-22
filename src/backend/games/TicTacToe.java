package games;

import backend.*;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.ArrayList;

public class TicTacToe extends AbstractGameEngine implements GameEngine {
    final int size;

    int winner = -1;

    int[] cases = new int[size*size];

    public TicTacToe() {
        this(3);
    }

    public TicTacToe(int size) {
        super(2);
        this.size = size;
        for(int i = 0; i < size*size; ++i) {
            cases[i] = -1;
        }
    }

    @Override
    public JsonNode getState() {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        for(int i = 0; i < size; ++i) {
            ArrayNode jsonCol = JsonNodeFactory.instance.arrayNode();

            for(int j = 0; j < size; ++j) {
                jsonCol.add(cases[i*size+j]);
            }

            result.add(jsonCol);
        }

        return result;
    }

    /** A Tic-Tac-Toe move is [x, y]. */
    @Override
    public boolean play(JsonNode move) throws InvalidMoveException {
        int player = getCurrentPlayer();
        incCurrentTurn();

        Point point = getPoint(move);
        int xy = point.x*size + point.y;
        if(0 <= point.x && point.x < 3 && 0 <= point.y && point.y < 3 && cases.get(xy)) {
            cases[xy] = getCurrentPlayer();

            if(hasWon(x, y)) {
                winner = player;
                return true;
            }
            else {
                return getCurrentTurn() == size*size;
            }
        }
        else {
            throw new InvalidMoveException(
                "Cannot play at [" + x + ", " + y + "]."
            );
        }
    }

    @Override
    public int getScore(int player) {
        return player == winner ? 1 : 0;
    }

    private boolean hasWon(int x, int y) {
        int me = cases[x*size + y];

        boolean won;

        won = true;
        for(int i = 0; i < size; ++i) {
            if(cases[x*size+i] != me) {
                won = false;
                break;
            }
        }
        if(won) return true;

        won = true;
        for(int i = 0; i < size; ++i) {
            if(cases[i*size+y] != me) {
                won = false;
                break;
            }
        }
        if(won) return true;

        if(x == y) {
            won = true;
            for(int i = 0; i < size; ++i) {
                if(cases[i*size+i] != me) {
                    won = false;
                    break;
                }
            }
            if(won) return true;
        }

        if(x+y == size-1) {
            won = true;
            for(int i = 0; i < size; ++i) {
                if(cases[i*size+size-1-i] != me) {
                    won = false;
                    break;
                }
            }
            if(won) return true;
        }

        return false;
    }

    private static Point getPoint(JsonNode move) throws InvalidMoveException {
        JsonNode jsonX = move.get(0);
        JsonNode jsonY = move.get(1);

        if(move.isArray()
        && jsonX != null && jsonY != null
        && jsonX.isInt() && jsonY.isInt()) {
            return new Point(jsonX.getInt(), jsonY.getInt());
        }
        else {
            throw new InvalidMoveException(
                "A Tic-Tac-Toe move shall be [x, y]"
            );
        }
    }

    private class Point {
        public int x, y;
        public Point(int x, int y) { this.x=x; this.y=y; }
    }
}

