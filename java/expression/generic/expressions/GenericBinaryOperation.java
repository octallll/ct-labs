package expression.generic.expressions;

import expression.BinaryMethods;
import expression.ToMiniString;
import expression.exceptions.ParserException;
import expression.generic.operationsType.Operator;

import java.util.Objects;

public abstract class GenericBinaryOperation<T> implements Expressions<T>, BinaryMethods {
    private final Expressions<T> leftOperation;
    private final Expressions<T> rightOperation;
    protected final Operator<T> operator;

    public GenericBinaryOperation(final Expressions<T> leftOperation, final Expressions<T> rightOperation, final Operator<T> operator) {
        this.leftOperation = leftOperation;
        this.rightOperation = rightOperation;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(" + leftOperation + " " + getOperationRender() + " " + rightOperation + ")";
    }

    @Override
    public String toMiniString() {
        final boolean needBracketsToLeft = (leftOperation instanceof final GenericBinaryOperation<T> operation &&
                operation.getPriority() > getPriority());
        final boolean needBracketsToRight = (rightOperation instanceof final GenericBinaryOperation<T> operation && checkOperationsForBrackets(operation));

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

    private boolean checkOperationsForBrackets(final GenericBinaryOperation<T> operation) {
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

    public abstract T calculateResultOfOperation(final T leftResult, final T rightResult);

    @Override
    public boolean equals(final Object maybeOperation) {
        return maybeOperation instanceof final GenericBinaryOperation<?> operation
                && operation.getOperationRender().equals(getOperationRender())
                && operation.leftOperation.equals(leftOperation)
                && operation.rightOperation.equals(rightOperation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftOperation, rightOperation, getOperationRender());
    }

    @Override
    public T evaluate(final T x, final T y, final T z) throws ParserException {
        return calculateResultOfOperation(leftOperation.evaluate(x, y, z), rightOperation.evaluate(x, y, z));
    }
}
