import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BFSVisualization extends JFrame {

    private final int startVertex = 0;

    private final int[] nodes = {0, 1, 2, 3, 4};

    private final int[][] edges = {
            {0, 1},
            {1, 4},
            {0, 3},
            {0, 2},
            {1, 2},
            {2, 3}
    };

    private final Map<Integer, Point> pos = new HashMap<>();
    private final Map<Integer, List<Integer>> adj = new HashMap<>();
    private final List<Event> events = new ArrayList<>();

    private int stepIndex = 0;

    private GraphPanel graphPanel;
    private CodePanel codePanel;
    private QueuePanel queuePanel;
    private JTextArea narrativeArea;

    private javax.swing.Timer animationTimer;
    private double animationProgress = 0.0;

    private final Color COLOR_UNVISITED = Color.WHITE;
    private final Color COLOR_IN_QUEUE = new Color(241, 196, 15);
    private final Color COLOR_CURRENT_U = new Color(230, 126, 34);
    private final Color COLOR_CURRENT_W = new Color(142, 68, 173);
    private final Color COLOR_FINISHED = new Color(46, 134, 193);
    private final Color COLOR_CURRENT_EDGE = new Color(231, 76, 60);
    private final Color COLOR_NORMAL_EDGE = new Color(170, 170, 170);
    private final Color COLOR_TICK = new Color(39, 174, 96);
    private final Color COLOR_FIRST_REACHED = new Color(39, 174, 96);

    private final String[] codeLines = {
            "BFS(start vertex v) {",
            "    create an empty queue;",
            "    add v to the queue;",
            "    mark v as visited;",
            "",
            "    while the queue is not empty {",
            "        take a vertex from the queue and call it u,",
            "        so we can check all neighbors of u;",
            "",
            "        for each neighbor w of u {",
            "            if w has not been visited {",
            "                add w to the queue;",
            "                record that w was first reached from u;",
            "                mark w as visited;",
            "            }",
            "        }",
            "        finish checking all neighbors of u;",
            "    }",
            "}"
    };

    public BFSVisualization() {
        setTitle("BFS Visualization - Java Version");
        setSize(1150, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPositions();
        buildAdjacencyList();
        bfsCapture(startVertex);

        buildUI();
        updateView(false);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        mainPanel.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Breadth-First Search Visualization");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        mainPanel.add(title, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        codePanel = new CodePanel();
        queuePanel = new QueuePanel();

        JPanel graphCard = createCard("Graph Traversal");
        graphCard.setLayout(new BorderLayout(10, 10));
        graphCard.add(graphPanel, BorderLayout.CENTER);
        graphCard.add(queuePanel, BorderLayout.SOUTH);

        JPanel codeCard = createCard("Pseudocode");
        codeCard.setLayout(new BorderLayout());
        codeCard.add(codePanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphCard, codeCard);
        splitPane.setResizeWeight(0.58);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(12, 12));
        bottomPanel.setOpaque(false);

        JPanel narrativeCard = createCard("Trace Log");
        narrativeCard.setLayout(new BorderLayout());

        narrativeArea = new JTextArea(7, 60);
        narrativeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        narrativeArea.setEditable(false);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        narrativeArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(narrativeArea);
        scrollPane.setBorder(null);
        narrativeCard.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton resetButton = createButton("Reset");
        JButton nextButton = createButton("Next Step");

        resetButton.addActionListener(e -> reset());
        nextButton.addActionListener(e -> nextStep());

        buttonPanel.add(resetButton);
        buttonPanel.add(nextButton);

        bottomPanel.add(narrativeCard, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createCard(String title) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 223, 228), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(new EmptyBorder(0, 0, 8, 0));

        outer.add(label, BorderLayout.NORTH);
        return outer;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 36));
        return button;
    }

    private void initPositions() {
        pos.put(0, new Point(150, 145));
        pos.put(1, new Point(405, 145));
        pos.put(4, new Point(405, 365));
        pos.put(3, new Point(150, 365));
        pos.put(2, new Point(280, 255));
    }

    private void buildAdjacencyList() {
        for (int node : nodes) {
            adj.put(node, new ArrayList<>());
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];

            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        for (int node : nodes) {
            Collections.sort(adj.get(node));
        }
    }

    private void bfsCapture(int start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        events.add(new Event("init_queue", start, null));

        queue.add(start);
        events.add(new Event("enqueue_start", start, null));

        visited.add(start);
        events.add(new Event("mark_start", start, null));

        while (!queue.isEmpty()) {
            events.add(new Event("check_empty", null, null));

            int u = queue.poll();
            events.add(new Event("dequeue", u, null));

            for (int w : adj.get(u)) {
                events.add(new Event("for", u, w));
                events.add(new Event("check_if", u, w));

                if (!visited.contains(w)) {
                    queue.add(w);
                    events.add(new Event("enqueue", u, w));

                    events.add(new Event("record_parent", u, w));

                    visited.add(w);
                    events.add(new Event("mark_child", u, w));
                } else {
                    events.add(new Event("skip", u, w));
                }
            }

            events.add(new Event("finish_vertex", u, null));
        }

        events.add(new Event("end", null, null));
    }

    private void nextStep() {
        if (stepIndex < events.size() - 1) {
            stepIndex++;
            updateView(true);
        }
    }

    private void reset() {
        stepIndex = 0;
        updateView(false);
    }

    private void updateView(boolean animate) {
        narrativeArea.setText(buildNarrative());
        narrativeArea.setCaretPosition(narrativeArea.getDocument().getLength());

        if (animate) {
            startAnimation();
        } else {
            animationProgress = 0.0;
            repaintAll();
        }
    }

    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationProgress = 0.0;

        animationTimer = new javax.swing.Timer(25, e -> {
            animationProgress += 0.08;

            if (animationProgress >= 1.0) {
                animationProgress = 1.0;
                animationTimer.stop();
            }

            repaintAll();
        });

        animationTimer.start();
    }

    private void repaintAll() {
        graphPanel.repaint();
        codePanel.repaint();
        queuePanel.repaint();
    }

    private List<Integer> reconstructQueue() {
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("init_queue")) {
                queue.clear();
            } else if (e.type.equals("enqueue_start")) {
                queue.add(e.u);
            } else if (e.type.equals("enqueue")) {
                queue.add(e.w);
            } else if (e.type.equals("dequeue") && !queue.isEmpty()) {
                queue.poll();
            }
        }

        return new ArrayList<>(queue);
    }

    private Set<Integer> getVisitedNodes() {
        Set<Integer> visited = new TreeSet<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("mark_start")) {
                visited.add(e.u);
            } else if (e.type.equals("mark_child")) {
                visited.add(e.w);
            }
        }

        return visited;
    }

    private Set<Integer> getFinishedNodes() {
        Set<Integer> finished = new TreeSet<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("finish_vertex")) {
                finished.add(e.u);
            }
        }

        return finished;
    }

    private List<Event> getFirstReachedEvents() {
        List<Event> result = new ArrayList<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("record_parent")) {
                result.add(e);
            }
        }

        return result;
    }

    private String buildNarrative() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);
            sb.append("Step ").append(i).append(": ");
            sb.append(getStepExplanation(e)).append("\n");
        }

        return sb.toString();
    }

    private String getStepExplanation(Event e) {
        switch (e.type) {
            case "init_queue":
                return "Create an empty queue.";

            case "enqueue_start":
                return "Add the start vertex v = " + e.u + " to the queue.";

            case "mark_start":
                return "Mark the start vertex v = " + e.u + " as visited.";

            case "check_empty":
                return "Check whether the queue is empty.";

            case "dequeue":
                return "Take vertex " + e.u + " from the queue and call it u so we can check its neighbors.";

            case "for":
                return "Now u = " + e.u + ". Check one of its neighbors: w = " + e.w + ".";

            case "check_if":
                return "Check whether w = " + e.w + " has already been visited.";

            case "skip":
                return "w = " + e.w + " has already been visited, so skip it.";

            case "enqueue":
                return "w = " + e.w + " has not been visited, so add it to the queue.";

            case "record_parent":
                return "Record that w = " + e.w + " was first reached from u = " + e.u + ".";

            case "mark_child":
                return "Mark w = " + e.w + " as visited.";

            case "finish_vertex":
                return "All neighbors of u = " + e.u + " have been checked.";

            case "end":
                return "The queue is empty. BFS is complete.";

            default:
                return "";
        }
    }

    private int getHighlightedLine(String eventType) {
        switch (eventType) {
            case "init_queue":
                return 1;
            case "enqueue_start":
                return 2;
            case "mark_start":
                return 3;
            case "check_empty":
                return 5;
            case "dequeue":
                return 6;
            case "for":
                return 9;
            case "check_if":
            case "skip":
                return 10;
            case "enqueue":
                return 11;
            case "record_parent":
                return 12;
            case "mark_child":
                return 13;
            case "finish_vertex":
                return 16;
            case "end":
                return 17;
            default:
                return -1;
        }
    }

    private Integer getDisplayU(Event current) {
        switch (current.type) {
            case "dequeue":
            case "for":
            case "check_if":
            case "enqueue":
            case "record_parent":
            case "mark_child":
            case "skip":
            case "finish_vertex":
                return current.u;
            default:
                return null;
        }
    }

    private Integer getDisplayW(Event current) {
        switch (current.type) {
            case "for":
            case "check_if":
            case "enqueue":
            case "record_parent":
            case "mark_child":
            case "skip":
                return current.w;
            default:
                return null;
        }
    }

    private boolean shouldShowStartV() {
        Event current = events.get(stepIndex);

        return current.type.equals("init_queue")
                || current.type.equals("enqueue_start")
                || current.type.equals("mark_start");
    }

    private boolean isCurrentEdge(int a, int b) {
        Event current = events.get(stepIndex);

        if (!(current.type.equals("for")
                || current.type.equals("check_if")
                || current.type.equals("skip"))) {
            return false;
        }

        Integer u = getDisplayU(current);
        Integer w = getDisplayW(current);

        if (u == null || w == null) {
            return false;
        }

        return (a == u && b == w) || (a == w && b == u);
    }

    private class GraphPanel extends JPanel {
        GraphPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 465));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Event current = events.get(stepIndex);
            Set<Integer> visited = getVisitedNodes();
            Set<Integer> finished = getFinishedNodes();

            Integer displayU = getDisplayU(current);
            Integer displayW = getDisplayW(current);

            drawExplanationBox(g2, current);

            // Draw normal edges first.
            for (int[] edge : edges) {
                Point p1 = pos.get(edge[0]);
                Point p2 = pos.get(edge[1]);

                g2.setColor(COLOR_NORMAL_EDGE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // Keep all first-reached relations as green dashed arrows.
            for (Event e : getFirstReachedEvents()) {
                boolean isCurrentFirstReached =
                        current.type.equals("record_parent")
                                && current.u != null
                                && current.w != null
                                && e.u.equals(current.u)
                                && e.w.equals(current.w);

                drawFirstReachedArrow(
                        g2,
                        pos.get(e.u),
                        pos.get(e.w),
                        isCurrentFirstReached
                );
            }

            // Draw current checking edge on top of normal edges and first-reached arrows.
            for (int[] edge : edges) {
                if (isCurrentEdge(edge[0], edge[1])) {
                    Point p1 = pos.get(edge[0]);
                    Point p2 = pos.get(edge[1]);

                    g2.setColor(COLOR_CURRENT_EDGE);
                    g2.setStroke(new BasicStroke(5));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            for (int node : nodes) {
                Point p = pos.get(node);

                boolean isVisited = visited.contains(node);
                boolean isFinished = finished.contains(node);
                boolean isCurrentU = displayU != null && node == displayU;
                boolean isCurrentW = displayW != null && node == displayW;
                boolean showStartV = shouldShowStartV() && node == startVertex;

                if (isCurrentU) {
                    g2.setColor(COLOR_CURRENT_U);
                } else if (isCurrentW) {
                    g2.setColor(COLOR_CURRENT_W);
                } else if (isFinished) {
                    g2.setColor(COLOR_FINISHED);
                } else if (isVisited) {
                    g2.setColor(COLOR_IN_QUEUE);
                } else {
                    g2.setColor(COLOR_UNVISITED);
                }

                g2.fillOval(p.x - 30, p.y - 30, 60, 60);

                if (isCurrentU) {
                    g2.setColor(COLOR_CURRENT_U.darker());
                    g2.setStroke(new BasicStroke(4));
                } else if (isCurrentW) {
                    g2.setColor(COLOR_CURRENT_W.darker());
                    g2.setStroke(new BasicStroke(4));
                } else if (showStartV) {
                    g2.setColor(COLOR_CURRENT_U);
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(new Color(40, 40, 40));
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawOval(p.x - 30, p.y - 30, 60, 60);

                g2.setFont(new Font("Arial", Font.BOLD, 20));

                if (isCurrentU || isCurrentW || isFinished) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.BLACK);
                }

                FontMetrics fm = g2.getFontMetrics();
                String text = String.valueOf(node);
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();

                g2.drawString(text, p.x - textWidth / 2, p.y + textHeight / 2 - 3);

                if (isVisited) {
                    drawLargeTick(g2, p.x + 12, p.y - 20);
                }

                if (showStartV) {
                    drawSmallTag(g2, p.x - 48, p.y + 38, "v");
                }

                if (isCurrentU) {
                    drawSmallTag(g2, p.x - 52, p.y - 50, "u");
                }

                if (isCurrentW) {
                    drawSmallTag(g2, p.x + 26, p.y - 50, "w");
                }
            }

            drawLegend(g2);
        }

        private void drawFirstReachedArrow(Graphics2D g2, Point from, Point to, boolean current) {
            Stroke oldStroke = g2.getStroke();

            double angle = Math.atan2(to.y - from.y, to.x - from.x);

            int r = 36;
            int sx = (int) (from.x + r * Math.cos(angle));
            int sy = (int) (from.y + r * Math.sin(angle));
            int ex = (int) (to.x - r * Math.cos(angle));
            int ey = (int) (to.y - r * Math.sin(angle));

            float[] dash = current ? new float[]{10f, 5f} : new float[]{7f, 6f};
            float strokeWidth = current ? 4.2f : 2.8f;

            g2.setColor(COLOR_FIRST_REACHED);
            g2.setStroke(new BasicStroke(
                    strokeWidth,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    0,
                    dash,
                    0
            ));

            g2.drawLine(sx, sy, ex, ey);

            // Arrow head
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int arrowSize = current ? 12 : 9;
            int ax1 = (int) (ex - arrowSize * Math.cos(angle - Math.PI / 6));
            int ay1 = (int) (ey - arrowSize * Math.sin(angle - Math.PI / 6));
            int ax2 = (int) (ex - arrowSize * Math.cos(angle + Math.PI / 6));
            int ay2 = (int) (ey - arrowSize * Math.sin(angle + Math.PI / 6));

            g2.drawLine(ex, ey, ax1, ay1);
            g2.drawLine(ex, ey, ax2, ay2);

            g2.setStroke(oldStroke);

            if (current) {
                drawFirstReachedLabel(g2, sx, sy, ex, ey);
            }
        }

        private void drawFirstReachedLabel(Graphics2D g2, int sx, int sy, int ex, int ey) {
            String label = "first reached";
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();

            int labelX = (sx + ex) / 2 + 8;
            int labelY = (sy + ey) / 2 - 8;
            int labelW = fm.stringWidth(label) + 14;
            int labelH = 22;

            g2.setColor(new Color(255, 255, 255, 235));
            g2.fillRoundRect(labelX - 7, labelY - 16, labelW, labelH, 10, 10);

            g2.setColor(COLOR_FIRST_REACHED);
            g2.drawRoundRect(labelX - 7, labelY - 16, labelW, labelH, 10, 10);

            g2.setColor(new Color(30, 30, 30));
            g2.drawString(label, labelX, labelY);
        }

        private void drawExplanationBox(Graphics2D g2, Event current) {
            String explanation = getStepExplanation(current);

            int x = 20;
            int y = 18;
            int width = getWidth() - 40;
            int height = 58;

            g2.setColor(new Color(255, 248, 225));
            g2.fillRoundRect(x, y, width, height, 12, 12);

            g2.setColor(new Color(255, 193, 7));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width, height, 12, 12);

            g2.setColor(new Color(40, 40, 40));
            g2.setFont(new Font("Arial", Font.BOLD, 14));

            FontMetrics fm = g2.getFontMetrics();
            int maxTextWidth = width - 28;

            if (fm.stringWidth(explanation) <= maxTextWidth) {
                g2.drawString(explanation, x + 14, y + 35);
            } else {
                int splitIndex = explanation.length() / 2;

                for (int i = splitIndex; i > 0; i--) {
                    if (explanation.charAt(i) == ' ') {
                        splitIndex = i;
                        break;
                    }
                }

                String line1 = explanation.substring(0, splitIndex).trim();
                String line2 = explanation.substring(splitIndex).trim();

                g2.drawString(line1, x + 14, y + 26);
                g2.drawString(line2, x + 14, y + 45);
            }
        }

        private void drawSmallTag(Graphics2D g2, int x, int y, String text) {
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();

            int width = fm.stringWidth(text) + 14;
            int height = 20;

            g2.setColor(new Color(33, 37, 41));
            g2.fillRoundRect(x, y, width, height, 10, 10);

            g2.setColor(Color.WHITE);
            g2.drawString(text, x + 7, y + 15);
        }

        private void drawLargeTick(Graphics2D g2, int x, int y) {
            Stroke oldStroke = g2.getStroke();

            g2.setColor(COLOR_TICK);
            g2.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Larger tick
            g2.drawLine(x, y, x + 6, y + 7);
            g2.drawLine(x + 6, y + 7, x + 18, y - 9);

            g2.setStroke(oldStroke);
        }

        private void drawLegend(Graphics2D g2) {
            int x = 20;
            int y = getHeight() - 100;

            g2.setFont(new Font("Arial", Font.PLAIN, 13));

            drawLegendItem(g2, x, y, COLOR_UNVISITED, "Unvisited");
            drawLegendItem(g2, x, y + 25, COLOR_IN_QUEUE, "Visited / waiting");
            drawLegendItem(g2, x, y + 50, COLOR_FINISHED, "Finished");

            drawLegendItem(g2, x + 180, y, COLOR_CURRENT_U, "Current u");
            drawLegendItem(g2, x + 180, y + 25, COLOR_CURRENT_W, "Current w");
            drawLegendItem(g2, x + 180, y + 50, COLOR_CURRENT_EDGE, "Checking edge");

            drawTickLegend(g2, x + 350, y, "Visited tick");
            drawFirstReachedLegend(g2, x + 350, y + 35, "First reached");
        }

        private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
            g2.setColor(color);
            g2.fillOval(x, y - 12, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y - 12, 14, 14);
            g2.drawString(text, x + 24, y);
        }

        private void drawTickLegend(Graphics2D g2, int x, int y, String text) {
            drawLargeTick(g2, x, y - 4);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(text, x + 28, y);
        }

        private void drawFirstReachedLegend(Graphics2D g2, int x, int y, String text) {
            Stroke oldStroke = g2.getStroke();

            float[] dash = {6f, 5f};
            g2.setColor(COLOR_FIRST_REACHED);
            g2.setStroke(new BasicStroke(
                    3f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    0,
                    dash,
                    0
            ));

            g2.drawLine(x, y - 5, x + 26, y - 5);

            g2.setStroke(oldStroke);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(text, x + 34, y);
        }
    }

    private class QueuePanel extends JPanel {
        QueuePanel() {
            setBackground(new Color(248, 249, 251));
            setPreferredSize(new Dimension(600, 115));
            setBorder(new CompoundBorder(
                    new LineBorder(new Color(225, 228, 232), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Event current = events.get(stepIndex);
            List<Integer> queue = reconstructQueue();

            int startX = 95;
            int y = 45;
            int boxW = 58;
            int boxH = 42;
            int gap = 12;

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString("Queue", 20, 36);

            if (!queue.isEmpty()) {
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.setColor(new Color(90, 90, 90));
                g2.drawString("next out", startX, 30);
            }

            if (queue.isEmpty()) {
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("empty", startX, y + 27);
            }

            Integer newlyAdded = null;

            if (current.type.equals("enqueue_start")) {
                newlyAdded = current.u;
            } else if (current.type.equals("enqueue")) {
                newlyAdded = current.w;
            }

            for (int i = 0; i < queue.size(); i++) {
                int value = queue.get(i);
                int x = startX + i * (boxW + gap);

                boolean isNewlyEnqueued =
                        newlyAdded != null
                                && value == newlyAdded
                                && i == queue.size() - 1;

                double scale = 1.0;

                if (isNewlyEnqueued) {
                    scale = 1.0 + 0.18 * Math.sin(animationProgress * Math.PI);
                }

                int drawW = (int) (boxW * scale);
                int drawH = (int) (boxH * scale);
                int drawX = x - (drawW - boxW) / 2;
                int drawY = y - (drawH - boxH) / 2;

                if (isNewlyEnqueued) {
                    g2.setColor(COLOR_IN_QUEUE);
                } else {
                    g2.setColor(Color.WHITE);
                }

                g2.fillRoundRect(drawX, drawY, drawW, drawH, 10, 10);

                if (isNewlyEnqueued) {
                    g2.setColor(COLOR_CURRENT_U);
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(new Color(80, 80, 80));
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawRoundRect(drawX, drawY, drawW, drawH, 10, 10);

                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.setColor(Color.BLACK);

                String text = String.valueOf(value);
                FontMetrics fm = g2.getFontMetrics();
                int textX = drawX + (drawW - fm.stringWidth(text)) / 2;
                int textY = drawY + (drawH + fm.getAscent()) / 2 - 4;

                g2.drawString(text, textX, textY);

                if (i < queue.size() - 1) {
                    drawArrow(g2, x + boxW + 2, y + boxH / 2, x + boxW + gap - 4, y + boxH / 2);
                }
            }

            if (current.type.equals("enqueue") || current.type.equals("enqueue_start")) {
                int addedValue = current.type.equals("enqueue_start") ? current.u : current.w;

                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(new Color(230, 126, 34));
                g2.drawString("Enqueue: " + addedValue + " is added to the queue.",
                        20, getHeight() - 16);
            } else if (current.type.equals("dequeue")) {
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(new Color(220, 53, 69));
                g2.drawString("Take " + current.u + " from the queue and call it u.",
                        20, getHeight() - 16);
            } else if (current.type.equals("record_parent")) {
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(COLOR_FIRST_REACHED);
                g2.drawString("Record that " + current.w + " was first reached from " + current.u + ".",
                        20, getHeight() - 16);
            } else if (current.type.equals("finish_vertex")) {
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(COLOR_FINISHED);
                g2.drawString("Finished checking all neighbors of " + current.u + ".",
                        20, getHeight() - 16);
            } else {
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.setColor(new Color(90, 90, 90));
                g2.drawString("A tick means this vertex has been visited.",
                        20, getHeight() - 16);
            }
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.setColor(new Color(120, 120, 120));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x1, y1, x2, y2);

            int arrowSize = 6;
            g2.drawLine(x2, y2, x2 - arrowSize, y2 - arrowSize);
            g2.drawLine(x2, y2, x2 - arrowSize, y2 + arrowSize);
        }
    }

    private class CodePanel extends JPanel {
        CodePanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(470, 430));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int x = 20;
            int y = 28;
            int lineHeight = 24;
            int boxWidth = getWidth() - 40;

            Event current = events.get(stepIndex);
            int highlightLine = getHighlightedLine(current.type);

            g2.setFont(new Font("Consolas", Font.PLAIN, 14));

            for (int i = 0; i < codeLines.length; i++) {
                int lineY = y + i * lineHeight;

                if (i == highlightLine) {
                    g2.setColor(new Color(255, 243, 205));
                    g2.fillRoundRect(x - 8, lineY - 18, boxWidth, lineHeight, 8, 8);

                    g2.setColor(new Color(255, 193, 7));
                    g2.fillRoundRect(x - 8, lineY - 18, 5, lineHeight, 5, 5);
                }

                g2.setColor(new Color(30, 30, 30));
                g2.drawString(codeLines[i], x, lineY);
            }
        }
    }

    private static class Event {
        String type;
        Integer u;
        Integer w;

        Event(String type, Integer u, Integer w) {
            this.type = type;
            this.u = u;
            this.w = w;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BFSVisualization app = new BFSVisualization();
            app.setVisible(true);
        });
    }
}