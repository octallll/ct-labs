package expression.exceptions;

import expression.BinaryOperation;
import expression.Expressions;
import expression.UnaryOperation;

import java.util.Map;

public abstract class Tetration extends UnaryOperation implements TetrationMethods {
    Tetration(Expressions expression) {
        super(expression);
    }

    @Override
    public double evaluateD(Map<String, Double> variables) {
        throw new IllegalStateException("Operations in double doesn't supported");
    }

    @Override
    public int evaluate(int x) {
        return calculateResultOfOperation(expression.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calculateResultOfOperation(expression.evaluate(x, y, z));
    }

    @Override
    public int calculateResultOfOperation(int x) {
        if (x <= 0) {
            throw new IncorrectOperandsException(x, "³");
        }

        if (x > 9) {
            throw new OverflowException("Overflow in tetration with " + x);
        }

        int result = 1;

        for (int step = 1; step <= getNumberOfTetration(); step++) {
            result = CheckedPower.calculate(x, result);
        }

        return result;
    }

    @Override
    public String toString() {
        return getSymbol() + "(" + expression.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (expression instanceof BinaryOperation) {
            return getSymbol() + "(" + expression.toMiniString() + ")";
        }
        return getSymbol() + expression.toMiniString();
    }
}
