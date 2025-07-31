package expression.generic;

import expression.exceptions.ParserException;
import expression.generic.expressions.Expressions;
import expression.generic.operationsType.*;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        Object[][][] matrix = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        Operator<?> operator = switch (mode) {
            case "i" -> new OperationsOverflowInt() ;
            case "d" -> new OperationsDouble();
            case "bi" -> new OperationsBigInteger();
            case "u" -> new OperationsInt();
            case "ifix" -> new OperationsFixedPoint();
            default -> throw new IllegalStateException("No such mode: " + mode);
        };

    //    System.err.println(mode);

        calculateMatrix(operator, matrix, expression, x1, x2, y1, y2, z1, z2);

        return matrix;
    }

    private <T> void calculateMatrix(Operator<T> operator, Object[][][] matrix, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParserException {
        ExpressionParser<T> parser = new ExpressionParser<>();

        Expressions<T> exp = parser.parse(expression, operator);

     //   System.err.println(x1 + " " + x2 + " " + y1 + " " + y2 + " " + z1 + " " + z2);

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    try {
                        matrix[x - x1][y - y1][z - z1] = exp.evaluate(operator.cast(x), operator.cast(y), operator.cast(z));
                    } catch (ArithmeticException e) {
                        matrix[x - x1][y - y1][z - z1] = null;
                    }
                }
            }
        }
    }
}
