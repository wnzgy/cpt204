import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class MergeSortAnimatorGUI extends JFrame {

    private int[] list1, list2, temp;
    private int current1, current2, current3; // 分别指向 list1, list2, temp 的当前元素


    private BoxPanel[] boxList1, boxList2, boxTemp;

 
    private DisplayPanel displayPanel;


    private JTextField inputField1, inputField2;
    private JTextArea logArea;
    private JTextArea codeArea;
    private JLabel statusLabel;


    private JScrollPane codeScrollPane;

    private JSlider codeHeightSlider;

    private JSplitPane bottomSplitPane;

    private enum MergeState {
        INIT,
        COMPARE,
        WAIT_ASSIGN,
        WAIT_FOR_ASSIGN_LEFT,
        WAIT_FOR_ASSIGN_RIGHT,
        EXHAUST_LEFT,
        WAIT_FOR_ASSIGN_EXHAUST_LEFT,
        EXHAUST_RIGHT,
        WAIT_FOR_ASSIGN_EXHAUST_RIGHT,
        FINISH
    }
    private MergeState state = MergeState.INIT;


    private final int ANIMATION_DELAY = 300;


    private final String[] codeLines = {
            "public static void merge(int[] list1, int[] list2, int[] temp) {",
            "    int current1 = 0; // Current index in list1",
            "    int current2 = 0; // Current index in list2",
            "    int current3 = 0; // Current index in temp",
            "    while (current1 < list1.length && current2 < list2.length) {",
            "        if (list1[current1] < list2[current2])",
            "            temp[current3++] = list1[current1++];",
            "        else",
            "            temp[current3++] = list2[current2++];",
            "    }",
            "    while (current1 < list1.length)",
            "        temp[current3++] = list1[current1++];",
            "    while (current2 < list2.length)",
            "        temp[current3++] = list2[current2++];",
            "}"
    };

    public MergeSortAnimatorGUI() {
        setTitle("Merge Sort Animator");
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel topPanel = new JPanel();
        inputField1 = new JTextField("32,46,48,49,81", 20);
        inputField2 = new JTextField("38,46,64,68,79,88,92,97", 20);
        JButton startButton = new JButton("Start");
        JButton nextButton = new JButton("Next Step");
        statusLabel = new JLabel("Status: ");
        JLabel codeHeightLabel = new JLabel("Code Window Height:");
        codeHeightSlider = new JSlider(JSlider.HORIZONTAL, 200, 600, 250);
        codeHeightSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int height = codeHeightSlider.getValue();
                codeScrollPane.setPreferredSize(new Dimension(codeScrollPane.getPreferredSize().width, height));
                codeScrollPane.revalidate();
            }
        });
        topPanel.add(new JLabel("List1:"));
        topPanel.add(inputField1);
        topPanel.add(new JLabel("List2:"));
        topPanel.add(inputField2);
        topPanel.add(startButton);
        topPanel.add(nextButton);
        topPanel.add(statusLabel);
        topPanel.add(codeHeightLabel);
        topPanel.add(codeHeightSlider);
        add(topPanel, BorderLayout.NORTH);

        displayPanel = new DisplayPanel();
        displayPanel.setPreferredSize(new Dimension(1050, 350));
        displayPanel.setBackground(Color.WHITE);
        add(displayPanel, BorderLayout.CENTER);


        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(600, 250));

        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        StringBuilder sb = new StringBuilder();
        for (String line : codeLines) {
            sb.append(line).append("\n");
        }
        codeArea.setText(sb.toString());
        codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setPreferredSize(new Dimension(450, codeHeightSlider.getValue()));

        bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, logScrollPane, codeScrollPane);
        bottomSplitPane.setDividerLocation(650);
        add(bottomSplitPane, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {

            String[] tokens1 = inputField1.getText().split(",");
            list1 = new int[tokens1.length];
            for (int i = 0; i < tokens1.length; i++) {
                list1[i] = Integer.parseInt(tokens1[i].trim());
            }

            String[] tokens2 = inputField2.getText().split(",");
            list2 = new int[tokens2.length];
            for (int i = 0; i < tokens2.length; i++) {
                list2[i] = Integer.parseInt(tokens2[i].trim());
            }

            temp = new int[list1.length + list2.length];
            current1 = 0;
            current2 = 0;
            current3 = 0;

            boxList1 = new BoxPanel[list1.length];
            boxList2 = new BoxPanel[list2.length];
            boxTemp = new BoxPanel[temp.length];
            displayPanel.removeAll();
            for (int i = 0; i < list1.length; i++) {
                boxList1[i] = new BoxPanel(list1[i], 50 + i * 60, 50, Color.CYAN);
                displayPanel.add(boxList1[i]);
            }
            for (int i = 0; i < list2.length; i++) {
                boxList2[i] = new BoxPanel(list2[i], 50 + i * 60, 150, Color.CYAN);
                displayPanel.add(boxList2[i]);
            }
            for (int i = 0; i < temp.length; i++) {
                boxTemp[i] = new BoxPanel(0, 50 + i * 60, 250, Color.LIGHT_GRAY);
                displayPanel.add(boxTemp[i]);
            }
            displayPanel.repaint();

            current1 = 0;
            current2 = 0;
            current3 = 0;
            state = MergeState.COMPARE;
            logArea.setText("List1: " + Arrays.toString(list1) + "\n" +
                    "List2: " + Arrays.toString(list2) + "\n\n");
            statusLabel.setText("Merge Started");
            highlightCode(-1);
        });

        nextButton.addActionListener(e -> {
            runStep();
            displayPanel.repaint();
        });
    }


    private void runStep() {
        switch(state) {
            case COMPARE:
                if (current1 < list1.length && current2 < list2.length) {
                    logArea.append("Comparing list1[" + current1 + "]=" + list1[current1] +
                            " and list2[" + current2 + "]=" + list2[current2] + "\n");
                    highlightCode(5);
                    boxList1[current1].setBackground(Color.ORANGE);
                    boxList2[current2].setBackground(Color.ORANGE);
                    state = MergeState.WAIT_ASSIGN;
                } else {
                    if (current2 >= list2.length) {
                        logArea.append("List2 exhausted. Prepare to copy remaining from list1.\n");
                        highlightCode(10);
                        state = MergeState.WAIT_FOR_ASSIGN_EXHAUST_LEFT;
                    } else if (current1 >= list1.length) {
                        logArea.append("List1 exhausted. Prepare to copy remaining from list2.\n");
                        highlightCode(12);
                        state = MergeState.WAIT_FOR_ASSIGN_EXHAUST_RIGHT;
                    } else {
                        state = MergeState.FINISH;
                    }
                    runStep();
                }
                break;
            case WAIT_ASSIGN:

                if (list1[current1] < list2[current2]) {
                    highlightCode(6);
                    state = MergeState.WAIT_FOR_ASSIGN_LEFT;
                } else {
                    highlightCode(8);
                    state = MergeState.WAIT_FOR_ASSIGN_RIGHT;
                }
                break;
            case WAIT_FOR_ASSIGN_LEFT:
                animateCopy(boxList1[current1], boxTemp[current3], () -> {
                    temp[current3] = list1[current1];
                    logArea.append("Copied list1[" + current1 + "]=" + list1[current1] +
                            " to temp[" + current3 + "]\n");
                    boxTemp[current3].setValue(temp[current3]);
                    current1++;
                    current3++;
                    state = MergeState.COMPARE;
                });
                break;
            case WAIT_FOR_ASSIGN_RIGHT:
                animateCopy(boxList2[current2], boxTemp[current3], () -> {
                    temp[current3] = list2[current2];
                    logArea.append("Copied list2[" + current2 + "]=" + list2[current2] +
                            " to temp[" + current3 + "]\n");
                    boxTemp[current3].setValue(temp[current3]);
                    current2++;
                    current3++;
                    state = MergeState.COMPARE;
                });
                break;
            case WAIT_FOR_ASSIGN_EXHAUST_LEFT:
                animateCopy(boxList1[current1], boxTemp[current3], () -> {
                    temp[current3] = list1[current1];
                    logArea.append("Copied list1[" + current1 + "]=" + list1[current1] +
                            " to temp[" + current3 + "]\n");
                    boxTemp[current3].setValue(temp[current3]);
                    current1++;
                    current3++;
                    if (current1 < list1.length) {
                        state = MergeState.EXHAUST_LEFT;
                    } else {
                        state = MergeState.FINISH;
                    }
                });
                break;
            case WAIT_FOR_ASSIGN_EXHAUST_RIGHT:
                animateCopy(boxList2[current2], boxTemp[current3], () -> {
                    temp[current3] = list2[current2];
                    logArea.append("Copied list2[" + current2 + "]=" + list2[current2] +
                            " to temp[" + current3 + "]\n");
                    boxTemp[current3].setValue(temp[current3]);
                    current2++;
                    current3++;
                    if (current2 < list2.length) {
                        state = MergeState.EXHAUST_RIGHT;
                    } else {
                        state = MergeState.FINISH;
                    }
                });
                break;
            case EXHAUST_LEFT:
                logArea.append("Copy remaining list1[" + current1 + "]=" + list1[current1] +
                        " to temp[" + current3 + "]\n");
                highlightCode(10);
                state = MergeState.WAIT_FOR_ASSIGN_EXHAUST_LEFT;
                break;
            case EXHAUST_RIGHT:
                logArea.append("Copy remaining list2[" + current2 + "]=" + list2[current2] +
                        " to temp[" + current3 + "]\n");
                highlightCode(12);
                state = MergeState.WAIT_FOR_ASSIGN_EXHAUST_RIGHT;
                break;
            case FINISH:
                logArea.append("Merge complete!\n");
                highlightCode(-1);
                statusLabel.setText("Merge Done");
                state = MergeState.FINISH;
                break;
            default:
                break;
        }
    }


    private void animateCopy(BoxPanel sourceBox, BoxPanel targetBox, Runnable callback) {
        sourceBox.setBackground(Color.YELLOW);
        targetBox.setBackground(Color.YELLOW);
        Timer t = new Timer(ANIMATION_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sourceBox.setBackground(Color.CYAN);
                targetBox.setBackground(Color.LIGHT_GRAY);
                ((Timer)e.getSource()).stop();
                callback.run();
                displayPanel.repaint();
            }
        });
        t.setRepeats(false);
        t.start();
    }


    private void highlightCode(int line) {
        codeArea.getHighlighter().removeAllHighlights();
        if (line < 0 || line >= codeLines.length) return;
        if (codeLines[line].trim().equals("}")) return;
        String content = codeArea.getText();
        int start = content.indexOf(codeLines[line]);
        if (start < 0) return;
        int end = start + codeLines[line].length();
        try {
            codeArea.getHighlighter().addHighlight(start, end,
                    new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 153)));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }


    class DisplayPanel extends JPanel {
        public DisplayPanel() {
            setLayout(null);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.RED);

            if (boxList1 != null && current1 < boxList1.length) {
                BoxPanel bp = boxList1[current1];
                int arrowX = bp.getX() + bp.getWidth() / 2;
                int arrowY = bp.getY() - 10;
                drawDownArrow(g, arrowX, arrowY, 10, 10);
                g.drawString("current1", arrowX - 40, arrowY - 2);
            }

            if (boxList2 != null && current2 < boxList2.length) {
                BoxPanel bp = boxList2[current2];
                int arrowX = bp.getX() + bp.getWidth() / 2;
                int arrowY = bp.getY() - 10;
                drawDownArrow(g, arrowX, arrowY, 10, 10);
                g.drawString("current2", arrowX - 40, arrowY - 2);
            }

            if (boxTemp != null && current3 < boxTemp.length) {
                BoxPanel bp = boxTemp[current3];
                int arrowX = bp.getX() + bp.getWidth() / 2;
                int arrowY = bp.getY() + bp.getHeight() + 10;
                drawUpArrow(g, arrowX, arrowY, 10, 10);
                g.drawString("current3", arrowX - 40, arrowY + 12);
            }
        }

        private void drawUpArrow(Graphics g, int x, int y, int width, int height) {
            int half = width / 2;
            int[] xs = { x, x - half, x + half };
            int[] ys = { y, y + height, y + height };
            g.fillPolygon(xs, ys, 3);
        }

        private void drawDownArrow(Graphics g, int x, int y, int width, int height) {
            int half = width / 2;
            int[] xs = { x, x - half, x + half };
            int[] ys = { y, y - height, y - height };
            g.fillPolygon(xs, ys, 3);
        }
    }


    class BoxPanel extends JPanel {
        private int value;
        public BoxPanel(int value, int x, int y, Color bg) {
            this.value = value;
            setBounds(x, y, 50, 50);
            setBackground(bg);
        }
        public void setValue(int v) {
            value = v;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            String s = String.valueOf(value);
            int x = (getWidth() - fm.stringWidth(s)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g.drawString(s, x, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MergeSortAnimatorGUI frame = new MergeSortAnimatorGUI();
            frame.setVisible(true);
        });
    }
}
