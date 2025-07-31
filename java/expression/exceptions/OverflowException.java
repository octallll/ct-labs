package expression.exceptions;

public class OverflowException extends UncheckedParserException {
    public OverflowException(String message) {
        super(message);
    }

    public OverflowException(int left, int right, String operation) {
        super("Overflow in calculating " + left + " " + operation + " " + right);
    }
}
