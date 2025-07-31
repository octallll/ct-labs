package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericCompareEqual<T> extends GenericBinaryOperation<T> {
    public GenericCompareEqual(Expressions<T> leftOperation, Expressions<T> rightOperation, Operator<T> operator) {
        super(leftOperation, rightOperation, operator);
    }

    @Override
    public T calculateResultOfOperation(T leftResult, T rightResult) {
        return operator.compareEqual(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getOperationRender() {
        return "==";
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
