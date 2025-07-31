package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericAdd<T> extends GenericBinaryOperation<T> {
    public GenericAdd(final Expressions<T> left, final Expressions<T> right, final Operator<T> operator) {
        super(left, right, operator);
    }

    @Override
    public T calculateResultOfOperation(final T leftResult, final T rightResult) {
        return operator.add(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getOperationRender() {
        return "+";
    }

    @Override
    public boolean needBracketsAfter() {
        return false;
    }

    @Override
    public boolean needBracketsBefore() {
        return true;
    }
}
