package expression.exceptions;

import expression.exceptions.compares.*;
import expression.parser.BaseParser;
import expression.parser.StringSource;

import java.util.ArrayList;
import expression.*;

public class ExpressionParser extends BaseParser implements TripleParser {
    static ArrayList<String[]> priorityToOperation = new ArrayList<>();

    static {
        priorityToOperation.add(new String[]{"==", "!="});
        priorityToOperation.add(new String[]{"<=", ">=", ">", "<"});
        priorityToOperation.add(new String[]{"<?", ">?"});
        priorityToOperation.add(new String[]{"+", "-"});
        priorityToOperation.add(new String[]{"*", "/"});
    }

    Expressions makeNewClass(final String operationRender, final Expressions left, final Expressions right) throws ParserException {
        return switch (operationRender) {
            case "+" -> new CheckedAdd(left, right);
            case "-" -> new CheckedSubtract(left, right);
            case "*" -> new CheckedMultiply(left, right);
            case "/" -> new CheckedDivide(left, right);
            case "<" -> new CompareLess(left, right);
            case "<=" -> new CompareLessOrEqual(left, right);
            case ">" -> new CompareGreater(left, right);
            case ">=" -> new CompareGreaterOrEqual(left, right);
            case "==" -> new CompareEqual(left, right);
            case "!=" -> new CompareNotEqual(left, right);
            case "<?" -> new Min(left, right);
            case ">?" -> new Max(left, right);
            default -> throw new UnknownOperationException("Unknown operation: " + operationRender, source.getPos());
        };
    }

    @Override
    public Expressions parse(final String expression) throws ParserException {
        source = new StringSource(expression);
        take();

        Expressions result = parsePriority(0);
        isCorrect(END);

        return result;
    }

    private Expressions parsePriority(int priority) throws ParserException {
        if (priority == priorityToOperation.size()) {
            return parseUnary();
        }

        Expressions result = parsePriority(priority + 1);

        skipWhitespace();

        boolean taken = true;

        while (taken) {
            taken = false;

            for (String operationRender : priorityToOperation.get(priority)) {
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

    private Expressions parseUnary() throws ParserException {
        skipWhitespace();

        Expressions result;

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
                result = new CheckedNegate(parseUnary());
            }
        } else if (Character.isDigit(getNext())) {
            result = parseConst(false);
        } else if (Character.isAlphabetic(getNext())) {
            result = parseVariable();
        } else {
            throw new UnexpectedCharacterException("Unexpected character - correct are brackets, minus, digit or letter, actual: " + getNext(), source.getPos());
        }

        skipWhitespace();

        return result;
    }

    private Expressions parseVariable() throws ParserException {
        StringBuilder name = new StringBuilder();

        while (Character.isAlphabetic(getNext())) {
            name.append(take());
        }

        char last = name.charAt(name.length() - 1);

        if (last != 'x' && last != 'y' && last != 'z') {
            throw new UnexpectedCharacterException("Error when parsing variable isCorrected x, y or z in last position, actual: " + last, source.getPos());
        }

        return new Variable(name.toString());
    }

    private Expressions parseConst(boolean isNegative) throws ParserException {
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

        if (Character.isAlphabetic(getNext())) {
            throw new UnexpectedCharacterException("Unexpected character after const: " + getNext(), source.getPos());
        }

        try {
            return new Const(Integer.parseInt(value.toString()));
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
