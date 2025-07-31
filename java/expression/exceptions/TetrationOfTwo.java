package expression.exceptions;

import expression.*;

import java.util.Map;

public class TetrationOfTwo extends Tetration {
    TetrationOfTwo(final Expressions expression) {
        super(expression);
    }


    @Override
    public String getSymbol() {
        return "²";
    }

    @Override
    public int getNumberOfTetration() {
        return 2;
    }
}
