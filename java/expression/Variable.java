package expression;

import java.util.Map;

public class Variable implements Expressions {
    private final String name;

    public Variable(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int evaluate(final int x) {
        return x;
    }

    String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object maybe) {
        if (maybe instanceof Variable variable) {
            return variable.getName().equals(getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public double evaluateD(final Map<String, Double> variables) {
        Double result = variables.get(name);

        if (result != null) {
            return result;
        } else {
            throw new IllegalStateException("No such variable: " + name);
        }
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return switch (name.charAt(name.length() - 1)) {
            case 'x' -> x;
            case 'y' -> y;
            case 'z' -> z;
            default -> throw new IllegalStateException("Incorrect variable name: " + name);
        };
    }
}
