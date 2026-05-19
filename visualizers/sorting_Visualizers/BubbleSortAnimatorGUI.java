import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BubbleSortAnimatorGUI extends JFrame {
    private List<BoxPanel> boxes = new ArrayList<>();
    private JPanel arrayPanel;
    private JButton nextButton;
    private JTextArea logArea;
    private JLabel statusLabel;
    private int[] array;
    private int k = 1, i = 0;


    private enum StepState { INIT_PASS, SHOW_NOW, CHECK_SWAP, ANIMATE_SWAP }
    private StepState state = StepState.INIT_PASS;

    private Timer animationTimer;
    private int swapX = 0;
    private JTextArea codeArea;

    
    private boolean innerNowDisplayed = false;
   
    private boolean passSwapped = false;

    private final String[] codeLines = {
            "for (int k = 1; k < list.length; k++) {",
            "    for (int i = 0; i < list.length - k; i++) {",
            "        if (list[i] > list[i + 1]) {",
            "            // swap list[i] and list[i + 1]",
            "        }",
            "    }",
            "}"
    };

    public BubbleSortAnimatorGUI() {
        setTitle("Bubble Sort Animator");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextField inputField = new JTextField("5,3,8,2,1", 30);
        JButton startButton = new JButton("Start");
        nextButton = new JButton("Next Step");
        statusLabel = new JLabel(" ");
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Array: "));
        topPanel.add(inputField);
        topPanel.add(startButton);
        topPanel.add(nextButton);
        add(topPanel, BorderLayout.NORTH);


        arrayPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Arial", Font.PLAIN, 12));
                for (int j = 0; j < boxes.size(); j++) {
                    int x = 100 + j * 60;
                    g.drawString("list[" + j + "]", x + 5, 110);
                }
            }
        };
        arrayPanel.setPreferredSize(new Dimension(1000, 200));
        arrayPanel.setBackground(Color.WHITE);
        add(arrayPanel, BorderLayout.CENTER);


        JPanel logPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea(10, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        logPanel.add(statusLabel, BorderLayout.SOUTH);


        codeArea = new JTextArea(10, 40);
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        codeArea.setEditable(false);
        StringBuilder codeText = new StringBuilder();
        for (String line : codeLines) {
            codeText.append(line).append("\n");
        }
        codeArea.setText(codeText.toString());


        JSplitPane bottomSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                logPanel,
                new JScrollPane(codeArea)
        );
        bottomSplitPane.setDividerLocation(600);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bottomSplitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);


        startButton.addActionListener(e -> {
            arrayPanel.removeAll();
            boxes.clear();
            String[] tokens = inputField.getText().split(",");
            array = new int[tokens.length];
            for (int j = 0; j < tokens.length; j++) {
                array[j] = Integer.parseInt(tokens[j].trim());
                BoxPanel box = new BoxPanel(array[j], j * 60 + 100, 50);
                boxes.add(box);
                arrayPanel.add(box);
            }
            arrayPanel.repaint();
            k = 1;
            i = 0;
            state = StepState.INIT_PASS;
            logArea.setText("Array Initialized: " + Arrays.toString(array) + "\n\n");
            statusLabel.setText(" ");
            innerNowDisplayed = false;
            passSwapped = false;
            highlightCode(-1); 
        });


        nextButton.addActionListener(e -> runStep());
    }


    private void highlightCode(int line) {
        Highlighter highlighter = codeArea.getHighlighter();
        highlighter.removeAllHighlights();
        if (line < 0 || line >= codeLines.length) {
            return;
        }
        if (codeLines[line].trim().equals("}")) {
            return;
        }
        String content = codeArea.getText();
        int start = content.indexOf(codeLines[line]);
        if (start < 0) {
            return;
        }
        int end = start + codeLines[line].length();
        try {
            highlighter.addHighlight(start, end,
                    new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 153)));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void runStep() {
        switch (state) {
            case INIT_PASS:
                
                if (k > 1 && !passSwapped) {
                    logArea.append("Sorting complete: Outer loop terminated because no swaps occurred during pass " + k + ".\n");
                    statusLabel.setText("Sorting Complete");
                    highlightCode(-1);
                    state = StepState.INIT_PASS;
                    return;
                }
                if (k >= array.length) {
                    logArea.append("Sorting complete: Outer loop terminated because k < list.length condition is not met (k = " + k + ").\n");
                    statusLabel.setText("Sorting Complete");
                    highlightCode(-1);
                    state = StepState.INIT_PASS;
                    return;
                }
                logArea.append("Starting Pass k = " + k + " (outer loop)\n");
                statusLabel.setText("Now entering k = " + k + " (outer loop)");
                highlightCode(0);  
                i = 0;
                innerNowDisplayed = false;
                passSwapped = false;  
                state = StepState.SHOW_NOW;
                break;

            case SHOW_NOW:

                if (i >= array.length - k) {

                    int sortedIndex = array.length - k;
                    boxes.get(sortedIndex).setSorted(true);
                    logArea.append("Pass complete.\n");

                    if (!passSwapped) {
                        logArea.append("Sorting complete: Outer loop terminated because no swaps occurred during pass " + k + ".\n");
                        statusLabel.setText("Sorting Complete");
                        highlightCode(-1);
                        state = StepState.INIT_PASS;
                        return;
                    } else {
                        k++;
                        state = StepState.INIT_PASS;
                        runStep();
                        return;
                    }
                }
                if (!innerNowDisplayed) {

                    logArea.append("Now at i = " + i + " (inner for)\n");
                    statusLabel.setText("At i = " + i + " (inner for)");
                    highlightCode(1);
                    innerNowDisplayed = true;
                } else {

                    logArea.append("Comparing i = " + i + " (" + array[i] + ") and i+1 = " + (i+1) + " (" + array[i+1] + ")\n");
                    statusLabel.setText("Comparing i = " + i);
                    boxes.get(i).setHighlight(true);
                    boxes.get(i+1).setHighlight(true);
                    highlightCode(2);
                    state = StepState.CHECK_SWAP;
                }
                break;

            case CHECK_SWAP:
                if (array[i] > array[i+1]) {
                    logArea.append("Swapping needed\n");
                    highlightCode(3);
                    passSwapped = true;
                    state = StepState.ANIMATE_SWAP;
                    swapX = 0;
                    animationTimer = new Timer(20, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            swapX += 5;
                            boxes.get(i).setLocation(boxes.get(i).getX() + 5, 50);
                            boxes.get(i+1).setLocation(boxes.get(i+1).getX() - 5, 50);
                            if (swapX >= 60) {
                                animationTimer.stop();
                                BoxPanel temp = boxes.get(i);
                                boxes.set(i, boxes.get(i+1));
                                boxes.set(i+1, temp);
                                arrayPanel.repaint();
                                int t = array[i];
                                array[i] = array[i+1];
                                array[i+1] = t;
                                logArea.append("Swapped: " + Arrays.toString(array) + "\n\n");
                                boxes.get(i).setHighlight(false);
                                boxes.get(i+1).setHighlight(false);
                                innerNowDisplayed = false;
                                i++;
                                state = StepState.SHOW_NOW;
                            }
                        }
                    });
                    animationTimer.start();
                } else {
                    logArea.append("No swap needed\n\n");
                    boxes.get(i).setHighlight(false);
                    boxes.get(i+1).setHighlight(false);
                    innerNowDisplayed = false;
                    i++;
                    state = StepState.SHOW_NOW;
                }
                break;

            case ANIMATE_SWAP:
                break;
        }
    }


    class BoxPanel extends JPanel {
        int value;
        boolean highlight = false;
        boolean sorted = false;

        public BoxPanel(int value, int x, int y) {
            this.value = value;
            setBounds(x, y, 50, 50);
            setBackground(Color.CYAN);
        }

        public void setHighlight(boolean h) {
            if (!sorted) { // 如果已经排序，不改变颜色
                setBackground(h ? Color.RED : Color.CYAN);
            }
        }

        public void setSorted(boolean sorted) {
            this.sorted = sorted;
            if (sorted) {
                setBackground(Color.GREEN);
            } else {
                setBackground(Color.CYAN);
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g.drawString(String.valueOf(value), 20, 30);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BubbleSortAnimatorGUI().setVisible(true));
    }
}
