package expression.generic.expressions;

public class GenericConst<T> implements Expressions<T> {
    private final T value;

    public GenericConst(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    T getConst() {
        return value;
    }

    @Override
    public boolean equals(final Object maybe) {
        return maybe instanceof final GenericConst<?> otherConst && otherConst.getConst().equals(getConst());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public T evaluate(final T x, final T y, final T z) {
        return value;
    }
}
