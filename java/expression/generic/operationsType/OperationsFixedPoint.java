package expression.generic.operationsType;

import java.util.Objects;

public class OperationsFixedPoint implements Operator<Integer>{
    @Override
    public Integer add(Integer leftResult, Integer rightResult) {
        return leftResult + rightResult;
    }

    @Override
    public Integer subtract(Integer leftResult, Integer rightResult) {
        return leftResult - rightResult;
    }

    @Override
    public Integer multiply(Integer leftResult, Integer rightResult) {
        long result = (long) leftResult * rightResult;

        return (int) (result >> 16);
    }

    @Override
    public Integer divide(Integer leftResult, Integer rightResult) {
        long result = ((long) leftResult << 16) / rightResult;

        return (int) result;
    }

    @Override
    public Integer negate(Integer result) {
        return -result;
    }

    @Override
    public Integer cast(int value) {
        int result = value;

        result = result * (1 << 16);

        return result;
    }

    @Override
    public Integer compareLess(Integer leftResult, Integer rightResult) {
        return leftResult < rightResult ? 1 << 16 : 0;
    }

    @Override
    public Integer compareLessOrEqual(Integer leftResult, Integer rightResult) {
        return leftResult <= rightResult ? 1 << 16 : 0;
    }

    @Override
    public Integer compareGreater(Integer leftResult, Integer rightResult) {
        return leftResult > rightResult ? 1 << 16 : 0;
    }

    @Override
    public Integer compareGreaterOrEqual(Integer leftResult, Integer rightResult) {
        return leftResult >= rightResult ? 1 << 16 : 0;
    }

    @Override
    public Integer compareEqual(Integer leftResult, Integer rightResult) {
        return Objects.equals(leftResult, rightResult) ? 1 << 16 : 0;
    }

    @Override
    public Integer compareNotEqual(Integer leftResult, Integer rightResult) {
        return !Objects.equals(leftResult, rightResult) ? 1 << 16 : 0;
    }

    @Override
    public Integer max(Integer leftResult, Integer rightResult) {
        return leftResult > rightResult ? leftResult : rightResult;
    }

    @Override
    public Integer min(Integer leftResult, Integer rightResult) {
        return leftResult < rightResult ? leftResult : rightResult;
    }
}
