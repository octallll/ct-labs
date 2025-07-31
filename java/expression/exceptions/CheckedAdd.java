package expression.exceptions;

import expression.Add;
import expression.Expressions;

public class CheckedAdd extends Add {
    public CheckedAdd(final Expressions left, final Expressions right) {
        super(left, right);
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        int result = leftResult + rightResult;

        if ((leftResult > 0 && rightResult > 0 && result <= 0) || (leftResult < 0 && rightResult < 0 && result >= 0)) {
            throw new OverflowException(leftResult, rightResult, "+");
        }

        return result;
    }
}
