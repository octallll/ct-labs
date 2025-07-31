package expression.exceptions.compares;

import expression.BinaryOperation;
import expression.Expressions;

public class Min extends BinaryOperation {
    public Min(Expressions leftOperation, Expressions rightOperation) {
        super(leftOperation, rightOperation);
    }

    @Override
    public int calculateResultOfOperation(int leftResult, int rightResult) {
        return Math.min(leftResult, rightResult);
    }

    @Override
    public double calculateResultOfOperation(double leftResult, double rightResult) {
        throw new IllegalStateException("Operations in double don't supported");
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getOperationRender() {
        return "<?";
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
