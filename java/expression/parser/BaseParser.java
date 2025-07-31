package expression.parser;

public abstract class BaseParser {
    protected static final char END = '\0';
    protected CharSource source;
    private char ch = 0xffff;

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean test(final char expected) {
        return ch == expected;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected void back() {
        ch = source.back();
    }

    protected boolean take(final String expected) {
        for (int strInd = 0; strInd < expected.length(); strInd++) {
            if (!take(expected.charAt(strInd))) {
                for (int step = 0; step < strInd; step++) {
                    back();
                }

                return false;
            }
        }

        return true;
    }

    protected boolean test(final String expected) {
        for (int strInd = 0; strInd < expected.length(); strInd++) {
            if (!take(expected.charAt(strInd))) {
                for (int step = 0; step < strInd; step++) {
                    back();
                }

                return false;
            }
        }

        for (int step = 0; step < expected.length(); step++) {
            back();
        }

        return true;
    }

    protected void expect(final char expected) {
        if (!take(expected)) {
            throw error("Expected '" + expected + "', found '" + ch + "'");
        }
    }

    protected void expect(final String value) {
        for (final char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }

    protected char getNext() {
        return ch;
    }
}
