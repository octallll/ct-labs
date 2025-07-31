package expression.generic.expressions;

import expression.ToMiniString;
import expression.exceptions.ParserException;

public interface GenericTripleExpression<T> extends ToMiniString {
    T evaluate(T x, T y, T z) throws ParserException;
}
