package expression.exceptions;

import expression.BinaryOperation;
import expression.Expressions;

public class Lcm extends BinaryOperation {
    Lcm(final Expressions left, final Expressions right) {
        super(left, right);
    }

    private int lcm(int x, int y) {
        int gcd = Gcd.gcd(x, y);

        if (gcd == 0) {
            return 0;
        }

        return CheckedMultiply.multiply(x / gcd, y);
    }

    @Override
    public int calculateResultOfOperation(int leftResult, int rightResult) {
        return lcm(leftResult, rightResult);
    }

    @Override
    public double calculateResultOfOperation(double leftResult, double rightResult) {
        throw new IllegalStateException("Operations in double doesn't supported");
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getOperationRender() {
        return "lcm";
    }

    @Override
    public boolean needBracketsAfter() {
        return true;
    }

    @Override
    public boolean needBracketsBefore() {
        return false;
    }
}
