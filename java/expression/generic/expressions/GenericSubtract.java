package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericSubtract<T> extends GenericBinaryOperation<T> {
    public GenericSubtract(final Expressions<T> left, final Expressions<T> right, final Operator<T> operator) {
        super(left, right, operator);
    }

    @Override
    public T calculateResultOfOperation(final T leftResult, final T rightResult) {
        return operator.subtract(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getOperationRender() {
        return "-";
    }

    @Override
    public boolean needBracketsAfter() {
        return true;
    }

    @Override
    public boolean needBracketsBefore() {
        return true;
    }
}
