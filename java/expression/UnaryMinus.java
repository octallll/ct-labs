package expression;

import java.util.Map;

public class UnaryMinus extends UnaryOperation {
    public UnaryMinus(final Expressions expression) {
        super(expression);
    }

    @Override
    public double evaluateD(final Map<String, Double> variables) {
        return -1 * expression.evaluateD(variables);
    }

    @Override
    public int calculateResultOfOperation(final int result) {
        return -1 * result;
    }

    @Override
    public String toString() {
        return "-(" + expression.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (expression instanceof BinaryOperation) {
            return "-(" + expression.toMiniString() + ")";
        } else {
            return  "- " + expression.toMiniString();
        }
    }
}
