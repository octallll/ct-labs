package expression;

import java.util.Map;

public class Const implements Expressions {
    private final Number value;

    public Const(final int value) {
        this.value = value;
    }

    public Const(final double valueD) {
        this.value = valueD;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    @Override
    public int evaluate(final int x) {
        return value.intValue();
    }

    Number getConst() {
        return value;
    }

    @Override
    public boolean equals(final Object maybe) {
        return maybe instanceof final Const otherConst && otherConst.getConst().equals(getConst());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public double evaluateD(final Map<String, Double> variables) {
        return value.doubleValue();
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return value.intValue();
    }
}
