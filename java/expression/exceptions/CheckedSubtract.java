package expression.exceptions;

import expression.Subtract;
import expression.Expressions;

public class CheckedSubtract extends Subtract {
    public CheckedSubtract(final Expressions left, final Expressions right) {
        super(left, right);
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, int rightResult) {
        if (rightResult == Integer.MIN_VALUE) {
            if (leftResult >= 0) {
                throw new OverflowException(leftResult, rightResult, "-");
            } else {
                return leftResult - rightResult;
            }
        }

        rightResult *= -1;
        int result = leftResult + rightResult;

        if ((leftResult > 0 && rightResult > 0 && result <= 0) || (leftResult < 0 && rightResult < 0 && result >= 0)) {
            throw new OverflowException(leftResult, rightResult, "-");
        }

        return result;
    }
}
