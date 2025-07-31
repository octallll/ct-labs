package expression;

public class Divide extends BinaryOperation{
    public Divide(final Expressions left, final Expressions right) {
        super(left, right);
    }

    @Override
    public int calculateResultOfOperation(final int leftResult, final int rightResult) {
        return leftResult / rightResult;
    }

    @Override
    public double calculateResultOfOperation(final double leftResult, final double rightResult) {
        return leftResult / rightResult;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public String getOperationRender() {
        return "/";
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
