package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericCompareGreater<T> extends GenericBinaryOperation<T> {
    public GenericCompareGreater(Expressions<T> leftOperation, Expressions<T> rightOperation, Operator<T> operator) {
        super(leftOperation, rightOperation, operator);
    }

    @Override
    public T calculateResultOfOperation(T leftResult, T rightResult) {
        return operator.compareGreater(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getOperationRender() {
        return ">";
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
