package expression.exceptions;

import expression.Expressions;
import expression.PowerOfThree;

public class CheckedPowerOfThree extends PowerOfThree {
    CheckedPowerOfThree(Expressions expression) {
        super(expression);
    }

    @Override
    public int calculateResultOfOperation(int x) {
        return CheckedPower.calculate(x, 3);
    }
}
