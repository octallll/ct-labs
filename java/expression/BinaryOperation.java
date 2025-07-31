package expression;

import java.util.Map;
import java.util.Objects;

public abstract class BinaryOperation implements Expressions, BinaryMethods {
    private final Expressions leftOperation;
    private final Expressions rightOperation;

    public BinaryOperation(final Expressions leftOperation, final Expressions rightOperation) {
        this.leftOperation = leftOperation;
        this.rightOperation = rightOperation;
    }

    @Override
    public String toString() {
        return "(" + leftOperation + " " + getOperationRender() + " " + rightOperation + ")";
    }

    @Override
    public String toMiniString() {
        final boolean needBracketsToLeft = (leftOperation instanceof final BinaryOperation operation &&
                operation.getPriority() > getPriority());
        final boolean needBracketsToRight = (rightOperation instanceof final BinaryOperation operation && checkOperationsForBrackets(operation));

        return toMiniString(leftOperation, needBracketsToLeft) +
                " " + getOperationRender() + " " +
                toMiniString(rightOperation, needBracketsToRight);
    }

    private static String toMiniString(final ToMiniString op, final boolean needBracketsToLeft) {
        return toMiniString(op.toMiniString(), needBracketsToLeft);
    }

    private static String toMiniString(final String inner, final boolean needBrackets) {
        return needBrackets ? "(" + inner + ")" : inner;
    }

    private boolean checkOperationsForBrackets(final BinaryOperation operation) {
        if (getPriority() < operation.getPriority()) {
            return true;
        }

        if (getPriority() == operation.getPriority()) {
            if (!needBracketsAfter()) {
                return false;
            }

            return !getOperationRender().equals(operation.getOperationRender()) || operation.needBracketsBefore();
        }

        return false;
    }

    public abstract int calculateResultOfOperation(final int leftResult, final int rightResult);

    public int evaluate(final int value) {
        return calculateResultOfOperation(leftOperation.evaluate(value), rightOperation.evaluate(value));
    }

    @Override
    public boolean equals(final Object maybeOperation) {
        return maybeOperation instanceof final BinaryOperation operation
                && operation.getOperationRender().equals(getOperationRender())
                && operation.leftOperation.equals(leftOperation)
                && operation.rightOperation.equals(rightOperation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperation, rightOperation, getOperationRender());
    }

    public abstract double calculateResultOfOperation(final double leftResult, final double rightResult);

    @Override
    public double evaluateD(final Map<String, Double> variables) {
        return calculateResultOfOperation(leftOperation.evaluateD(variables), rightOperation.evaluateD(variables));
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return calculateResultOfOperation(leftOperation.evaluate(x, y, z), rightOperation.evaluate(x, y, z));
    }
}
