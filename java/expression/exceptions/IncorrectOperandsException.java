package expression.exceptions;

public class IncorrectOperandsException extends RuntimeException {
    IncorrectOperandsException(int left, int right, String operation, int pos) {
        super("Incorrect operands in: " + left + " " + operation + " " + right + " at position " + pos);
    }

    IncorrectOperandsException(int x, String operand) {
        super("Incorrect operand in operation: " + operand + ": " + x);
    }
}
