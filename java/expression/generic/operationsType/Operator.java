package expression.generic.operationsType;

public interface Operator<T> {
    T add(final T leftResult, final T rightResult);
    T subtract(final T leftResult, final T rightResult);
    T multiply(final T leftResult, final T rightResult);
    T divide(final T leftResult, final T rightResult);
    T negate(final T result);
    T cast(final int value);
    T compareLess(final T leftResult, final T rightResult);
    T compareLessOrEqual(final T leftResult, final T rightResult);
    T compareGreater(final T leftResult, final T rightResult);
    T compareGreaterOrEqual(final T leftResult, final T rightResult);
    T compareEqual(final T leftResult, final T rightResult);
    T compareNotEqual(final T leftResult, final T rightResult);
    T max(final T leftResult, final T rightResult);
    T min(final T leftResult, final T rightResult);
}
