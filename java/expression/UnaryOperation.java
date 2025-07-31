package expression;

public abstract class UnaryOperation implements Expressions, UnaryMethods {
    public Expressions expression;

    public UnaryOperation(final Expressions expression) {
        this.expression = expression;
    }

    @Override
    public int evaluate(final int x) {
        return calculateResultOfOperation(expression.evaluate(x));
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return calculateResultOfOperation(expression.evaluate(x, y, z));
    }
}
