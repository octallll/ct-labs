package expression.exceptions;

public class CheckedPower {
    public static int calculate(int a, int p) {
        int result = 1;

        for (int step = 1; step <= p; step++) {
            result = CheckedMultiply.multiply(result, a);
        }

        return result;
    }
}
