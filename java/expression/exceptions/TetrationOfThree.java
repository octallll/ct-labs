package expression.exceptions;

import expression.Expressions;

public class TetrationOfThree extends Tetration {
    TetrationOfThree(Expressions expression) {
        super(expression);
    }


    @Override
    public String getSymbol() {
        return "³";
    }

    @Override
    public int getNumberOfTetration() {
        return 3;
    }
}
