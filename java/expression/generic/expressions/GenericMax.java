package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericMax<T> extends GenericBinaryOperation<T> {
    public GenericMax(Expressions<T> leftOperation, Expressions<T> rightOperation, Operator<T> operator) {
        super(leftOperation, rightOperation, operator);
    }

    @Override
    public T calculateResultOfOperation(T leftResult, T rightResult) {
        return operator.max(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getOperationRender() {
        return ">?";
    }

    @Override
    public boolean needBracketsAfter() {
        return true;
    }

    @Override
    public boolean needBracketsBefore() {
        return false;
    }
}
