package expression.generic.expressions;

public class GenericVariable<T> implements Expressions<T> {
    private final String name;

    public GenericVariable(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object maybe) {
        if (maybe instanceof GenericVariable<?> variable) {
            return variable.getName().equals(getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public T evaluate(final T x, final T y, final T z) {
        return switch (name.charAt(name.length() - 1)) {
            case 'x' -> x;
            case 'y' -> y;
            case 'z' -> z;
            default -> throw new IllegalStateException("Incorrect variable name: " + name);
        };
    }
}
