package expression.generic.expressions;

import expression.generic.expressions.Expressions;
import expression.generic.operationsType.Operator;

public class GenericDivide<T> extends GenericBinaryOperation<T> {
    public GenericDivide(final Expressions<T> left, final Expressions<T> right, final Operator<T> operator) {
        super(left, right, operator);
    }

    @Override
    public T calculateResultOfOperation(final T leftResult, final T rightResult) {
        return operator.divide(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getOperationRender() {
        return "/";
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
