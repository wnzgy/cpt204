import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DFSVisualization extends JFrame {

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
    private FocusPanel focusPanel;
    private JTextArea narrativeArea;

    private javax.swing.Timer animationTimer;
    private double animationProgress = 0.0;

    private final Color COLOR_UNVISITED = Color.WHITE;
    private final Color COLOR_VISITED = new Color(241, 196, 15);
    private final Color COLOR_CURRENT_V = new Color(230, 126, 34);
    private final Color COLOR_CURRENT_W = new Color(142, 68, 173);
    private final Color COLOR_FINISHED = new Color(46, 134, 193);
    private final Color COLOR_CURRENT_EDGE = new Color(231, 76, 60);
    private final Color COLOR_NORMAL_EDGE = new Color(170, 170, 170);
    private final Color COLOR_FIRST_REACHED = new Color(39, 174, 96);
    private final Color COLOR_TICK = new Color(39, 174, 96);

    private final String[] codeLines = {
            "DFS(vertex v) {",
            "    mark v as visited;",
            "",
            "    for each neighbor w of v {",
            "        if w has not been visited {",
            "            remember that w was found from v;",
            "            go deeper from w;",
            "        }",
            "    }",
            "    v is done;",
            "    go back to the previous vertex;",
            "}"
    };

    public DFSVisualization() {
        setTitle("DFS Visualization - Java Version");
        setSize(1150, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPositions();
        buildAdjacencyList();

        Set<Integer> visited = new HashSet<>();
        dfsCapture(startVertex, null, visited);

        buildUI();
        updateView(false);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        mainPanel.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Depth-First Search Visualization");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        codePanel = new CodePanel();
        focusPanel = new FocusPanel();

        JPanel graphCard = createCard("Graph Traversal");
        graphCard.setLayout(new BorderLayout(10, 10));
        graphCard.add(graphPanel, BorderLayout.CENTER);
        graphCard.add(focusPanel, BorderLayout.SOUTH);

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

        narrativeArea = new JTextArea(8, 60);
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

    private void dfsCapture(int v, Integer parent, Set<Integer> visited) {
        events.add(new Event("enter", v, parent));
        events.add(new Event("visit", v, parent));
        visited.add(v);

        for (int w : adj.get(v)) {
            events.add(new Event("for", v, w));
            events.add(new Event("if", v, w));

            if (!visited.contains(w)) {
                events.add(new Event("go_deeper", v, w));
                events.add(new Event("record_first", v, w));
                events.add(new Event("move_to_child", v, w));

                dfsCapture(w, v, visited);

                events.add(new Event("go_back", v, w));
            } else {
                events.add(new Event("skip", v, w));
            }
        }

        events.add(new Event("done", v, parent));
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
        focusPanel.repaint();
    }

    private Set<Integer> getVisitedNodes() {
        Set<Integer> visited = new TreeSet<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("visit")) {
                visited.add(e.v);
            }
        }

        return visited;
    }

    private Set<Integer> getFinishedNodes() {
        Set<Integer> finished = new TreeSet<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("done")) {
                finished.add(e.v);
            }
        }

        return finished;
    }

    private List<Event> getFirstReachedEvents() {
        List<Event> result = new ArrayList<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            if (e.type.equals("record_first")) {
                result.add(e);
            }
        }

        return result;
    }

    private String buildNarrative() {
        StringBuilder sb = new StringBuilder();
        List<Integer> active = new ArrayList<>();

        for (int i = 0; i <= stepIndex; i++) {
            Event e = events.get(i);

            String indent = "  ".repeat(active.size());

            switch (e.type) {
                case "enter":
                    sb.append(indent)
                            .append("[Focus] Start checking vertex ")
                            .append(e.v)
                            .append(".\n");
                    active.add(e.v);
                    break;

                case "visit":
                    sb.append(indent)
                            .append("[Visit] Mark vertex ")
                            .append(e.v)
                            .append(" as visited.\n");
                    break;

                case "for":
                    sb.append(indent)
                            .append("[Look around] From vertex ")
                            .append(e.v)
                            .append(", check its neighbors: ")
                            .append(adj.get(e.v))
                            .append("\n");
                    break;

                case "if":
                    sb.append(indent)
                            .append("[Check] Look at neighbor ")
                            .append(e.w)
                            .append(". Has it already been visited?\n");
                    break;

                case "go_deeper":
                    sb.append(indent)
                            .append("[Go deeper] Neighbor ")
                            .append(e.w)
                            .append(" is not visited, so move focus from ")
                            .append(e.v)
                            .append(" to ")
                            .append(e.w)
                            .append(".\n");
                    break;

                case "record_first":
                    sb.append(indent)
                            .append("[First reached] Remember that vertex ")
                            .append(e.w)
                            .append(" was first reached from vertex ")
                            .append(e.v)
                            .append(".\n");
                    break;

                case "move_to_child":
                    sb.append(indent)
                            .append("[Focus change] Now focus on vertex ")
                            .append(e.w)
                            .append(".\n");
                    break;

                case "skip":
                    sb.append(indent)
                            .append("[Skip] Neighbor ")
                            .append(e.w)
                            .append(" has already been visited, so do not visit it again.\n");
                    break;

                case "go_back":
                    sb.append(indent)
                            .append("[Go back] Vertex ")
                            .append(e.w)
                            .append(" is done. Focus returns to vertex ")
                            .append(e.v)
                            .append(" to continue checking other neighbors.\n");
                    break;

                case "done":
                    sb.append(indent)
                            .append("[Done] Vertex ")
                            .append(e.v)
                            .append(" has no unvisited neighbors left.\n\n");

                    if (!active.isEmpty()) {
                        active.remove(active.size() - 1);
                    }
                    break;

                default:
                    break;
            }
        }

        if (stepIndex == events.size() - 1) {
            sb.append(buildFirstReachedSummary());
        }

        return sb.toString();
    }

    private String buildFirstReachedSummary() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Extracted green dashed arrows ===\n");

        for (Event e : getFirstReachedEvents()) {
            sb.append(e.v)
                    .append(" -> ")
                    .append(e.w)
                    .append("    meaning: vertex ")
                    .append(e.w)
                    .append(" was first reached from vertex ")
                    .append(e.v)
                    .append("\n");
        }

        sb.append("\n");
        sb.append("Meaning of the green dashed arrows:\n");
        sb.append("- They show how DFS first discovered each vertex.\n");
        sb.append("- Together, they form the DFS search tree.\n");
        sb.append("- DFS goes as deep as possible first, then goes back when no unvisited neighbor remains.\n");

        return sb.toString();
    }

    private String getStepExplanation(Event e) {
        switch (e.type) {
            case "enter":
                return "Focus on vertex " + e.v + ". DFS will check this vertex first.";

            case "visit":
                return "Mark vertex " + e.v + " as visited.";

            case "for":
                return "From vertex " + e.v + ", check its neighbors one by one.";

            case "if":
                return "Check whether neighbor w = " + e.w + " has already been visited.";

            case "go_deeper":
                return "w = " + e.w + " is not visited, so DFS moves focus from " + e.v + " to " + e.w + ".";

            case "record_first":
                return "Remember that w = " + e.w + " was first reached from v = " + e.v + ".";

            case "move_to_child":
                return "Now focus on vertex " + e.w + " and continue DFS from there.";

            case "skip":
                return "w = " + e.w + " has already been visited, so skip it.";

            case "go_back":
                return "Vertex " + e.w + " is done. Move focus back to vertex " + e.v + ".";

            case "done":
                return "Vertex " + e.v + " is done because it has no unvisited neighbors left.";

            default:
                return "";
        }
    }

    private int getHighlightedLine(String eventType) {
        switch (eventType) {
            case "enter":
                return 0;
            case "visit":
                return 1;
            case "for":
            case "go_back":
                return 3;
            case "if":
            case "go_deeper":
            case "skip":
                return 4;
            case "record_first":
                return 5;
            case "move_to_child":
                return 6;
            case "done":
                return 9;
            default:
                return -1;
        }
    }

    private boolean isCurrentEdge(int a, int b) {
        Event current = events.get(stepIndex);

        if (!(current.type.equals("for")
                || current.type.equals("if")
                || current.type.equals("go_deeper")
                || current.type.equals("skip"))) {
            return false;
        }

        if (current.w == null) {
            return false;
        }

        return (a == current.v && b == current.w) || (a == current.w && b == current.v);
    }

    private boolean shouldDisplayW(Event e) {
        return e.type.equals("for")
                || e.type.equals("if")
                || e.type.equals("go_deeper")
                || e.type.equals("record_first")
                || e.type.equals("move_to_child")
                || e.type.equals("skip")
                || e.type.equals("go_back");
    }

    private Integer getFocusedVertex(Event e) {
        switch (e.type) {
            case "move_to_child":
                return e.w;

            case "go_back":
                return e.v;

            default:
                return e.v;
        }
    }

    private String getFocusStatus(Event e) {
        switch (e.type) {
            case "enter":
                return "Start checking this vertex.";

            case "visit":
                return "Mark this vertex as visited.";

            case "for":
                return "Check this vertex's neighbors.";

            case "if":
                return "Checking neighbor " + e.w + ".";

            case "go_deeper":
                return "Neighbor " + e.w + " is unvisited, so DFS will move deeper.";

            case "record_first":
                return "Record that " + e.w + " was first reached from " + e.v + ".";

            case "move_to_child":
                return "Focus moves to vertex " + e.w + ".";

            case "skip":
                return "Neighbor " + e.w + " is already visited, so skip it.";

            case "go_back":
                return "Go back to vertex " + e.v + " after vertex " + e.w + " is done.";

            case "done":
                return "This vertex has no unvisited neighbors left.";

            default:
                return "";
        }
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

            drawExplanationBox(g2, current);

            for (int[] edge : edges) {
                Point p1 = pos.get(edge[0]);
                Point p2 = pos.get(edge[1]);

                g2.setColor(COLOR_NORMAL_EDGE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            for (Event e : getFirstReachedEvents()) {
                boolean isCurrentFirstReached =
                        current.type.equals("record_first")
                                && e.v.equals(current.v)
                                && e.w.equals(current.w);

                drawFirstReachedArrow(g2, pos.get(e.v), pos.get(e.w), isCurrentFirstReached);
            }

            for (int[] edge : edges) {
                if (isCurrentEdge(edge[0], edge[1])) {
                    Point p1 = pos.get(edge[0]);
                    Point p2 = pos.get(edge[1]);

                    g2.setColor(COLOR_CURRENT_EDGE);
                    g2.setStroke(new BasicStroke(5));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            Integer focused = getFocusedVertex(current);

            for (int node : nodes) {
                Point p = pos.get(node);

                boolean isVisited = visited.contains(node);
                boolean isFinished = finished.contains(node);
                boolean isCurrentV = focused != null && node == focused && !current.type.equals("done");
                boolean isCurrentW = shouldDisplayW(current) && current.w != null && node == current.w;

                if (isCurrentV) {
                    g2.setColor(COLOR_CURRENT_V);
                } else if (isCurrentW) {
                    g2.setColor(COLOR_CURRENT_W);
                } else if (isFinished) {
                    g2.setColor(COLOR_FINISHED);
                } else if (isVisited) {
                    g2.setColor(COLOR_VISITED);
                } else {
                    g2.setColor(COLOR_UNVISITED);
                }

                g2.fillOval(p.x - 30, p.y - 30, 60, 60);

                if (isCurrentV) {
                    g2.setColor(COLOR_CURRENT_V.darker());
                    g2.setStroke(new BasicStroke(4));
                } else if (isCurrentW) {
                    g2.setColor(COLOR_CURRENT_W.darker());
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(new Color(40, 40, 40));
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawOval(p.x - 30, p.y - 30, 60, 60);

                g2.setFont(new Font("Arial", Font.BOLD, 20));

                if (isCurrentV || isCurrentW || isFinished) {
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

                if (isCurrentV) {
                    drawSmallTag(g2, p.x - 52, p.y - 50, "v");
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

            g2.drawLine(x, y, x + 6, y + 7);
            g2.drawLine(x + 6, y + 7, x + 18, y - 9);

            g2.setStroke(oldStroke);
        }

        private void drawLegend(Graphics2D g2) {
            int x = 20;
            int y = getHeight() - 100;

            g2.setFont(new Font("Arial", Font.PLAIN, 13));

            drawLegendItem(g2, x, y, COLOR_UNVISITED, "Unvisited");
            drawLegendItem(g2, x, y + 25, COLOR_VISITED, "Visited");
            drawLegendItem(g2, x, y + 50, COLOR_FINISHED, "Done");

            drawLegendItem(g2, x + 155, y, COLOR_CURRENT_V, "Focused v");
            drawLegendItem(g2, x + 155, y + 25, COLOR_CURRENT_W, "Current w");
            drawLegendItem(g2, x + 155, y + 50, COLOR_CURRENT_EDGE, "Checking edge");

            drawTickLegend(g2, x + 335, y, "Visited tick");
            drawFirstReachedLegend(g2, x + 335, y + 35, "First reached");
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

    private class FocusPanel extends JPanel {
        FocusPanel() {
            setBackground(new Color(248, 249, 251));
            setPreferredSize(new Dimension(600, 105));
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
            Integer focused = getFocusedVertex(current);

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString("Focused Vertex", 20, 34);

            int circleX = 165;
            int circleY = 46;
            int r = 28;

            if (focused == null) {
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("none", circleX - 20, circleY + 6);
            } else {
                g2.setColor(COLOR_CURRENT_V);
                g2.fillOval(circleX - r, circleY - r, r * 2, r * 2);

                g2.setColor(COLOR_CURRENT_V.darker());
                g2.setStroke(new BasicStroke(4));
                g2.drawOval(circleX - r, circleY - r, r * 2, r * 2);

                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.setColor(Color.WHITE);

                String text = String.valueOf(focused);
                FontMetrics fm = g2.getFontMetrics();

                g2.drawString(
                        text,
                        circleX - fm.stringWidth(text) / 2,
                        circleY + fm.getAscent() / 2 - 4
                );
            }

            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(50, 50, 50));
            g2.drawString("What DFS is doing now:", 230, 36);

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(new Color(90, 90, 90));

            String status = getFocusStatus(current);
            drawWrappedText(g2, status, 230, 58, getWidth() - 250, 18);

            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setColor(new Color(110, 110, 110));
            g2.drawString("DFS focuses on one vertex, checks its neighbors, goes deeper if possible, otherwise goes back.",
                    20, getHeight() - 14);
        }

        private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
            FontMetrics fm = g2.getFontMetrics();

            if (fm.stringWidth(text) <= maxWidth) {
                g2.drawString(text, x, y);
                return;
            }

            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();
            int currentY = y;

            for (String word : words) {
                String testLine = line.length() == 0 ? word : line + " " + word;

                if (fm.stringWidth(testLine) > maxWidth) {
                    g2.drawString(line.toString(), x, currentY);
                    line = new StringBuilder(word);
                    currentY += lineHeight;
                } else {
                    line = new StringBuilder(testLine);
                }
            }

            if (line.length() > 0) {
                g2.drawString(line.toString(), x, currentY);
            }
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
            int y = 32;
            int lineHeight = 28;
            int boxWidth = getWidth() - 40;

            Event current = events.get(stepIndex);
            int highlightLine = getHighlightedLine(current.type);

            g2.setFont(new Font("Consolas", Font.PLAIN, 15));

            for (int i = 0; i < codeLines.length; i++) {
                int lineY = y + i * lineHeight;

                if (i == highlightLine) {
                    g2.setColor(new Color(255, 243, 205));
                    g2.fillRoundRect(x - 8, lineY - 20, boxWidth, lineHeight, 8, 8);

                    g2.setColor(new Color(255, 193, 7));
                    g2.fillRoundRect(x - 8, lineY - 20, 5, lineHeight, 5, 5);
                }

                g2.setColor(new Color(30, 30, 30));
                g2.drawString(codeLines[i], x, lineY);
            }
        }
    }

    private static class Event {
        String type;
        Integer v;
        Integer w;

        Event(String type, Integer v, Integer w) {
            this.type = type;
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DFSVisualization app = new DFSVisualization();
            app.setVisible(true);
        });
    }
}