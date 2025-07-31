package expression.exceptions;

public class DividingByZeroException extends UncheckedParserException {
    public DividingByZeroException(String message) {
        super(message);
    }
}
