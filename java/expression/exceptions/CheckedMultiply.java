package expression.exceptions;

import expression.Expressions;
import expression.Multiply;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(final Expressions left, final Expressions right) {
        super(left, right);
    }

    public static int multiply(final int leftResult, final int rightResult) {
        int result = leftResult * rightResult;

        if ((leftResult != 0 && result / leftResult != rightResult) || (rightResult != 0 && result / rightResult != leftResult)) {
            throw new OverflowException(leftResult, rightResult, "*");
        }

        return result;
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        return multiply(leftResult, rightResult);
    }
}
