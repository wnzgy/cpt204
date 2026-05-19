package cpt204.chart;

import cpt204.sort.DataStructureComparisonResult;
import cpt204.sort.DatasetCharacteristicsResult;
import cpt204.sort.DatasetSortingResult;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SortingOutputVisualizer {
    public void writeSortingRuntimeChart(
            Path outputFile,
            List<DatasetSortingResult> sortingResults
    ) throws IOException {
        int width = 1500;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "Task A Sorting Runtime Comparison", 40, 50);
        drawSortingRuntimeChart(g, sortingResults, 65, 95, 1370, 600);

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeDatasetCharacteristicsChart(
            Path outputFile,
            List<DatasetCharacteristicsResult> results
    ) throws IOException {
        int width = 1300;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "Dataset Characteristics Used in Sorting Analysis", 40, 50);
        drawInversionBars(g, results, 70, 110, 540, 480);
        drawBubbleBars(g, results, 690, 110, 540, 480);

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeDataStructureChart(
            Path outputFile,
            List<DataStructureComparisonResult> results
    ) throws IOException {
        int width = 1300;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "ArrayList vs LinkedList for Quick Sort", 40, 50);
        drawStructureBars(g, results, 80, 120, 1120, 460);

        g.dispose();
        saveImage(outputFile, image);
    }

    private void prepareCanvas(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
    }

    private void drawTitle(Graphics2D g, String title, int x, int y) {
        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString(title, x, y);
    }

    private void drawSortingRuntimeChart(
            Graphics2D g,
            List<DatasetSortingResult> sortingResults,
            int x,
            int y,
            int width,
            int height
    ) {
        drawChartBox(g, x, y, width, height, "Measured Runtime");

        String[] algorithmNames = {"Bubble Sort", "Quick Sort", "Merge Sort"};
        Color[] colors = {
                new Color(40, 119, 190),
                new Color(219, 84, 47),
                new Color(23, 150, 115)
        };

        double minTime = 0.01;
        double maxTime = 1.0;
        for (DatasetSortingResult sortingResult : sortingResults) {
            for (String algorithmName : algorithmNames) {
                maxTime = Math.max(maxTime, getMeasuredMillis(sortingResult, algorithmName));
            }
        }
        maxTime = nextPowerOfTen(maxTime);

        int plotX = x + 105;
        int plotY = y + 95;
        int plotWidth = width - 220;
        int plotHeight = height - 190;
        drawRuntimeGrid(g, plotX, plotY, plotWidth, plotHeight, minTime, maxTime);
        drawRuntimeAxis(g, plotX, plotY, plotWidth, plotHeight);
        drawDatasetLabels(g, sortingResults, plotX, plotY, plotWidth, plotHeight);

        for (int a = 0; a < algorithmNames.length; a++) {
            drawMeasuredLine(g, sortingResults, algorithmNames[a], colors[a], minTime, maxTime, plotX, plotY, plotWidth, plotHeight);
        }

        drawSortingLegend(g, algorithmNames, colors, x + 70, y + 52);
    }

    private void drawRuntimeGrid(
            Graphics2D g,
            int plotX,
            int plotY,
            int plotWidth,
            int plotHeight,
            double minTime,
            double maxTime
    ) {
        double[] timeTicks = {0.01, 0.1, 1.0, 10.0, 100.0};

        g.setFont(new Font("Arial", Font.PLAIN, 13));
        for (double timeTick : timeTicks) {
            if (timeTick >= minTime && timeTick <= maxTime) {
                int tickY = logValueToY(timeTick, minTime, maxTime, plotY, plotHeight);
                g.setColor(new Color(225, 230, 238));
                g.setStroke(new BasicStroke(1));
                g.drawLine(plotX, tickY, plotX + plotWidth, tickY);
                g.setColor(new Color(75, 85, 100));
                g.drawString(formatTick(timeTick), plotX - 52, tickY + 5);
            }
        }
    }

    private void drawRuntimeAxis(Graphics2D g, int plotX, int plotY, int plotWidth, int plotHeight) {
        g.setColor(new Color(120, 135, 155));
        g.setStroke(new BasicStroke(2));
        g.drawLine(plotX, plotY, plotX, plotY + plotHeight);
        g.drawLine(plotX, plotY + plotHeight, plotX + plotWidth, plotY + plotHeight);

        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Runtime (ms, log scale)", plotX, plotY - 22);
    }

    private void drawDatasetLabels(
            Graphics2D g,
            List<DatasetSortingResult> sortingResults,
            int plotX,
            int plotY,
            int plotWidth,
            int plotHeight
    ) {
        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < sortingResults.size(); i++) {
            int pointX = getDatasetX(i, sortingResults.size(), plotX, plotWidth);
            g.drawString(sortingResults.get(i).getDatasetName(), pointX - 30, plotY + plotHeight + 30);
        }
    }

    private void drawMeasuredLine(
            Graphics2D g,
            List<DatasetSortingResult> sortingResults,
            String algorithmName,
            Color color,
            double minTime,
            double maxTime,
            int plotX,
            int plotY,
            int plotWidth,
            int plotHeight
    ) {
        g.setColor(color);
        g.setStroke(new BasicStroke(3));

        int lastX = -1;
        int lastY = -1;
        for (int i = 0; i < sortingResults.size(); i++) {
            DatasetSortingResult result = sortingResults.get(i);
            double millis = getMeasuredMillis(result, algorithmName);
            int pointX = getDatasetX(i, sortingResults.size(), plotX, plotWidth);
            int pointY = logValueToY(millis, minTime, maxTime, plotY, plotHeight);

            g.setColor(color);
            g.setStroke(new BasicStroke(3));
            if (lastX >= 0) {
                g.drawLine(lastX, lastY, pointX, pointY);
            }
            g.fillOval(pointX - 6, pointY - 6, 12, 12);
            drawRuntimePointLabel(g, algorithmName, millis, pointX, pointY);

            lastX = pointX;
            lastY = pointY;
        }
    }

    private void drawSortingLegend(Graphics2D g, String[] algorithmNames, Color[] colors, int x, int y) {
        for (int i = 0; i < algorithmNames.length; i++) {
            int legendX = x + i * 210;
            g.setColor(colors[i]);
            g.setStroke(new BasicStroke(3));
            g.drawLine(legendX, y, legendX + 28, y);
            g.fillOval(legendX + 11, y - 5, 10, 10);
            g.setColor(new Color(20, 36, 56));
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString(algorithmNames[i], legendX + 38, y + 5);
        }
    }

    private void drawRuntimePointLabel(Graphics2D g, String algorithmName, double millis, int pointX, int pointY) {
        int yOffset = -12;
        int xOffset = -34;
        if ("Quick Sort".equals(algorithmName)) {
            yOffset = 18;
        } else if ("Merge Sort".equals(algorithmName)) {
            yOffset = 34;
            xOffset = 8;
        }

        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(String.format("%.3f ms", millis), pointX + xOffset, pointY + yOffset);
    }

    private int getDatasetX(int index, int datasetCount, int plotX, int plotWidth) {
        return plotX + (index + 1) * plotWidth / (datasetCount + 1);
    }

    private double getMeasuredMillis(DatasetSortingResult result, String algorithmName) {
        Long nanos = result.getAvgRuntimeByAlgorithmNanos().get(algorithmName);
        return nanos / 1_000_000.0;
    }

    private int logValueToY(double value, double minValue, double maxValue, int plotY, int plotHeight) {
        double safeValue = Math.max(value, minValue);
        double minLog = Math.log10(minValue);
        double maxLog = Math.log10(maxValue);
        double valueLog = Math.log10(safeValue);
        double ratio = (valueLog - minLog) / (maxLog - minLog);
        return plotY + plotHeight - (int) Math.round(ratio * plotHeight);
    }

    private double nextPowerOfTen(double value) {
        return Math.pow(10, Math.ceil(Math.log10(value)));
    }

    private String formatTick(double value) {
        if (value < 1.0) {
            return String.valueOf(value);
        }
        return String.format("%.0f", value);
    }

    private void drawInversionBars(Graphics2D g, List<DatasetCharacteristicsResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Total inversions");
        long maxValue = 1L;
        for (DatasetCharacteristicsResult result : results) {
            maxValue = Math.max(maxValue, result.getTotalInversions());
        }
        int rowY = y + 80;
        for (DatasetCharacteristicsResult result : results) {
            drawOneBar(g, result.getDatasetName(), result.getTotalInversions(), maxValue, x + 35, rowY, width - 130, new Color(40, 119, 190));
            rowY += 90;
        }
    }

    private void drawBubbleBars(Graphics2D g, List<DatasetCharacteristicsResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Bubble sort passes");
        long maxValue = 1L;
        for (DatasetCharacteristicsResult result : results) {
            maxValue = Math.max(maxValue, result.getBubblePasses());
        }
        int rowY = y + 80;
        for (DatasetCharacteristicsResult result : results) {
            drawOneBar(g, result.getDatasetName(), result.getBubblePasses(), maxValue, x + 35, rowY, width - 130, new Color(23, 150, 115));
            rowY += 90;
        }
    }

    private void drawStructureBars(Graphics2D g, List<DataStructureComparisonResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Full-Dataset Quick Sort Runtime by List Type");
        double maxValue = 1.0;
        for (DataStructureComparisonResult result : results) {
            maxValue = Math.max(maxValue, result.getAvgRuntimeMillis());
        }

        int rowY = y + 75;
        for (DataStructureComparisonResult result : results) {
            String label = result.getDatasetName() + " " + result.getListType();
            Color color = "ArrayList".equals(result.getListType()) ? new Color(40, 119, 190) : new Color(219, 84, 47);
            drawOneBar(g, label, result.getAvgRuntimeMillis(), maxValue, x + 35, rowY, width - 140, color);
            rowY += 58;
        }
    }

    private void drawChartBox(Graphics2D g, int x, int y, int width, int height, String title) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(title, x + 20, y + 32);
    }

    private void drawOneBar(Graphics2D g, String label, long value, long maxValue, int x, int y, int width, Color color) {
        drawOneBar(g, label, (double) value, (double) maxValue, x, y, width, color);
    }

    private void drawOneBar(Graphics2D g, String label, double value, double maxValue, int x, int y, int width, Color color) {
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(new Color(20, 36, 56));
        g.drawString(label, x, y + 16);

        int barX = x + 150;
        int barWidth = (int) (width * value / maxValue);
        g.setColor(new Color(232, 236, 242));
        g.fillRoundRect(barX, y, width, 22, 8, 8);
        g.setColor(color);
        g.fillRoundRect(barX, y, Math.max(2, barWidth), 22, 8, 8);
        g.setColor(new Color(20, 36, 56));
        int valueX = barX + Math.min(barWidth + 10, width - 70);
        g.drawString(formatNumber(value), valueX, y + 16);
    }

    private String formatNumber(double value) {
        if (value >= 1000) {
            return String.format("%.0f", value);
        }
        return String.format("%.3f", value);
    }

    private void saveImage(Path outputFile, BufferedImage image) throws IOException {
        Files.createDirectories(outputFile.getParent());
        ImageIO.write(image, "png", outputFile.toFile());
    }
}
