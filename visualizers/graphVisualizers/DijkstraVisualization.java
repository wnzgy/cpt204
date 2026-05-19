import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraVisualization extends JFrame {

    private static final int VERTEX_RADIUS = 30;

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<String, Vertex> vertexMap = new HashMap<>();

    private final DijkstraStepper stepper;

    private GraphPanel graphPanel;
    private CodePanel codePanel;
    private TablePanel tablePanel;
    private JTextArea narrativeArea;
    private JLabel statusLabel;

    private StepResult currentStep;

    private final Color COLOR_UNVISITED = Color.WHITE;
    private final Color COLOR_IN_T = new Color(46, 134, 193);
    private final Color COLOR_CURRENT_U = new Color(230, 126, 34);
    private final Color COLOR_CURRENT_V = new Color(142, 68, 173);
    private final Color COLOR_NORMAL_EDGE = new Color(170, 170, 170);
    private final Color COLOR_CANDIDATE_EDGE = new Color(241, 196, 15);
    private final Color COLOR_CHECKING_EDGE = new Color(231, 76, 60);
    private final Color COLOR_SHORTEST_PATH_EDGE = new Color(39, 174, 96);
    private final Color COLOR_T_REGION = new Color(52, 152, 219);
    private final Color COLOR_BEST_KNOWN = new Color(41, 128, 185);

    private final String[] codeLines = {
            "ShortestPathTree getShortestPath(s) {",
            "    let T be the set of vertices whose shortest paths are known;",
            "    set cost[s] = 0;",
            "    set cost[v] = INF for all other vertices;",
            "    set parent[v] = -1 for all vertices;",
            "",
            "    while size of T < number of vertices {",
            "        find u not in T with the smallest cost[u];",
            "        add u to T;",
            "",
            "        for each edge (u, v) from u {",
            "            if v is not in T and",
            "               cost[v] > cost[u] + weight(u, v) {",
            "                cost[v] = cost[u] + weight(u, v);",
            "                parent[v] = u;",
            "            }",
            "        }",
            "    }",
            "}"
    };

    public DijkstraVisualization() {
        setTitle("Dijkstra Shortest Path Visualization - Java Version");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initGraph();

        Vertex source = vertexMap.get("A");
        stepper = new DijkstraStepper(vertices, edges, source);

        buildUI();

        currentStep = new StepResult("init", 0, null, null);
        updateView();
    }

    private void initGraph() {
        // Wider layout to reduce overlap between weights, cost labels, and edges.
        addVertex("A", 390, 320);
        addVertex("B", 120, 320);
        addVertex("C", 390, 115);
        addVertex("D", 670, 320);
        addVertex("E", 390, 525);

        // Undirected weighted graph.
        addUndirectedEdge("A", "B", 4);
        addUndirectedEdge("A", "C", 2);
        addUndirectedEdge("A", "D", 9);
        addUndirectedEdge("A", "E", 12);

        addUndirectedEdge("B", "C", 1);
        addUndirectedEdge("B", "E", 5);
        addUndirectedEdge("C", "D", 8);
        addUndirectedEdge("C", "E", 10);
        addUndirectedEdge("D", "E", 3);
    }

    private void addVertex(String id, int x, int y) {
        Vertex v = new Vertex(id, x, y);
        vertices.add(v);
        vertexMap.put(id, v);
    }

    private void addUndirectedEdge(String a, String b, int weight) {
        Vertex u = vertexMap.get(a);
        Vertex v = vertexMap.get(b);

        edges.add(new Edge(u, v, weight));
        edges.add(new Edge(v, u, weight));
    }

    private String edgeKey(Vertex a, Vertex b) {
        if (a.id.compareTo(b.id) < 0) {
            return a.id + "-" + b.id;
        }
        return b.id + "-" + a.id;
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        mainPanel.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Dijkstra's Shortest Path Visualization");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        codePanel = new CodePanel();
        tablePanel = new TablePanel();

        JPanel graphCard = createCard("Graph Traversal");
        graphCard.setLayout(new BorderLayout(10, 10));
        graphCard.add(graphPanel, BorderLayout.CENTER);
        graphCard.add(tablePanel, BorderLayout.SOUTH);

        JPanel codeCard = createCard("Pseudocode");
        codeCard.setLayout(new BorderLayout());
        codeCard.add(codePanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphCard, codeCard);
        splitPane.setResizeWeight(0.70);
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

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);

        statusLabel = new JLabel("u = -, v = -");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(new EmptyBorder(0, 0, 0, 20));

        JButton resetButton = createButton("Reset");
        JButton nextButton = createButton("Next Step");

        resetButton.addActionListener(e -> reset());
        nextButton.addActionListener(e -> nextStep());

        controlPanel.add(statusLabel);
        controlPanel.add(resetButton);
        controlPanel.add(nextButton);

        bottomPanel.add(narrativeCard, BorderLayout.CENTER);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);

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

    private void nextStep() {
        currentStep = stepper.next();
        updateView();
    }

    private void reset() {
        stepper.reset();
        currentStep = new StepResult("init", 0, null, null);
        updateView();
    }

    private void updateView() {
        graphPanel.repaint();
        codePanel.repaint();
        tablePanel.repaint();

        narrativeArea.setText(buildNarrative());
        narrativeArea.setCaretPosition(narrativeArea.getDocument().getLength());

        Vertex u = stepper.u;
        Vertex v = currentStep.node;
        statusLabel.setText("u = " + (u == null ? "-" : u.id)
                + ", v = " + (v == null || v == u ? "-" : v.id));
    }

    private String buildNarrative() {
        StringBuilder sb = new StringBuilder();

        for (String line : stepper.trace) {
            sb.append(line).append("\n");
        }

        if (stepper.done) {
            sb.append("\n");
            sb.append("=== Final shortest paths from source A ===\n");

            for (Vertex v : vertices) {
                sb.append("A -> ").append(v.id)
                        .append("    cost = ").append(formatCost(stepper.cost.get(v)))
                        .append("    path = ").append(buildPathText(v))
                        .append("\n");
            }

            sb.append("\nMeaning:\n");
            sb.append("- cost[v] is the shortest known distance from source A to v.\n");
            sb.append("- parent[v] records the previous vertex on the current shortest path.\n");
            sb.append("- The blue dashed line shows the current best known path edge to an unfinished vertex.\n");
            sb.append("- The green arrows form the finalized shortest-path tree.\n");
        }

        return sb.toString();
    }

    private String buildPathText(Vertex target) {
        LinkedList<String> path = new LinkedList<>();
        Vertex current = target;
        Set<Vertex> seen = new HashSet<>();

        while (current != null && !seen.contains(current)) {
            seen.add(current);
            path.addFirst(current.id);
            current = stepper.parent.get(current);
        }

        return String.join(" -> ", path);
    }

    private Edge findEdge(Vertex a, Vertex b) {
        for (Edge e : edges) {
            if (e.u == a && e.v == b) {
                return e;
            }
        }
        return null;
    }

    private List<Edge> getShortestPathTreeEdges() {
        List<Edge> result = new ArrayList<>();

        for (Vertex v : vertices) {
            Vertex p = stepper.parent.get(v);
            if (p != null && stepper.T.contains(v)) {
                Edge e = findEdge(p, v);
                if (e != null) {
                    result.add(e);
                }
            }
        }

        return result;
    }

    private List<Edge> getBestKnownEdgesToOutsideT() {
        List<Edge> result = new ArrayList<>();

        for (Vertex v : vertices) {
            Vertex p = stepper.parent.get(v);

            if (p != null && !stepper.T.contains(v)) {
                Edge e = findEdge(p, v);
                if (e != null) {
                    result.add(e);
                }
            }
        }

        return result;
    }

    private List<Edge> getPotentialEdgesFromCurrentU() {
        List<Edge> result = new ArrayList<>();

        if (stepper.u == null) {
            return result;
        }

        for (Edge e : edges) {
            if (e.u == stepper.u && !stepper.T.contains(e.v)) {
                result.add(e);
            }
        }

        return result;
    }

    private class GraphPanel extends JPanel {
        GraphPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(850, 570));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawExplanationBox(g2);
            drawTRegion(g2);
            drawEdges(g2);
            drawBestKnownCostEdges(g2);
            drawShortestPathTreeEdges(g2);
            drawVertices(g2);
            drawLegend(g2);
        }

        private void drawExplanationBox(Graphics2D g2) {
            String explanation = getStepExplanation(currentStep);

            int x = 20;
            int y = 18;
            int width = getWidth() - 40;
            int height = 62;

            g2.setColor(new Color(255, 248, 225));
            g2.fillRoundRect(x, y, width, height, 12, 12);

            g2.setColor(new Color(255, 193, 7));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width, height, 12, 12);

            g2.setColor(new Color(40, 40, 40));
            g2.setFont(new Font("Arial", Font.BOLD, 14));

            drawWrappedText(g2, explanation, x + 14, y + 25, width - 28, 18);
        }

        private void drawTRegion(Graphics2D g2) {
            if (stepper.T.isEmpty()) {
                return;
            }

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (Vertex v : stepper.T) {
                minX = Math.min(minX, v.x);
                minY = Math.min(minY, v.y);
                maxX = Math.max(maxX, v.x);
                maxY = Math.max(maxY, v.y);
            }

            int pad = 58;

            g2.setColor(new Color(52, 152, 219, 35));
            g2.fillRoundRect(minX - pad, minY - pad, maxX - minX + 2 * pad, maxY - minY + 2 * pad, 22, 22);

            g2.setColor(COLOR_T_REGION);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8f, 6f}, 0));
            g2.drawRoundRect(minX - pad, minY - pad, maxX - minX + 2 * pad, maxY - minY + 2 * pad, 22, 22);
        }

        private void drawEdges(Graphics2D g2) {
            Set<String> drawn = new HashSet<>();

            for (Edge e : edges) {
                String key = edgeKey(e.u, e.v);
                if (drawn.contains(key)) {
                    continue;
                }
                drawn.add(key);

                Color edgeColor = COLOR_NORMAL_EDGE;
                float width = 2f;

                if (isPotentialEdge(e)) {
                    edgeColor = COLOR_CANDIDATE_EDGE;
                    width = 4f;
                }

                if (currentStep.edge != null
                        && edgeKey(currentStep.edge.u, currentStep.edge.v).equals(key)
                        && (currentStep.type.equals("for_check") || currentStep.type.equals("if_check"))) {
                    edgeColor = COLOR_CHECKING_EDGE;
                    width = 5f;
                }

                g2.setColor(edgeColor);
                g2.setStroke(new BasicStroke(width));
                g2.drawLine(e.u.x, e.u.y, e.v.x, e.v.y);

                drawWeight(g2, e);
            }
        }

        private boolean isPotentialEdge(Edge e) {
            if (stepper.u == null) {
                return false;
            }

            for (Edge p : getPotentialEdgesFromCurrentU()) {
                if (edgeKey(p.u, p.v).equals(edgeKey(e.u, e.v))) {
                    return true;
                }
            }

            return false;
        }

        private void drawWeight(Graphics2D g2, Edge e) {
            int mx = (e.u.x + e.v.x) / 2;
            int my = (e.u.y + e.v.y) / 2;

            int dx = e.v.x - e.u.x;
            int dy = e.v.y - e.u.y;
            double len = Math.sqrt(dx * dx + dy * dy);

            int ox = 0;
            int oy = 0;

            if (len != 0) {
                ox = (int) (-dy / len * 12);
                oy = (int) (dx / len * 12);
            }

            String text = String.valueOf(e.weight);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            int x = mx + ox;
            int y = my + oy;

            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(x - fm.stringWidth(text) / 2 - 5, y - 15, fm.stringWidth(text) + 10, 20, 8, 8);

            g2.setColor(new Color(50, 50, 50));
            g2.drawString(text, x - fm.stringWidth(text) / 2, y);
        }

        private void drawBestKnownCostEdges(Graphics2D g2) {
            for (Edge e : getBestKnownEdgesToOutsideT()) {
                drawDashedConnection(g2, e.u, e.v, COLOR_BEST_KNOWN, 3.2f);
            }
        }

        private void drawDashedConnection(Graphics2D g2, Vertex from, Vertex to, Color color, float width) {
            Stroke oldStroke = g2.getStroke();

            double angle = Math.atan2(to.y - from.y, to.x - from.x);

            int sx = (int) (from.x + VERTEX_RADIUS * Math.cos(angle));
            int sy = (int) (from.y + VERTEX_RADIUS * Math.sin(angle));
            int ex = (int) (to.x - VERTEX_RADIUS * Math.cos(angle));
            int ey = (int) (to.y - VERTEX_RADIUS * Math.sin(angle));

            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                    width,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    0,
                    new float[]{8f, 6f},
                    0
            ));

            g2.drawLine(sx, sy, ex, ey);
            g2.setStroke(oldStroke);
        }

        private void drawShortestPathTreeEdges(Graphics2D g2) {
            for (Edge e : getShortestPathTreeEdges()) {
                drawArrow(g2, e.u, e.v, COLOR_SHORTEST_PATH_EDGE, 4.4f);
            }
        }

        private void drawArrow(Graphics2D g2, Vertex from, Vertex to, Color color, float width) {
            Stroke oldStroke = g2.getStroke();

            double angle = Math.atan2(to.y - from.y, to.x - from.x);

            int sx = (int) (from.x + VERTEX_RADIUS * Math.cos(angle));
            int sy = (int) (from.y + VERTEX_RADIUS * Math.sin(angle));
            int ex = (int) (to.x - VERTEX_RADIUS * Math.cos(angle));
            int ey = (int) (to.y - VERTEX_RADIUS * Math.sin(angle));

            g2.setColor(color);
            g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(sx, sy, ex, ey);

            int arrowSize = 11;
            int ax1 = (int) (ex - arrowSize * Math.cos(angle - Math.PI / 6));
            int ay1 = (int) (ey - arrowSize * Math.sin(angle - Math.PI / 6));
            int ax2 = (int) (ex - arrowSize * Math.cos(angle + Math.PI / 6));
            int ay2 = (int) (ey - arrowSize * Math.sin(angle + Math.PI / 6));

            g2.drawLine(ex, ey, ax1, ay1);
            g2.drawLine(ex, ey, ax2, ay2);

            g2.setStroke(oldStroke);
        }

        private void drawVertices(Graphics2D g2) {
            for (Vertex v : vertices) {
                boolean inT = stepper.T.contains(v);
                boolean isU = stepper.u == v;
                boolean isCurrentV = currentStep.node == v && v != stepper.u;

                if (isU) {
                    g2.setColor(COLOR_CURRENT_U);
                } else if (isCurrentV) {
                    g2.setColor(COLOR_CURRENT_V);
                } else if (inT) {
                    g2.setColor(COLOR_IN_T);
                } else {
                    g2.setColor(COLOR_UNVISITED);
                }

                g2.fillOval(v.x - VERTEX_RADIUS, v.y - VERTEX_RADIUS, VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

                if (isU) {
                    g2.setColor(COLOR_CURRENT_U.darker());
                    g2.setStroke(new BasicStroke(4));
                } else if (isCurrentV) {
                    g2.setColor(COLOR_CURRENT_V.darker());
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawOval(v.x - VERTEX_RADIUS, v.y - VERTEX_RADIUS, VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.setColor((isU || isCurrentV || inT) ? Color.WHITE : Color.BLACK);

                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(v.id, v.x - fm.stringWidth(v.id) / 2, v.y + fm.getAscent() / 2 - 3);

                drawCostNearVertex(g2, v);

                if (isU) {
                    drawSmallTag(g2, v.x + 22, v.y - 48, "u");
                }

                if (isCurrentV) {
                    drawSmallTag(g2, v.x + 22, v.y - 48, "v");
                }
            }
        }

        private void drawCostNearVertex(Graphics2D g2, Vertex v) {
            String costText = formatCost(stepper.cost.get(v));
            String parentText = stepper.parent.get(v) == null ? "-1" : stepper.parent.get(v).id;

            String label;

            if (stepper.parent.get(v) == null) {
                label = "cost = " + costText;
            } else {
                label = "cost = " + costText + ", from " + parentText;
            }

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            int labelW = fm.stringWidth(label) + 14;
            int labelH = 22;

            int x;
            int y;

            switch (v.id) {
                case "A":
                    x = v.x + VERTEX_RADIUS + 18;
                    y = v.y + VERTEX_RADIUS + 32;
                    break;
                case "B":
                    x = v.x + VERTEX_RADIUS + 12;
                    y = v.y + VERTEX_RADIUS + 28;
                    break;
                case "C":
                    x = v.x + VERTEX_RADIUS + 12;
                    y = v.y - VERTEX_RADIUS - 12;
                    break;
                case "D":
                    // Put D's cost label on the right side of vertex D.
                    x = v.x + VERTEX_RADIUS + 12;
                    y = v.y + VERTEX_RADIUS + 28;
                    break;
                case "E":
                    x = v.x + VERTEX_RADIUS + 12;
                    y = v.y - VERTEX_RADIUS - 12;
                    break;
                default:
                    x = v.x + VERTEX_RADIUS + 12;
                    y = v.y + VERTEX_RADIUS + 20;
            }

            x = Math.max(8, Math.min(x, getWidth() - labelW - 8));
            y = Math.max(95, Math.min(y, getHeight() - 125));

            g2.setColor(new Color(255, 255, 255, 245));
            g2.fillRoundRect(x, y - labelH + 5, labelW, labelH, 8, 8);

            g2.setColor(COLOR_BEST_KNOWN);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y - labelH + 5, labelW, labelH, 8, 8);

            g2.setColor(COLOR_BEST_KNOWN.darker());
            g2.drawString(label, x + 7, y);
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

        private void drawLegend(Graphics2D g2) {
            int x = 20;
            int y = getHeight() - 95;

            g2.setFont(new Font("Arial", Font.PLAIN, 13));

            drawLegendItem(g2, x, y, COLOR_IN_T, "Finalized in T");
            drawLegendItem(g2, x, y + 25, COLOR_CURRENT_U, "Current u");
            drawLegendItem(g2, x + 155, y, COLOR_CURRENT_V, "Current v");
            drawLegendItem(g2, x + 155, y + 25, COLOR_CANDIDATE_EDGE, "Edges from u");
            drawLegendLine(g2, x + 330, y - 6, COLOR_CHECKING_EDGE, "Checking edge", false, false);
            drawLegendLine(g2, x + 330, y + 19, COLOR_BEST_KNOWN, "Best known path edge", false, true);
            drawLegendLine(g2, x + 580, y - 6, COLOR_SHORTEST_PATH_EDGE, "Shortest-path tree edge", true, false);
        }

        private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
            g2.setColor(color);
            g2.fillOval(x, y - 12, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y - 12, 14, 14);
            g2.drawString(text, x + 24, y);
        }

        private void drawLegendLine(Graphics2D g2, int x, int y, Color color, String text, boolean arrow, boolean dashed) {
            Stroke oldStroke = g2.getStroke();

            if (dashed) {
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{7f, 5f}, 0));
            } else {
                g2.setStroke(new BasicStroke(4));
            }

            g2.setColor(color);
            g2.drawLine(x, y, x + 26, y);

            if (arrow) {
                g2.drawLine(x + 26, y, x + 18, y - 5);
                g2.drawLine(x + 26, y, x + 18, y + 5);
            }

            g2.setStroke(oldStroke);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(text, x + 36, y + 5);
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
            setPreferredSize(new Dimension(430, 500));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int x = 18;
            int y = 32;
            int lineHeight = 26;
            int boxWidth = getWidth() - 36;

            int highlightLine = currentStep.codeLine;

            g2.setFont(new Font("Consolas", Font.PLAIN, 12));

            for (int i = 0; i < codeLines.length; i++) {
                int lineY = y + i * lineHeight;

                if (i == highlightLine) {
                    g2.setColor(new Color(255, 243, 205));
                    g2.fillRoundRect(x - 8, lineY - 19, boxWidth, lineHeight, 8, 8);

                    g2.setColor(new Color(255, 193, 7));
                    g2.fillRoundRect(x - 8, lineY - 19, 5, lineHeight, 5, 5);
                }

                g2.setColor(new Color(30, 30, 30));
                g2.drawString(codeLines[i], x, lineY);
            }
        }
    }

    private class TablePanel extends JPanel {
        TablePanel() {
            setBackground(new Color(248, 249, 251));
            setPreferredSize(new Dimension(850, 115));
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

            int startX = 85;
            int startY = 20;
            int cellW = 95;
            int cellH = 26;

            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(30, 30, 30));
            g2.drawString("cost", 20, startY + cellH - 7);
            g2.drawString("parent", 20, startY + 2 * cellH - 7);
            g2.drawString("vertex", 20, startY + 3 * cellH - 7);

            for (int i = 0; i < vertices.size(); i++) {
                Vertex v = vertices.get(i);
                int x = startX + i * cellW;

                drawTableCell(g2, x, startY, cellW, cellH, formatCost(stepper.cost.get(v)),
                        currentStep.type.equals("update_cost") && currentStep.node == v);

                String parentText = stepper.parent.get(v) == null ? "-1" : stepper.parent.get(v).id;

                drawTableCell(g2, x, startY + cellH, cellW, cellH, parentText,
                        currentStep.type.equals("update_parent") && currentStep.node == v);

                drawTableCell(g2, x, startY + 2 * cellH, cellW, cellH, v.id, false);
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setColor(new Color(90, 90, 90));
            g2.drawString("cost[v] = shortest known distance from source A to v; parent[v] = previous vertex on that path.",
                    20, getHeight() - 10);
        }

        private void drawTableCell(Graphics2D g2, int x, int y, int w, int h, String text, boolean highlight) {
            if (highlight) {
                g2.setColor(new Color(255, 243, 205));
            } else {
                g2.setColor(Color.WHITE);
            }

            g2.fillRoundRect(x, y, w - 6, h - 4, 8, 8);

            if (highlight) {
                g2.setColor(new Color(255, 193, 7));
                g2.setStroke(new BasicStroke(3));
            } else {
                g2.setColor(new Color(190, 190, 190));
                g2.setStroke(new BasicStroke(1));
            }

            g2.drawRoundRect(x, y, w - 6, h - 4, 8, 8);

            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(Color.BLACK);

            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - 6 - fm.stringWidth(text)) / 2;
            int ty = y + (h - 4 + fm.getAscent()) / 2 - 4;

            g2.drawString(text, tx, ty);
        }
    }

    private String getStepExplanation(StepResult step) {
        switch (step.type) {
            case "init_T":
                return "Create T. T stores vertices whose shortest paths from source A are already known.";

            case "init_cost":
                return "Set cost[A] = 0. For all other vertices, set cost[v] = INF because no path is known yet.";

            case "init_parent":
                return "Set parent[v] = -1 for all vertices. Parent will later record the previous vertex on the shortest path.";

            case "while_check":
                return "Check whether all vertices have been added to T. If not, continue the loop.";

            case "find_min":
                return "Find u outside T with the smallest cost[u]. This vertex has the next finalized shortest path.";

            case "add_to_T":
                return "Add u to T. The shortest path from A to u is now finalized.";

            case "for_prep":
                return "Now check every edge (u, v) from u to a vertex outside T.";

            case "for_check":
                return "Check this edge (u, v). The possible new distance to v is cost[u] + weight(u, v).";

            case "if_check":
                return "Compare current cost[v] with cost[u] + weight(u, v). Update only if the new path is shorter.";

            case "update_cost":
                return "Update cost[v] to cost[u] + weight(u, v). This is the new shortest known distance to v.";

            case "update_parent":
                return "Update parent[v] = u. This records that the best current path to v comes through u.";

            case "done":
                return "All vertices are in T. The green arrows form the shortest-path tree from source A.";

            case "init":
            default:
                return "Click Next Step to start Dijkstra's algorithm from source vertex A.";
        }
    }

    private String formatCost(Double cost) {
        if (cost == null || cost.isInfinite()) {
            return "INF";
        }

        if (Math.abs(cost - Math.round(cost)) < 0.0001) {
            return String.valueOf((int) Math.round(cost));
        }

        return String.format("%.1f", cost);
    }

    private static class DijkstraStepper {
        private final List<Vertex> V;
        private final List<Edge> E;
        private final Vertex source;

        Map<Vertex, Double> cost = new HashMap<>();
        Map<Vertex, Vertex> parent = new HashMap<>();
        Set<Vertex> T = new LinkedHashSet<>();
        List<String> trace = new ArrayList<>();

        Vertex u = null;
        Edge currentEdge = null;
        Iterator<Edge> edgeIterator = null;
        boolean done = false;

        private State state = State.INIT_T;

        enum State {
            INIT_T,
            INIT_COST,
            INIT_PARENT,
            WHILE_CHECK,
            FIND_MIN,
            ADD_TO_T,
            FOR_PREP,
            FOR_CHECK,
            IF_CHECK,
            UPDATE_COST,
            UPDATE_PARENT,
            DONE
        }

        DijkstraStepper(List<Vertex> V, List<Edge> E, Vertex source) {
            this.V = V;
            this.E = E;
            this.source = source;
            reset();
        }

        void reset() {
            cost.clear();
            parent.clear();
            T.clear();
            trace.clear();

            for (Vertex v : V) {
                cost.put(v, Double.POSITIVE_INFINITY);
                parent.put(v, null);
            }

            cost.put(source, 0.0);

            u = null;
            currentEdge = null;
            edgeIterator = null;
            done = false;
            state = State.INIT_T;
        }

        StepResult next() {
            if (done) {
                return new StepResult("done", 18, null, null);
            }

            switch (state) {
                case INIT_T:
                    trace.add("[Init] Create T as an empty set. T stores vertices whose shortest paths are known.");
                    state = State.INIT_COST;
                    return new StepResult("init_T", 1, null, null);

                case INIT_COST:
                    trace.add("[Init] cost[A] = 0; cost[v] = INF for all other vertices.");
                    state = State.INIT_PARENT;
                    return new StepResult("init_cost", 2, source, null);

                case INIT_PARENT:
                    trace.add("[Init] parent[v] = -1 for all vertices.");
                    state = State.WHILE_CHECK;
                    return new StepResult("init_parent", 4, null, null);

                case WHILE_CHECK:
                    if (T.size() < V.size()) {
                        trace.add("[While] T has " + T.size() + " of " + V.size() + " vertices. Continue.");
                        state = State.FIND_MIN;
                        return new StepResult("while_check", 6, null, null);
                    } else {
                        trace.add("[Done] T contains all vertices. Dijkstra's algorithm is complete.");
                        state = State.DONE;
                        done = true;
                        return new StepResult("done", 18, null, null);
                    }

                case FIND_MIN:
                    u = findMinOutsideT();

                    if (u == null || cost.get(u).isInfinite()) {
                        trace.add("[Find u] No reachable vertex remains outside T. Stop.");
                        state = State.DONE;
                        done = true;
                        return new StepResult("done", 18, null, null);
                    }

                    trace.add("[Find u] Choose u = " + u.id
                            + " because it has the smallest cost outside T: cost[" + u.id + "] = "
                            + format(cost.get(u)) + ".");
                    state = State.ADD_TO_T;
                    return new StepResult("find_min", 7, u, null);

                case ADD_TO_T:
                    T.add(u);

                    if (parent.get(u) == null) {
                        trace.add("[Add to T] Add " + u.id + " to T. It is the source vertex, so no parent edge is added.");
                    } else {
                        Vertex p = parent.get(u);
                        trace.add("[Add to T] Add " + u.id
                                + " to T. The shortest-path tree edge is " + p.id + " -> " + u.id + ".");
                    }

                    state = State.FOR_PREP;
                    return new StepResult("add_to_T", 8, u, null);

                case FOR_PREP:
                    edgeIterator = outgoingEdgesFromU();
                    trace.add("[For] Check every edge from u = " + u.id + " to vertices outside T.");
                    state = State.FOR_CHECK;
                    return new StepResult("for_prep", 10, u, null);

                case FOR_CHECK:
                    if (edgeIterator.hasNext()) {
                        currentEdge = edgeIterator.next();
                        Vertex v = currentEdge.v;
                        trace.add("[Edge] Check edge (" + u.id + ", " + v.id + ") with weight " + currentEdge.weight + ".");
                        state = State.IF_CHECK;
                        return new StepResult("for_check", 10, v, currentEdge);
                    } else {
                        trace.add("[For done] No more useful outgoing edges from u = " + u.id + ". Go back to the while-loop.");
                        state = State.WHILE_CHECK;
                        return new StepResult("while_check", 6, null, null);
                    }

                case IF_CHECK:
                    Vertex v = currentEdge.v;
                    double newCost = cost.get(u) + currentEdge.weight;

                    if (!T.contains(v) && cost.get(v) > newCost) {
                        trace.add("[Check] Current cost[" + v.id + "] = " + format(cost.get(v))
                                + ". New path through " + u.id + " gives cost[" + u.id + "] + weight("
                                + u.id + ", " + v.id + ") = "
                                + format(cost.get(u)) + " + " + currentEdge.weight + " = "
                                + format(newCost) + ". This is shorter, so update.");
                        state = State.UPDATE_COST;
                    } else {
                        trace.add("[Check] No update for " + v.id
                                + ". Current cost[" + v.id + "] = " + format(cost.get(v))
                                + ", new path cost = " + format(newCost) + ".");
                        state = State.FOR_CHECK;
                    }

                    return new StepResult("if_check", 12, v, currentEdge);

                case UPDATE_COST:
                    Vertex vCost = currentEdge.v;
                    double updatedCost = cost.get(u) + currentEdge.weight;
                    cost.put(vCost, updatedCost);
                    trace.add("[Update cost] cost[" + vCost.id + "] = " + format(updatedCost)
                            + ". This is now the shortest known distance from A to " + vCost.id + ".");
                    state = State.UPDATE_PARENT;
                    return new StepResult("update_cost", 13, vCost, currentEdge);

                case UPDATE_PARENT:
                    Vertex vParent = currentEdge.v;
                    parent.put(vParent, u);
                    trace.add("[Update parent] parent[" + vParent.id + "] = " + u.id
                            + ". The current best path to " + vParent.id + " comes through " + u.id + ".");
                    state = State.FOR_CHECK;
                    return new StepResult("update_parent", 14, vParent, currentEdge);

                case DONE:
                default:
                    done = true;
                    return new StepResult("done", 18, null, null);
            }
        }

        private Vertex findMinOutsideT() {
            Vertex best = null;
            double bestCost = Double.POSITIVE_INFINITY;

            for (Vertex v : V) {
                if (!T.contains(v) && cost.get(v) < bestCost) {
                    best = v;
                    bestCost = cost.get(v);
                }
            }

            return best;
        }

        private Iterator<Edge> outgoingEdgesFromU() {
            List<Edge> result = new ArrayList<>();

            for (Edge e : E) {
                if (e.u == u && !T.contains(e.v)) {
                    result.add(e);
                }
            }

            result.sort(Comparator.comparing(e -> e.v.id));
            return result.iterator();
        }

        private String format(Double value) {
            if (value == null || value.isInfinite()) {
                return "INF";
            }

            if (Math.abs(value - Math.round(value)) < 0.0001) {
                return String.valueOf((int) Math.round(value));
            }

            return String.format("%.1f", value);
        }
    }

    private static class StepResult {
        String type;
        int codeLine;
        Vertex node;
        Edge edge;

        StepResult(String type, int codeLine, Vertex node, Edge edge) {
            this.type = type;
            this.codeLine = codeLine;
            this.node = node;
            this.edge = edge;
        }
    }

    private static class Vertex {
        String id;
        int x;
        int y;

        Vertex(String id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    private static class Edge {
        Vertex u;
        Vertex v;
        int weight;

        Edge(Vertex u, Vertex v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DijkstraVisualization app = new DijkstraVisualization();
            app.setVisible(true);
        });
    }
}
