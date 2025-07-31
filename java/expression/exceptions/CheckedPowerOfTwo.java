package expression.exceptions;

import expression.Expressions;
import expression.PowerOfTwo;

public class CheckedPowerOfTwo extends PowerOfTwo {
    CheckedPowerOfTwo(Expressions expression) {
        super(expression);
    }

    @Override
    public int calculateResultOfOperation(int x) {
        return CheckedPower.calculate(x, 2);
    }
}
