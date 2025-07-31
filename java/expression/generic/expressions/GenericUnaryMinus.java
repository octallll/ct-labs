package expression.generic.expressions;

import expression.generic.operationsType.Operator;

public class GenericUnaryMinus<T> extends GenericUnaryOperation<T> {
    public GenericUnaryMinus(final Expressions<T> expression, final Operator<T> operator) {
        super(expression, operator);
    }

    @Override
    public T calculateResultOfOperation(final T result) {
        return operator.negate(result);
    }

    @Override
    public String toString() {
        return "-(" + expression.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (expression instanceof GenericBinaryOperation) {
            return "-(" + expression.toMiniString() + ")";
        } else {
            return  "- " + expression.toMiniString();
        }
    }
}
