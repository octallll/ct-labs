package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericCompareNotEqual<T> extends GenericBinaryOperation<T> {
    public GenericCompareNotEqual(Expressions<T> leftOperation, Expressions<T> rightOperation, Operator<T> operator) {
        super(leftOperation, rightOperation, operator);
    }

    @Override
    public T calculateResultOfOperation(T leftResult, T rightResult) {
        return operator.compareNotEqual(leftResult, rightResult);
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getOperationRender() {
        return "!=";
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
