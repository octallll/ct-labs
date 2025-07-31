package expression;

public class Logarithm extends BinaryOperation{
    public Logarithm(final Expressions left, final Expressions right) {
        super(left, right);
    }


    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        int result = 0;

        if (rightResult > 1 && leftResult >= rightResult) {
            long currentPower = rightResult;

            while (currentPower <= (long) leftResult) {
                result++;
                currentPower *= rightResult;
            }
        } else if (leftResult == 0 && rightResult >= 1) {
            result = Integer.MIN_VALUE;
        } else if (rightResult == 1 && leftResult != 1 && leftResult >= 0) {
            result = Integer.MAX_VALUE;
        }

        return result;
    }

    @Override
    public double calculateResultOfOperation(final double leftResult, final double rightResult) {
        throw new IllegalStateException("Calculate in double in Logarithm doesn't supported");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getOperationRender() {
        return "//";
    }

    @Override
    public boolean needBracketsAfter() {
        return true;
    }

    @Override
    public boolean needBracketsBefore() {
        return true;
    }
}
