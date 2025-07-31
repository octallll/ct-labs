package expression.exceptions;

import expression.Divide;
import expression.Expressions;

public class CheckedDivide extends Divide {
    public CheckedDivide(final Expressions left, final Expressions right) {
        super(left, right);
    }

    public static int divide(final int leftResult, final int rightResult) {
        if (rightResult == 0) {
            throw new DividingByZeroException("Divide by zero: " + leftResult + " / " + rightResult);
        }

        if (leftResult == Integer.MIN_VALUE && rightResult == -1) {
            throw new OverflowException(leftResult, rightResult, "/");
        }

        return leftResult / rightResult;
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        return divide(leftResult, rightResult);
    }
}
