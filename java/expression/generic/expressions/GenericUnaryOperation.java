package expression.generic.expressions;

import expression.exceptions.ParserException;
import expression.generic.operationsType.Operator;

public abstract class GenericUnaryOperation<T> implements Expressions<T> {
    protected Expressions<T> expression;
    protected Operator<T> operator;

    public GenericUnaryOperation(final Expressions<T> expression, final Operator<T> operator) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public T evaluate(final T x, final T y, final T z) throws ParserException {
        return calculateResultOfOperation(expression.evaluate(x, y, z));
    }

    public abstract T calculateResultOfOperation(final T result);
}
