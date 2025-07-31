package expression.exceptions;

import expression.BinaryOperation;
import expression.Expressions;

public class Gcd extends BinaryOperation {
    Gcd(final Expressions left, final Expressions right) {
        super(left, right);
    }

    private static int calculateGcd(int x, int y) {
        return x == 0 ? y : gcd(y % x, x);
    }

    private static int abs(int x) {
        return x < 0 ? -x : x;
    }

    public static int gcd(final int x, final int y) {
        return calculateGcd(abs(x), abs(y));
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        return gcd(leftResult, rightResult);
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
        return "gcd";
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
