package expression.generic.expressions;

public interface Expressions<T> extends GenericTripleExpression<T> {
    boolean equals(Object maybe);
}
