package expression.exceptions;

public class UnexpectedCharacterException extends ParserException {
    public UnexpectedCharacterException(String message, int pos) {
        super(message + " at position " + pos);
    }

    public UnexpectedCharacterException(char expected, char actual, int pos) {
        super("Unexpected character expected: " + (expected == '\0' ? "END" : expected) + " actual: " + actual + " at position " + pos);
    }
}
