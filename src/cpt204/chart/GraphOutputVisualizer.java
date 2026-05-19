package cpt204.chart;

import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphQueryResult;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GraphOutputVisualizer {
    public void writePathSummary(Path outputFile, List<GraphQueryResult> graphResults) throws IOException {
        int width = 1500;
        int rowHeight = 150;
        int height = 110 + graphResults.size() * rowHeight;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Task B Shortest Path Cases", 40, 50);

        int y = 95;
        for (GraphQueryResult result : graphResults) {
            drawPathCase(g, result, 40, y, width - 80, rowHeight - 22);
            y += rowHeight;
        }

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeAlgorithmComparison(Path outputFile, List<GraphAlgorithmComparisonResult> results) throws IOException {
        int width = 1500;
        int height = 720;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Task B Shortest Path Algorithm Comparison", 40, 50);

        drawComparisonTable(g, results, 40, 90);
        drawRuntimeBars(g, results, 760, 95, 680, 520);

        g.dispose();
        saveImage(outputFile, image);
    }

    private void prepareCanvas(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
    }

    private void drawPathCase(Graphics2D g, GraphQueryResult result, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, width, height, 16, 16);

        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(result.getCaseName() + "   cost = " + result.getTotalCost(), x + 20, y + 32);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String subtitle = "start: " + result.getStart() + "    destination: " + result.getDestination()
                + "    waypoints: " + formatWaypoints(result);
        g.drawString(subtitle, x + 20, y + 58);

        List<String> path = result.getFinalPath();
        int nodeY = y + 95;
        int nodeX = x + 24;
        int maxX = x + width - 40;
        for (int i = 0; i < path.size(); i++) {
            String node = path.get(i);
            int boxWidth = 62;
            if (nodeX + boxWidth > maxX) {
                break;
            }
            drawNodeBox(g, node, nodeX, nodeY);
            if (i < path.size() - 1 && nodeX + 92 < maxX) {
                drawArrow(g, nodeX + 62, nodeY + 18, nodeX + 86, nodeY + 18);
            }
            nodeX += 92;
        }

        if (path.size() > 14) {
            g.setColor(new Color(110, 118, 130));
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Path is truncated in the image; full path is saved in graph_cases.csv.", maxX - 390, nodeY + 52);
        }
    }

    private String formatWaypoints(GraphQueryResult result) {
        if (result.getWaypointsInOrder().isEmpty()) {
            return "NONE";
        }
        return String.join(" -> ", result.getWaypointsInOrder());
    }

    private void drawNodeBox(Graphics2D g, String text, int x, int y) {
        g.setColor(new Color(221, 238, 255));
        g.fillRoundRect(x, y, 62, 36, 12, 12);
        g.setColor(new Color(52, 111, 186));
        g.drawRoundRect(x, y, 62, 36, 12, 12);

        g.setFont(new Font("Consolas", Font.BOLD, 14));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (62 - metrics.stringWidth(text)) / 2;
        g.setColor(new Color(20, 44, 76));
        g.drawString(text, textX, y + 23);
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(new Color(90, 100, 115));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x2 - 6, y2 - 5);
        g.drawLine(x2, y2, x2 - 6, y2 + 5);
    }

    private void drawComparisonTable(Graphics2D g, List<GraphAlgorithmComparisonResult> results, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, 670, 550, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, 670, 550, 16, 16);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(20, 36, 56));
        g.drawString("Algorithm", x + 18, y + 32);
        g.drawString("Case", x + 260, y + 32);
        g.drawString("Cost", x + 370, y + 32);
        g.drawString("Time (ms)", x + 470, y + 32);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        int rowY = y + 65;
        for (GraphAlgorithmComparisonResult result : results) {
            GraphQueryResult query = result.getQueryResult();
            g.setColor(new Color(20, 36, 56));
            g.drawString(result.getAlgorithmName(), x + 18, rowY);
            g.drawString(query.getCaseName(), x + 260, rowY);
            g.drawString(String.valueOf(query.getTotalCost()), x + 370, rowY);
            g.drawString(String.format("%.6f", result.getRuntimeMillis()), x + 470, rowY);
            rowY += 36;
        }
    }

    private void drawRuntimeBars(Graphics2D g, List<GraphAlgorithmComparisonResult> results, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, width, height, 16, 16);

        double maxTime = 1.0;
        for (GraphAlgorithmComparisonResult result : results) {
            maxTime = Math.max(maxTime, result.getRuntimeMillis());
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(20, 36, 56));
        g.drawString("Runtime bars", x + 20, y + 30);

        int rowY = y + 62;
        for (GraphAlgorithmComparisonResult result : results) {
            int barWidth = (int) (420 * result.getRuntimeMillis() / maxTime);
            g.setColor(new Color(20, 36, 56));
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString(shortName(result.getAlgorithmName()) + " " + result.getQueryResult().getCaseName(), x + 20, rowY + 15);

            g.setColor(new Color(91, 141, 214));
            g.fillRoundRect(x + 170, rowY, Math.max(2, barWidth), 20, 8, 8);
            g.setColor(new Color(20, 36, 56));
            g.drawString(String.format("%.4f ms", result.getRuntimeMillis()), x + 600, rowY + 15);
            rowY += 38;
        }
    }

    private String shortName(String name) {
        if (name.startsWith("Bidirectional")) {
            return "Bi-Dijkstra";
        }
        return name;
    }

    private void saveImage(Path outputFile, BufferedImage image) throws IOException {
        Files.createDirectories(outputFile.getParent());
        ImageIO.write(image, "png", outputFile.toFile());
    }
}
