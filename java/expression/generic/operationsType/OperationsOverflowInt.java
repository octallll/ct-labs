package expression.generic.operationsType;

import expression.exceptions.DividingByZeroException;
import expression.exceptions.OverflowException;

import java.util.Objects;

public class OperationsOverflowInt implements Operator<Integer> {
    @Override
    public Integer add(Integer leftResult, Integer rightResult) {
        int result = leftResult + rightResult;

        if ((leftResult > 0 && rightResult > 0 && result <= 0) || (leftResult < 0 && rightResult < 0 && result >= 0)) {
            throw new OverflowException(leftResult, rightResult, "+");
        }

        return result;
    }

    @Override
    public Integer subtract(Integer leftResult, Integer rightResult) {
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

    @Override
    public Integer multiply(Integer leftResult, Integer rightResult) {
        int result = leftResult * rightResult;

        if ((leftResult != 0 && result / leftResult != rightResult) || (rightResult != 0 && result / rightResult != leftResult)) {
            throw new OverflowException(leftResult, rightResult, "*");
        }

        return result;
    }

    @Override
    public Integer divide(Integer leftResult, Integer rightResult) {
        if (rightResult == 0) {
            throw new DividingByZeroException("Divide by zero: " + leftResult + " / " + rightResult);
        }

        if (leftResult == Integer.MIN_VALUE && rightResult == -1) {
            throw new OverflowException(leftResult, rightResult, "/");
        }

        return leftResult / rightResult;
    }

    @Override
    public Integer negate(Integer result) {
        if (result == Integer.MIN_VALUE) {
            throw new OverflowException("Overflow in negate to: " + result);
        }

        return -1 * result;
    }

    @Override
    public Integer cast(int value) {
        return value;
    }

    @Override
    public Integer compareLess(Integer leftResult, Integer rightResult) {
        return leftResult < rightResult ? 1 : 0;
    }

    @Override
    public Integer compareLessOrEqual(Integer leftResult, Integer rightResult) {
        return leftResult <= rightResult ? 1 : 0;
    }

    @Override
    public Integer compareGreater(Integer leftResult, Integer rightResult) {
        return leftResult > rightResult ? 1 : 0;
    }

    @Override
    public Integer compareGreaterOrEqual(Integer leftResult, Integer rightResult) {
        return leftResult >= rightResult ? 1 : 0;
    }

    @Override
    public Integer compareEqual(Integer leftResult, Integer rightResult) {
        return Objects.equals(leftResult, rightResult) ? 1 : 0;
    }

    @Override
    public Integer compareNotEqual(Integer leftResult, Integer rightResult) {
        return !Objects.equals(leftResult, rightResult) ? 1 : 0;
    }

    @Override
    public Integer max(Integer leftResult, Integer rightResult) {
        return Math.max(leftResult, rightResult);
    }

    @Override
    public Integer min(Integer leftResult, Integer rightResult) {
        return Math.min(leftResult, rightResult);
    }
}
