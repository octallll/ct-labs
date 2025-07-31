package expression;

public class Main {
    public static void main(String[] args) {
        Expressions exp = new Add(
                new Subtract(
                        new Multiply(new Variable("x"), new Variable("x")),
                        new Multiply(new Variable("x"), new Const(2))
                ),
                new Const(1)
        );

        System.out.println(exp.evaluate(Integer.parseInt(args[0])));
    }
}
