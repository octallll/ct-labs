package expression.exceptions;

public class UnknownOperationException extends ParserException {
    public UnknownOperationException(String message, int pos) {
        super(message + " at position " + pos);
    }
}
