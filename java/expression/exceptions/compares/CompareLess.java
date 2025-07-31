package expression.exceptions.compares;

import expression.BinaryOperation;
import expression.Expressions;

public class CompareLess extends BinaryOperation {
    public CompareLess(Expressions leftOperation, Expressions rightOperation) {
        super(leftOperation, rightOperation);
    }

    @Override
    public int calculateResultOfOperation(int leftResult, int rightResult) {
        return (leftResult < rightResult ? 1 : 0);
    }

    @Override
    public double calculateResultOfOperation(double leftResult, double rightResult) {
        throw new IllegalStateException("Operations in double doesn't supported");
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getOperationRender() {
        return "<";
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
