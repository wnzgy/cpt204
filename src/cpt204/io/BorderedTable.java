package cpt204.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BorderedTable {
    private static final String TOP_LEFT = "\u250C";
    private static final String TOP_RIGHT = "\u2510";
    private static final String BOTTOM_LEFT = "\u2514";
    private static final String BOTTOM_RIGHT = "\u2518";
    private static final String HORIZONTAL = "\u2500";
    private static final String VERTICAL = "\u2502";
    private static final String T_DOWN = "\u252C";
    private static final String T_UP = "\u2534";
    private static final String T_RIGHT = "\u251C";
    private static final String T_LEFT = "\u2524";
    private static final String CROSS = "\u253C";

    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    public BorderedTable(String... headers) {
        this.headers = headers.clone();
    }

    public BorderedTable addRow(String... cells) {
        if (cells.length != headers.length) {
            throw new IllegalArgumentException("Expected " + headers.length + " cells, got " + cells.length);
        }
        rows.add(cells.clone());
        return this;
    }

    public void printToConsole() {
        for (String line : renderLines()) {
            System.out.println(line);
        }
    }

    public List<String> renderLines() {
        int columnCount = headers.length;
        int[] widths = new int[columnCount];
        for (int c = 0; c < columnCount; c++) {
            widths[c] = headers[c].length();
        }
        for (String[] row : rows) {
            for (int c = 0; c < columnCount; c++) {
                widths[c] = Math.max(widths[c], row[c].length());
            }
        }

        List<String> lines = new ArrayList<>();
        lines.add(horizontalLine(TOP_LEFT, T_DOWN, TOP_RIGHT, widths));
        lines.add(dataLine(headers, widths));
        lines.add(horizontalLine(T_RIGHT, CROSS, T_LEFT, widths));
        for (String[] row : rows) {
            lines.add(dataLine(row, widths));
        }
        lines.add(horizontalLine(BOTTOM_LEFT, T_UP, BOTTOM_RIGHT, widths));
        return lines;
    }

    private static String horizontalLine(String left, String middle, String right, int[] widths) {
        StringBuilder builder = new StringBuilder();
        builder.append(left);
        for (int i = 0; i < widths.length; i++) {
            builder.append(repeat(HORIZONTAL, widths[i] + 2));
            builder.append(i < widths.length - 1 ? middle : right);
        }
        return builder.toString();
    }

    private static String dataLine(String[] cells, int[] widths) {
        StringBuilder builder = new StringBuilder();
        builder.append(VERTICAL);
        for (int i = 0; i < cells.length; i++) {
            builder.append(' ');
            builder.append(padRight(cells[i], widths[i]));
            builder.append(' ');
            builder.append(VERTICAL);
        }
        return builder.toString();
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static String repeat(String unit, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(unit);
        }
        return builder.toString();
    }
}
