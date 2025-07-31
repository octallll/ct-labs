package expression.generic;

import java.util.ArrayList;

import expression.exceptions.*;
import expression.generic.expressions.*;
import expression.generic.operationsType.Operator;
import expression.parser.BaseParser;
import expression.parser.StringSource;

public class ExpressionParser<T> extends BaseParser {
    private final static ArrayList<String[]> PRIORITY = new ArrayList<>();
    private Operator<T> operator;

    static {
        PRIORITY.add(new String[]{"==", "!="});
        PRIORITY.add(new String[]{"<=", ">=", ">", "<"});
        PRIORITY.add(new String[]{"<?", ">?"});
        PRIORITY.add(new String[]{"+", "-"});
        PRIORITY.add(new String[]{"*", "/"});
    }

    Expressions<T> makeNewClass(final String operationRender, final Expressions<T> left, final Expressions<T> right) throws ParserException {
        return switch (operationRender) {
            case "+" -> new GenericAdd<>(left, right, operator);
            case "-" -> new GenericSubtract<>(left, right, operator);
            case "*" -> new GenericMultiply<>(left, right, operator);
            case "/" -> new GenericDivide<>(left, right, operator);
            case "<" -> new GenericCompareLess<>(left, right, operator);
            case "<=" -> new GenericCompareLessOrEqual<>(left, right, operator);
            case ">" -> new GenericCompareGreater<>(left, right, operator);
            case ">=" -> new GenericCompareGreaterOrEqual<>(left, right, operator);
            case "==" -> new GenericCompareEqual<>(left, right, operator);
            case "!=" -> new GenericCompareNotEqual<>(left, right, operator);
            case "<?" -> new GenericMin<>(left, right, operator);
            case ">?" -> new GenericMax<>(left, right, operator);
            default -> throw new UnknownOperationException("Unknown operation: " + operationRender, source.getPos());
        };
    }

    public Expressions<T> parse(final String expression, Operator<T> operator) throws ParserException {
        source = new StringSource(expression);
        take();

        this.operator = operator;

        Expressions<T> result = parsePriority(0);
        isCorrect(END);

        return result;
    }

    private Expressions<T> parsePriority(int priority) throws ParserException {
        if (priority == PRIORITY.size()) {
            return parseUnary();
        }

        Expressions<T> result = parsePriority(priority + 1);

        skipWhitespace();

        boolean taken = true;

        while (taken) {
            taken = false;

            for (String operationRender : PRIORITY.get(priority)) {
                if (take(operationRender)) {
                    result = makeNewClass(operationRender, result, parsePriority(priority + 1));
                    taken = true;

                    break;
                }
            }

            skipWhitespace();
        }

        return result;
    }

    private Expressions<T> parseUnary() throws ParserException {
        skipWhitespace();

        Expressions<T> result;

        if (take('(')) {
            result = parsePriority(0);
            skipWhitespace();
            isCorrect(')');
        } else if (take('{')) {
            result = parsePriority(0);
            skipWhitespace();
            isCorrect('}');
        } else if (take('[')) {
            result = parsePriority(0);
            skipWhitespace();
            isCorrect(']');
        } else if (take('-')) {
            if (Character.isDigit(getNext())) {
                result = parseConst(true);
            } else {
                result = new GenericUnaryMinus<>(parseUnary(), operator);
            }
        } else if (Character.isDigit(getNext())) {
            result = parseConst(false);
        } else if (Character.isAlphabetic(getNext())) {
            result = parseVariable();
        } else {
            throw new UnexpectedCharacterException("Unexpected character isCorrected brackets, minus, digit or letter, actual: " + getNext(), source.getPos());
        }

        skipWhitespace();

        return result;
    }

    private Expressions<T> parseVariable() throws ParserException {
        StringBuilder name = new StringBuilder();

        while (Character.isAlphabetic(getNext())) {
            name.append(take());
        }

        char last = name.charAt(name.length() - 1);

        if (last != 'x' && last != 'y' && last != 'z') {
            throw new UnexpectedCharacterException("Error when parsing variable - corrected are x, y or z in last position, actual: " + last, source.getPos());
        }

        return new GenericVariable<>(name.toString());
    }

    private Expressions<T> parseConst(boolean isNegative) throws ParserException {
        StringBuilder value = new StringBuilder();

        if (isNegative) {
            value.append('-');
        }

        if (take('-')) {
            value.append('-');
        }

        if (take('0')) {
            return new GenericConst<>(operator.cast(0));
        }

        while (Character.isDigit(getNext())) {
            value.append(take());
        }

        if (Character.isAlphabetic(getNext())) {
            throw new UnexpectedCharacterException("Unexpected character after const: " + getNext(), source.getPos());
        }

        try {
            return new GenericConst<>(operator.cast(Integer.parseInt(value.toString())));
        } catch (NumberFormatException e) {
            throw new NumberParseException("Cannot parse " + value + " to int", source.getPos());
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(getNext())) {
            take();
        }
    }

    public void isCorrect(char isCorrected) throws ParserException {
        if (!take(isCorrected)) {
            throw new UnexpectedCharacterException(isCorrected, getNext(), source.getPos());
        }
    }
}
