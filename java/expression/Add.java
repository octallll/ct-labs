package expression;

public class Add extends BinaryOperation {
    public Add(final Expressions left, final Expressions right) {
        super(left, right);
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        return leftResult + rightResult;
    }

    @Override
    public double calculateResultOfOperation(final double leftResult, final double rightResult) {
        return leftResult + rightResult;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getOperationRender() {
        return "+";
    }

    @Override
    public boolean needBracketsAfter() {
        return false;
    }

    @Override
    public boolean needBracketsBefore() {
        return true;
    }
}
