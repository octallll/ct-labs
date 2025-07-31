package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Md2Html {
    private static final Map<String, String> openTag = new HashMap<>();
    private static final Map<String, String> closeTag = new HashMap<>();
    private static final Map<Character, String> specialReplace = new HashMap<>();

    static {
        openTag.put("*", "em");
        openTag.put("_", "em");
        openTag.put("--", "s");
        openTag.put("`", "code");
        openTag.put("**", "strong");
        openTag.put("__", "strong");

        closeTag.put("*", "/em");
        closeTag.put("_", "/em");
        closeTag.put("--", "/s");
        closeTag.put("`", "/code");
        closeTag.put("**", "/strong");
        closeTag.put("__", "/strong");

        specialReplace.put('<', "&lt;");
        specialReplace.put('>', "&gt;");
        specialReplace.put('&', "&amp;");
    }

    public static void main(String[] args) {
        String inputFile = args[0], outputFile = args[1];

        List<String> paragraphList = new ArrayList<>();

        readText(inputFile, paragraphList);

        writeLines(outputFile, paragraphList);
    }

    private static void readText(String inputFile, List<String> paragraphList) {
            try (BufferedReader scanner = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            readLines(scanner, paragraphList);
        } catch (IOException e) {
            System.err.println("Error when reading file: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void readLines(BufferedReader scanner, List<String> paragraphList) throws IOException {
        StringBuilder markdownParagraph = new StringBuilder();

        boolean endOfOutput = false;

        while (true) {
            String line = scanner.readLine();

            if (line == null) {
                endOfOutput = true;
                line = "";
            }

            if (!line.isEmpty()) {
                markdownParagraph.append(line).append(System.lineSeparator());
                continue;
            }

            if (markdownParagraph.isEmpty()) {
                if (endOfOutput) {
                    break;
                } else {
                    continue;
                }
            }

            line = markdownParagraph.toString();
            line = line.substring(0, line.length() - System.lineSeparator().length());
            markdownParagraph.setLength(0);

            processLine(line, paragraphList);

            if (endOfOutput) {
                break;
            }
        }
    }

    private static void processLine(String line, List<String> paragraphList) {
        StringBuilder currentParagraph = new StringBuilder();

        Deque<String> openTags = new ArrayDeque<>();

        boolean isLinkOpen = false;

        int linkBoardIndex = 3;
        Character[] needSymbolsForLink = {']', '<', '>'};

        enum LinkPart {
            Link,
            Text
        }
        LinkPart linkPart = LinkPart.Text;
        StringBuilder textPartOfLink = new StringBuilder();
        StringBuilder linkPartOfLink = new StringBuilder();

        int tittle = getTittle(line);

        if (tittle > 0) {
            currentParagraph.append("<h").append(tittle).append(">");

            openTags.addLast("</h" + tittle + ">");

            line = line.substring(tittle + 1);
        } else {
            currentParagraph.append("<p>");

            openTags.addLast("</p>");
        }

        for (int lineIndex = 0; lineIndex < line.length(); lineIndex++) {
            char currentSymbol = line.charAt(lineIndex);

            if (isLinkOpen) {
                if (currentSymbol == needSymbolsForLink[linkBoardIndex]) {
                    if (currentSymbol == ']') {
                        linkPart = LinkPart.Link;
                    }

                    if (currentSymbol == '>') {
                        isLinkOpen = false;
                        currentParagraph.append("<a href='").append(linkPartOfLink).append("'>").append(textPartOfLink).append("</a>");
                    }

                    linkBoardIndex++;

                    continue;
                }
            }

            if (currentSymbol == '\\') {
                if (lineIndex + 1 < line.length()) {
                    if (specialReplace.containsKey(line.charAt(lineIndex + 1))) {
                        continue;
                    }

                    updateParagraphOrLink(isLinkOpen, currentParagraph, textPartOfLink, Character.toString(line.charAt(lineIndex + 1)));
                    lineIndex++;
                }

                continue;
            }

            if (currentSymbol == '[' && !isLinkOpen) {
                int needIndex = 0;

                for (int checkIndex = lineIndex + 1; checkIndex < line.length() && needIndex < 3; checkIndex++) {
                    if (checkIndex > 0 && line.charAt(checkIndex - 1) == '\\') {
                        continue;
                    }

                    if (line.charAt(checkIndex) == needSymbolsForLink[needIndex]) {
                        needIndex++;
                    }
                }

                if (needIndex == needSymbolsForLink.length) {
                    isLinkOpen = true;
                    linkBoardIndex = 0;
                    linkPart = LinkPart.Text;
                    textPartOfLink.setLength(0);
                    linkPartOfLink.setLength(0);
                    continue;
                }
            }

            if (isLinkOpen && linkPart == LinkPart.Link) {
                if (specialReplace.containsKey(currentSymbol)) {
                    updateParagraphOrLink(isLinkOpen, currentParagraph, linkPartOfLink, specialReplace.get(currentSymbol));
                } else {
                    updateParagraphOrLink(isLinkOpen, currentParagraph, linkPartOfLink, Character.toString(currentSymbol));
                }

                continue;
            }

            String markdownElement = "";

            if (currentSymbol == '*' || currentSymbol == '_') {
                if (lineIndex + 1 < line.length() && line.charAt(lineIndex + 1) == currentSymbol) {
                    markdownElement = currentSymbol + Character.toString(line.charAt(lineIndex + 1));
                    lineIndex++;
                } else {
                    if ((lineIndex - 1 >= 0 && !Character.isWhitespace(line.charAt(lineIndex - 1)))
                            || (lineIndex + 1 < line.length() && !Character.isWhitespace(line.charAt(lineIndex + 1)))) {
                        markdownElement = Character.toString(currentSymbol);
                    } else {
                        markdownElement = "";
                    }
                }
            }

            if (currentSymbol == '-' && lineIndex + 1 < line.length() && line.charAt(lineIndex + 1) == '-') {
                markdownElement = currentSymbol + Character.toString(line.charAt(lineIndex + 1));
                lineIndex++;
            }

            if (currentSymbol == '`') {
                markdownElement = Character.toString(currentSymbol);
            }

            if (!markdownElement.isEmpty()) {
                if (!openTags.isEmpty() && openTags.getLast().equals(closeTag.get(markdownElement))) {
                    updateParagraphOrLink(isLinkOpen, currentParagraph, textPartOfLink, "<" + openTags.removeLast() + ">");
                } else {
                    updateParagraphOrLink(isLinkOpen, currentParagraph, textPartOfLink, "<" + openTag.get(markdownElement) + ">");
                    openTags.addLast(closeTag.get(markdownElement));
                }

                continue;
            }

            if (specialReplace.containsKey(currentSymbol)) {
                updateParagraphOrLink(isLinkOpen, currentParagraph, textPartOfLink, specialReplace.get(currentSymbol));
            } else {
                updateParagraphOrLink(isLinkOpen, currentParagraph, textPartOfLink, Character.toString(currentSymbol));
            }
        }

        updateParagraphList(currentParagraph, paragraphList, openTags);
        currentParagraph.setLength(0);
    }

    private static void updateParagraphOrLink(boolean isLinkOpen, StringBuilder currentParagraph, StringBuilder linkData, String addedData) {
        if (isLinkOpen) {
            linkData.append(addedData);
        } else {
            currentParagraph.append(addedData);
        }
    }

    private static void updateParagraphList(StringBuilder currentParagraph, List<String> paragraphList, Deque<String> openTags) {
        if (!currentParagraph.isEmpty()) {
            while (!openTags.isEmpty()) {
                currentParagraph.append(openTags.removeLast());
            }

            paragraphList.add(currentParagraph.toString());
        }
    }

    private static int getTittle(String line) {
        int titleType = 0;
        int titleEndIndex = -1;

        for (int lineIndex = 0; lineIndex < line.length(); lineIndex++) {
            if (line.charAt(lineIndex) != '#') {
                titleEndIndex = lineIndex;
                break;
            }

            titleType++;
        }

        if (titleType > 6
                || titleType == 0
                || (titleEndIndex != -1 && titleEndIndex + 1 < line.length() && !Character.isWhitespace(line.charAt(titleEndIndex)))) {
            return 0;
        } else {
            return titleType;
        }
    }

    private static void writeLines(String outputFile, List<String> paragraphList) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)
        )) {
            for (int listIndex = 0; listIndex < paragraphList.size(); listIndex++) {
                writer.write(paragraphList.get(listIndex));

                if (listIndex != paragraphList.size() - 1) {
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error when writing to file: " + e.getMessage());
        }
    }
}
