package expression.parser;

import java.util.HashMap;
import java.util.Map;
import expression.*;

public class ExpressionParser extends BaseParser implements TripleParser {
    static Map<Integer, String[]> priorityToOperation = new HashMap<>();

    static {
        priorityToOperation.put(1, new String[]{"+", "-"});
        priorityToOperation.put(2, new String[]{"*", "/"});
        priorityToOperation.put(3, new String[]{"**", "//"});
    }

    Expressions makeNewClass(final String operationRender, final Expressions left, final Expressions right) {
        return switch (operationRender) {
            case "+" -> new Add(left, right);
            case "-" -> new Subtract(left, right);
            case "*" -> new Multiply(left, right);
            case "/" -> new Divide(left, right);
            case "**" -> new Pow(left, right);
            case "//" -> new Logarithm(left, right);
            default -> throw new IllegalStateException("No such operation");
        };
    }

    @Override
    public Expressions parse(final String expression) {
        source = new StringSource(expression);
        take();

        return parseFirstPriority();
    }

    private Expressions parseFirstPriority() {
        Expressions result = parseSecondPriority();

        skipWhitespace();

        boolean taken = true;

        while (taken) {
            taken = false;

            for (String operationRender : priorityToOperation.get(1)) {
                if (take(operationRender)) {
                    result = makeNewClass(operationRender, result, parseSecondPriority());
                    taken = true;
                }
            }

            skipWhitespace();
        }

        return result;
    }

    private Expressions parseSecondPriority() {
        Expressions result = parseThirdPriority();

        skipWhitespace();

        boolean taken = true;

        while (taken) {
            taken = false;

            for (String operationRender : priorityToOperation.get(2)) {
                if (take(operationRender)) {
                    result = makeNewClass(operationRender, result, parseThirdPriority());
                    taken = true;
                }
            }

            skipWhitespace();
        }

        return result;
    }

    private Expressions parseThirdPriority() {
        Expressions result = parsFourthPriority();

        skipWhitespace();

        boolean taken = true;

        while (taken) {
            taken = false;

            for (String operationRender : priorityToOperation.get(3)) {
                if (take(operationRender)) {
                    result = makeNewClass(operationRender, result, parsFourthPriority());
                    taken = true;
                }
            }

            skipWhitespace();
        }

        return result;
    }

    private Expressions parsFourthPriority() {
        skipWhitespace();

        Expressions result;

        if (take('(')) {
            result = parseFirstPriority();
            skipWhitespace();
            expect(')');
        } else if (take('-')) {
            if (Character.isDigit(getNext())) {
                result = parseConst(true);
            } else {
                result = new UnaryMinus(parsFourthPriority());
            }
        } else if (Character.isDigit(getNext())) {
            result = parseConst(false);
        } else {
            result = parseVariable();
        }

        skipWhitespace();

        while (test('²') || test('³')) {
            if (take('²')) {
                result = new PowerOfTwo(result);
            } else if (take('³')) {
                result = new PowerOfThree(result);
            }
        }

        return result;
    }

    private Expressions parseVariable() {
        StringBuilder name = new StringBuilder();

        while (Character.isAlphabetic(getNext())) {
            name.append(take());
        }

        return new Variable(name.toString());
    }

    private Expressions parseConst(boolean isNegative) {
        StringBuilder value = new StringBuilder();

        if (isNegative) {
            value.append('-');
        }

        if (take('-')) {
            value.append('-');
        }

        if (take('0')) {
            return new Const(0);
        }

        while (Character.isDigit(getNext())) {
            value.append(take());
        }

        return new Const(Integer.parseInt(value.toString()));
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(getNext())) {
            take();
        }
    }
}
