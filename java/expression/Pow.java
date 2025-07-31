package expression;

public class Pow extends BinaryOperation {
    public Pow(final Expressions left, final Expressions right) {
        super(left, right);
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        if (rightResult <= 0) {
            return 1;
        }

        if (leftResult == 0) {
            return 0;
        }

        int result = calculateResultOfOperation(leftResult, rightResult / 2);
        result = result * result;

        if (rightResult % 2 == 1) {
            result = result * leftResult;
        }

        return result;
    }

    @Override
    public double calculateResultOfOperation(final double leftResult, final double rightResult) {
        throw new IllegalStateException("Calculate Pow in double doesn't supported");
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getOperationRender() {
        return "**";
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
