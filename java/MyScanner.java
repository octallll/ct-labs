import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyScanner {
    private final int BUFFER_SIZE = 1024;

    private final char[] buffer = new char[BUFFER_SIZE];
    private int bufferIndex = BUFFER_SIZE;
    private int canRead = BUFFER_SIZE;

    private static Reader reader;

    MyScanner(final InputStream inputStream) {
        reader = new InputStreamReader(inputStream);
    }

    MyScanner(final FileInputStream fileInputStream) {
        reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
    }

    MyScanner(final String text) {
        reader = new StringReader(text);
    }

    private void updateBuffer() {
        if (bufferIndex < canRead) {
            return;
        }

        try {
            canRead = reader.read(buffer);

            bufferIndex = 0;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private char getNextChar() {
        if (bufferIndex == canRead) {
            updateBuffer();
        }

        if (canRead == -1) {
            return (char) 0;
        } else {
            return buffer[bufferIndex++];
        }
    }

    public boolean isNext() {
        updateBuffer();

        return bufferIndex < canRead;
    }

    private static boolean checkCharForReverse(char currentSymbol) {
        return currentSymbol == '-' || Character.isDigit(currentSymbol) || currentSymbol == 'o' || currentSymbol == 'O';
    }

    private static boolean checkCharForWordCount(char currentSymbol) {
        return Character.isLetter(currentSymbol)
                || Character.getType(currentSymbol) == Character.DASH_PUNCTUATION
                || currentSymbol == '\'';
    }

    private static boolean checkCharForWordCountEven(char currentSymbol) {
        return Character.isLetter(currentSymbol)
                || Character.isDigit(currentSymbol)
                || Character.getType(currentSymbol) == Character.DASH_PUNCTUATION
                || currentSymbol == '\'';
    }

    enum TaskType {
        Reverse,
        WordCount,
        WordCountEven
    }

    private static boolean checkChar(TaskType taskType, char currentSymbol) {
        return switch (taskType) {
            case Reverse -> checkCharForReverse(currentSymbol);
            case WordCount -> checkCharForWordCount(currentSymbol);
            case WordCountEven -> checkCharForWordCountEven(currentSymbol);
            default -> false;
        };

    }

    private String getNext(TaskType taskType, boolean needLine) {
        StringBuilder current = new StringBuilder();

        while (isNext()) {
            char currentSymbol = getNextChar();

            if (needLine && currentSymbol == '\n') {
                if (!current.isEmpty()) {
                    bufferIndex--;
                    return current.toString();
                } else {
                    return null;
                }
            }
            if (checkChar(taskType, currentSymbol)) {
                current.append(currentSymbol);
            } else if (!current.isEmpty()) {
                return current.toString();
            }
        }

        if (!current.isEmpty()) {
            return current.toString();
        } else {
            return null;
        }
    }

    public String getNextWordCountEven() {
        return getNext(TaskType.WordCountEven, true);
    }

    public String getNextWordCount() {
        return getNext(TaskType.WordCount, false);
    }

    public String getNextIntInLine() {
        return getNext(TaskType.Reverse, true);
    }

    public String getNextInt() {
        return getNext(TaskType.Reverse, false);
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error when closing reader: " + e.getMessage());
        }
    }
}