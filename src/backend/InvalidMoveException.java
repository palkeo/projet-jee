package backend;
@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {
    public static final long serialVersionUID = 2243411481362L;

    public InvalidMoveException(String message) {
        super(message);
    }
}

