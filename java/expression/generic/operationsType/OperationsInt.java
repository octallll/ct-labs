package expression.generic.operationsType;

import java.util.Objects;

public class OperationsInt implements Operator<Integer>{
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
        return leftResult * rightResult;
    }

    @Override
    public Integer divide(Integer leftResult, Integer rightResult) {
        return leftResult / rightResult;
    }

    @Override
    public Integer negate(Integer result) {
        return -result;
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
        return leftResult > rightResult ? leftResult : rightResult;
    }

    @Override
    public Integer min(Integer leftResult, Integer rightResult) {
        return leftResult < rightResult ? leftResult : rightResult;
    }
}
