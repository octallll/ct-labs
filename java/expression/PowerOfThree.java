package expression;

import java.util.Map;

public class PowerOfThree extends UnaryOperation {
    public PowerOfThree(final Expressions expression) {
        super(expression);
    }

    @Override
    public double evaluateD(final Map<String, Double> variables) {
        throw new IllegalStateException("Operations in double doesn't supported");
    }

    @Override
    public int evaluate(final int x) {
        int result = expression.evaluate(x);

        return calculateResultOfOperation(result);
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        int result = expression.evaluate(x, y, z);

        return calculateResultOfOperation(result);
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + ")³";
    }

    public String toMiniString() {
        if (expression instanceof BinaryOperation || expression instanceof UnaryMinus) {
            return "(" + expression.toMiniString() + ")³";
        }
        return expression.toMiniString() + "³";
    }

    @Override
    public int calculateResultOfOperation(int x) {
        return x * x * x;
    }
}
