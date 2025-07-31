package expression.generic.operationsType;

import java.util.Objects;

public class OperationsDouble implements Operator<Double> {
    @Override
    public Double add(Double leftResult, Double rightResult) {
        return leftResult + rightResult;
    }

    @Override
    public Double subtract(Double leftResult, Double rightResult) {
        return leftResult - rightResult;
    }

    @Override
    public Double multiply(Double leftResult, Double rightResult) {
        return leftResult * rightResult;
    }

    @Override
    public Double divide(Double leftResult, Double rightResult) {
        return leftResult / rightResult;
    }

    @Override
    public Double negate(Double result) {
        return -result;
    }

    @Override
    public Double cast(int value) {
        return (double) value;
    }

    @Override
    public Double compareLess(Double leftResult, Double rightResult) {
        return leftResult.compareTo(rightResult) < 0 ? 1.0 : 0.0;
    }

    @Override
    public Double compareLessOrEqual(Double leftResult, Double rightResult) {
        return leftResult.compareTo(rightResult) <= 0 ? 1.0 : 0.0;
    }

    @Override
    public Double compareGreater(Double leftResult, Double rightResult) {
        return leftResult.compareTo(rightResult) > 0 ? 1.0 : 0.0;
    }

    @Override
    public Double compareGreaterOrEqual(Double leftResult, Double rightResult) {
        return leftResult.compareTo(rightResult) >= 0 ? 1.0 : 0.0;
    }

    @Override
    public Double compareEqual(Double leftResult, Double rightResult) {
        return Objects.equals(leftResult, rightResult) ? 1.0 : 0.0;
    }

    @Override
    public Double compareNotEqual(Double leftResult, Double rightResult) {
        return !Objects.equals(leftResult, rightResult) ? 1.0 : 0.0;
    }

    private Double zeroOut(Double value) {
        return value == -0.0 ? 0.0 : value;
    }

    @Override
    public Double max(Double leftResult, Double rightResult) {
        if (leftResult.isNaN()) {
            return leftResult;
        }

        if (rightResult.isNaN()) {
            return rightResult;
        }

        return leftResult.compareTo(rightResult) <= 0 ? rightResult : leftResult;
    }

    @Override
    public Double min(Double leftResult, Double rightResult) {
        if (leftResult.isNaN()) {
            return leftResult;
        }

        if (rightResult.isNaN()) {
            return rightResult;
        }

        return leftResult.compareTo(rightResult) <= 0 ? leftResult : rightResult;
    }
}
