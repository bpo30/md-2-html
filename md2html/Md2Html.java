package md2html;

import markup.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Md2Html {

    private static void convert(String inputFile, String outputFile) throws IOException {
        List<ExternalElement> elements = parseMarkdown(inputFile);
        writeHtml(elements, outputFile);
    }

    private static List<ExternalElement> parseMarkdown(String inputFile) throws IOException {
        List<ExternalElement> elements = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {

            StringBuilder currentBlock = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (!currentBlock.isEmpty()) {
                        elements.add(parseExternalElement(currentBlock.toString()));
                        currentBlock.setLength(0);
                    }
                } else {
                    if (!currentBlock.isEmpty()) {
                        currentBlock.append('\n');
                    }
                    currentBlock.append(line);
                }
            }

            if (!currentBlock.isEmpty()) {
                elements.add(parseExternalElement(currentBlock.toString()));
            }
        }

        return elements;
    }

    private static ExternalElement parseExternalElement(String text) {
        if (text.startsWith("#")) {
            int headerLevel = 0;
            int index = 0;

            while (index < text.length() && text.charAt(index) == '#') {
                headerLevel++;
                index++;
            }

            if (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                String content = text.substring(index + 1);
                Header header = new Header(parseInternalElements(content));
                header.setHeaderLevel(headerLevel);
                return header;
            }
        }

        return new Paragraph(parseInternalElements(text));
    }

    private static List<InternalElement> parseInternalElements(String text) {
        List<InternalElement> elements = new ArrayList<>();
        List<Token> tokens = tokenize(text);
        parseTokens(tokens, elements);
        return elements;
    }

    private static List<Token> tokenize(String text) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\\' && i + 1 < text.length()) {
                current.append(text.charAt(i + 1));
                i++;
            } else if (c == '*' || c == '_' || c == '-' || c == '`') {
                if (current.length() > 0) {
                    tokens.add(new Token(TokenType.TEXT, current.toString()));
                    current.setLength(0);
                }

                String tag = extractTag(text, i, c);
                tokens.add(new Token(TokenType.TAG, tag));
                i += tag.length() - 1;
            } else if (c == '<' || c == '>' || c == '&') {
                if (current.length() > 0) {
                    tokens.add(new Token(TokenType.TEXT, current.toString()));
                    current.setLength(0);
                }
                tokens.add(new Token(TokenType.SPECIAL, String.valueOf(c)));
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            tokens.add(new Token(TokenType.TEXT, current.toString()));
        }

        return tokens;
    }

    private static String extractTag(String text, int start, char c) {
        if (c == '`') return "`";
        if (c == '-' && start + 1 < text.length() && text.charAt(start + 1) == '-') return "--";
        if ((c == '*' || c == '_') && start + 1 < text.length() && text.charAt(start + 1) == c) {
            return String.valueOf(c) + c;
        }
        return String.valueOf(c);
    }

    private static void parseTokens(List<Token> tokens, List<InternalElement> elements) {
        Stack<String> tagStack = new Stack<>();
        List<InternalElement> currentElements = new ArrayList<>();
        boolean insideCode = false;

        for (Token token : tokens) {
            if (isCodeToken(token)) {
                insideCode = handleCodeToggle(insideCode, currentElements, elements);
            } else if (insideCode) {
                addCodeContent(token, currentElements);
            } else {
                handleRegularToken(token, tagStack, currentElements, elements);
            }
        }

        elements.addAll(currentElements);
    }

    private static boolean isCodeToken(Token token) {
        return token.type == TokenType.TAG && token.value.equals("`");
    }

    private static boolean handleCodeToggle(boolean insideCode, List<InternalElement> currentElements,
                                            List<InternalElement> elements) {
        if (insideCode) {
            StringBuilder codeContent = new StringBuilder();
            for (InternalElement element : currentElements) {
                if (element instanceof Text) {
                    StringBuilder sb = new StringBuilder();
                    element.toMarkdown(sb);
                    codeContent.append(sb.toString());
                }
            }
            elements.add(new Code(Arrays.asList(new Text(codeContent.toString()))));
            currentElements.clear();
        } else {
            elements.addAll(currentElements);
            currentElements.clear();
        }
        return !insideCode;
    }

    private static void addCodeContent(Token token, List<InternalElement> currentElements) {
        currentElements.add(new Text(token.value));
    }

    private static void handleRegularToken(Token token, Stack<String> tagStack,
                                           List<InternalElement> currentElements,
                                           List<InternalElement> elements) {
        switch (token.type) {
            case TEXT:
                currentElements.add(new Text(token.value));
                break;
            case SPECIAL:
                currentElements.add(new Text(escapeHtml(token.value)));
                break;
            case TAG:
                handleMarkupTag(token.value, tagStack, currentElements, elements);
                break;
        }
    }

    private static void handleMarkupTag(String tag, Stack<String> tagStack,
                                        List<InternalElement> currentElements,
                                        List<InternalElement> elements) {
        if (!tagStack.isEmpty() && tagStack.peek().equals(tag)) {
            tagStack.pop();
            InternalElement element = createMarkupElement(tag, new ArrayList<>(currentElements));
            elements.add(element);
            currentElements.clear();
        } else {
            elements.addAll(currentElements);
            currentElements.clear();
            tagStack.push(tag);
        }
    }

    private static InternalElement createMarkupElement(String tag, List<InternalElement> content) {
        switch (tag) {
            case "*":
            case "_":
                return new Emphasis(content);
            case "**":
            case "__":
                return new Strong(content);
            case "--":
                return new Strikeout(content);
            default:
                return new Text(tag);
        }
    }

    private static String escapeHtml(String text) {
        switch (text) {
            case "<": return "&lt;";
            case ">": return "&gt;";
            case "&": return "&amp;";
            default: return text;
        }
    }

    private static void writeHtml(List<ExternalElement> elements, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            for (ExternalElement element : elements) {
                StringBuilder buffer = new StringBuilder();
                element.toHtml(buffer);
                writer.write(buffer.toString());
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        try {
            convert(args[0], args[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Token {
        final TokenType type;
        final String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private enum TokenType {
        TEXT, TAG, SPECIAL
    }
}