package expression.exceptions;

public class NumberParseException extends ParserException {
    public NumberParseException(String message, int pos) {
        super(message + "at position " + pos);
    }
}
