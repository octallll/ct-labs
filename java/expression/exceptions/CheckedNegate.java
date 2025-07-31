package expression.exceptions;

import expression.Expressions;
import expression.UnaryMinus;

public class CheckedNegate extends UnaryMinus {
    CheckedNegate(Expressions expression) {
        super(expression);
    }

    @Override
    public int calculateResultOfOperation(final int result) {
        if (result == Integer.MIN_VALUE) {
            throw new OverflowException("Overflow in negate to: " + result);
        }

        return -1 * result;
    }
}
