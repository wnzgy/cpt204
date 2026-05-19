import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class QuickSortAnimatorGUI extends JFrame {
    private int[] array;

    private int first;
    private int pivot;
    private int low;
    private int high;

    private BoxPanel[] boxList;

    private enum PartitionState {
        INIT,
        MOVE_LOW,
        MOVE_HIGH,
        CHECK_SWAP,
        WAIT_FOR_SWAP,
        SWAP_ELEMENTS,
        POST_PROCESS,
        SWAP_PIVOT,
        FINISH
    }
    private PartitionState pState = PartitionState.INIT;


    private boolean pivotSwapComparedHighlighted = false;


    private JTextField inputField;         
    private JTextArea logArea, codeArea;  
    private JLabel statusLabel;              
    private DisplayPanel displayPanel;       


    private JScrollPane codeScrollPane;
    private JSlider codeHeightSlider;
    private JSplitPane bottomSplitPane;


    private final int ANIMATION_DELAY = 300;


    private final String[] codeLines = {
            "int pivot = list[first];",
            "int low = first + 1; // position, not value",
            "int high = last; // position, not value",
            "while (high > low) {",                       // index 3
            "    while (low <= high && list[low] <= pivot) {",  // index 4
            "        low++;",                             // index 5
            "    }",                                      // index 6
            "    while (low <= high && list[high] > pivot) {",   // index 7
            "        high--;",                            // index 8
            "    }",                                      // index 9
            "    if (high > low) {",                       // index 10
            "        int temp = list[high];",              // index 11
            "        list[high] = list[low];",             // index 12
            "        list[low] = temp;",                   // index 13
            "    }",                                      // index 14
            "}",                                          // index 15
            "while (high > first && list[high] >= pivot) {",    // index 16
            "    high--;",                                // index 17
            "}",                                          // index 18
            "if (pivot > list[high]) {",                   // index 19
            "    list[first] = list[high];",              // index 20
            "    list[high] = pivot;",                    // index 21
            "    return high;",                           // index 22
            "} else {",                                   // index 23
            "    return first;",                          // index 24
            "}"                                           // index 25
    };

    public QuickSortAnimatorGUI() {
        setTitle("Quick Sort Partition Visualizer");
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        inputField = new JTextField("55,23,78,12,90,34,67,45,89", 30);
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
        topPanel.add(new JLabel("Array:"));
        topPanel.add(inputField);
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
            String[] tokens = inputField.getText().split(",");
            array = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                array[i] = Integer.parseInt(tokens[i].trim());
            }

            first = 0;
            pivot = array[first];
            low = first + 1;
            high = array.length - 1;
            pivotSwapComparedHighlighted = false;
            boxList = new BoxPanel[array.length];
            displayPanel.removeAll();
            for (int i = 0; i < array.length; i++) {
                Color col = Color.CYAN;
                if (i == first) {
                    col = Color.BLUE;
                }
                boxList[i] = new BoxPanel(array[i], 50 + i * 60, 150, col);
                displayPanel.add(boxList[i]);
            }
            displayPanel.repaint();

            pState = PartitionState.INIT;
            logArea.setText("Initial Array: " + Arrays.toString(array) + "\n");
            statusLabel.setText("Partition Started");
            highlightCode(-1); // 清除所有代码高亮
            runStep();  // 自动执行 INIT 状态
        });

        nextButton.addActionListener(e -> {
            runStep();
            displayPanel.repaint();
        });
    }

    private void runStep() {
        switch (pState) {
            case INIT:
                logArea.append("Pivot = " + pivot + " at index " + first + "\n");
                logArea.append("low pointer = " + low + ", high pointer = " + high + "\n");
                highlightCode(4);
                pState = PartitionState.MOVE_LOW;
                break;
            case MOVE_LOW:
                if (low <= high && array[low] <= pivot) {
                    highlightCode(5);
                    logArea.append("Moving low pointer right: array[" + low + "] = " + array[low] + "\n");
                    low++;
                } else {
                    highlightCode(7);
                    pState = PartitionState.MOVE_HIGH;
                }
                break;
            case MOVE_HIGH:
                if (low <= high && array[high] > pivot) {

                    highlightCode(8);
                    logArea.append("Moving high pointer left: array[" + high + "] = " + array[high] + "\n");
                    high--;
                } else {
                    pState = PartitionState.CHECK_SWAP;
                }
                break;
            case CHECK_SWAP:
                if (high > low) {
                    logArea.append("Pointers stopped. Ready to swap.\n");
                    boxList[low].setBackground(Color.ORANGE);
                    boxList[high].setBackground(Color.ORANGE);
                    highlightCode(10);
                    pState = PartitionState.WAIT_FOR_SWAP;
                } else {
                    logArea.append("Pointers have crossed, entering post-process.\n");
                    pState = PartitionState.POST_PROCESS;
                }
                break;
            case WAIT_FOR_SWAP:
                pState = PartitionState.SWAP_ELEMENTS;
                break;
            case SWAP_ELEMENTS:
                animateSwap(boxList[low], boxList[high], () -> {
                    int tempVal = array[low];
                    array[low] = array[high];
                    array[high] = tempVal;
                    boxList[low].setValue(array[low]);
                    boxList[high].setValue(array[high]);
                    logArea.append("Swapped indices " + low + " and " + high + "\n");
                    if (low != first) boxList[low].setBackground(Color.CYAN);
                    if (high != first) boxList[high].setBackground(Color.CYAN);
                    highlightCode(4);
                    pState = PartitionState.MOVE_LOW;
                });
                break;
            case POST_PROCESS:
                if (high > first && array[high] >= pivot) {
                    highlightCode(17);
                    logArea.append("Post-process: Moving high pointer left: array[" + high + "] = " + array[high] + "\n");
                    high--;
                } else {
                    pState = PartitionState.SWAP_PIVOT;
                }
                break;
            case SWAP_PIVOT:
                if (pivot > array[high]) {
                    if (!pivotSwapComparedHighlighted) {
                        logArea.append("Pivot swap candidate: pivot (" + pivot + ") with array[" + high + "] = " + array[high] + "\n");
                        boxList[first].setBackground(Color.ORANGE);
                        boxList[high].setBackground(Color.ORANGE);
                        highlightCode(19);
                        pivotSwapComparedHighlighted = true;
                    } else {

                        animateSwap(boxList[first], boxList[high], () -> {
                            int tempVal = array[first];
                            array[first] = array[high];
                            array[high] = tempVal;
                            boxList[first].setValue(array[first]);
                            boxList[high].setValue(array[high]);
                            boxList[high].setBackground(Color.GREEN);
                            logArea.append("Pivot placed at index " + high + "\n");
                            pState = PartitionState.FINISH;
                            pivotSwapComparedHighlighted = false;
                        });
                    }
                } else {
                    logArea.append("No pivot swap needed.\n");
                    pState = PartitionState.FINISH;
                }
                break;
            case FINISH:
                logArea.append("Partition complete!\n");
                statusLabel.setText("Partition Done");
                break;
            default:
                break;
        }
    }

    private void animateSwap(BoxPanel boxA, BoxPanel boxB, Runnable callback) {
        boxA.setBackground(Color.YELLOW);
        boxB.setBackground(Color.YELLOW);
        Timer t = new Timer(ANIMATION_DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boxA.setBackground(Color.CYAN);
                boxB.setBackground(Color.CYAN);
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
        String target = codeLines[line].trim();
        if (target.startsWith("//")) return;
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
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.RED);

            if (boxList != null && array != null && array.length > 0) {
                BoxPanel bp = boxList[0];
                int x = bp.getX() + bp.getWidth() / 2;
                int y = bp.getY() - 20;
                g.drawString("↓", x - 5, y);
                g.drawString("pivot", x - 30, y - 5);
            }

            if (boxList != null && low < boxList.length && low >= 1) {
                BoxPanel bp = boxList[low];
                int x = bp.getX() + bp.getWidth() / 2;
                int y = bp.getY() + bp.getHeight() + 20;
                g.drawString("↓", x - 5, y);
                g.drawString("low", x - 20, y + 20);
            }

            if (boxList != null && high < boxList.length) {
                BoxPanel bp = boxList[high];
                int x = bp.getX() + bp.getWidth() / 2;
                int y = bp.getY() - 20;
                g.drawString("↓", x - 5, y);
                g.drawString("high", x - 25, y - 5);
            }
        }
    }


    class BoxPanel extends JPanel {
        private int value;
        public BoxPanel(int value, int x, int y, Color bg) {
            this.value = value;
            setBounds(x, y, 50, 50);
            setBackground(bg);
        }
        public int getValue() {
            return value;
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
            QuickSortAnimatorGUI frame = new QuickSortAnimatorGUI();
            frame.setVisible(true);
        });
    }
}
