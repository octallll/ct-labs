package expression.generic.operationsType;

import java.math.BigInteger;

public class OperationsBigInteger implements Operator<BigInteger> {
    @Override
    public BigInteger add(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.add(rightResult);
    }

    @Override
    public BigInteger subtract(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.subtract(rightResult);
    }

    @Override
    public BigInteger multiply(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.multiply(rightResult);
    }

    @Override
    public BigInteger divide(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.divide(rightResult);
    }

    @Override
    public BigInteger negate(BigInteger result) {
        return result.negate();
    }

    @Override
    public BigInteger cast(final int value) {
        return BigInteger.valueOf(value);
    }

    @Override
    public BigInteger compareLess(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) < 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger compareLessOrEqual(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) <= 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger compareGreater(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) > 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger compareGreaterOrEqual(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) >= 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger compareEqual(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) == 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger compareNotEqual(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) != 0 ? BigInteger.ONE : BigInteger.ZERO;
    }

    @Override
    public BigInteger max(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) > 0 ? leftResult : rightResult;
    }

    @Override
    public BigInteger min(BigInteger leftResult, BigInteger rightResult) {
        return leftResult.compareTo(rightResult) < 0 ? leftResult : rightResult;
    }
}
