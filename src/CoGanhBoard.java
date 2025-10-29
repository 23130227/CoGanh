// java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CoGanhBoard extends JPanel {
    private static final int SIZE = 5;          // 5x5 điểm
    private static final int CELL_SIZE = 100;   // khoảng cách giữa các điểm
    private static final int MARGIN = 50;       // lề
    private static final int PIECE_SIZE = 28;   // kích thước quân

    private final Color[][] board = new Color[SIZE][SIZE];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Color currentPlayer = Color.BLUE;   // Xanh đi trước

    public CoGanhBoard() {
        setPreferredSize(new Dimension(
                2 * MARGIN + (SIZE - 1) * CELL_SIZE,
                2 * MARGIN + (SIZE - 1) * CELL_SIZE
        ));
        setBackground(Color.WHITE);
        initBoard();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = Math.round((e.getX() - MARGIN) / (float) CELL_SIZE);
                int row = Math.round((e.getY() - MARGIN) / (float) CELL_SIZE);
                if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return;

                if (selectedRow == -1) {
                    // Chỉ chọn quân của người chơi hiện tại
                    if (board[row][col] != null && board[row][col] == currentPlayer) {
                        selectedRow = row;
                        selectedCol = col;
                        repaint();
                    }
                } else {
                    // Bấm lại vào cùng ô: bỏ chọn
                    if (selectedRow == row && selectedCol == col) {
                        selectedRow = -1;
                        selectedCol = -1;
                        repaint();
                        return;
                    }
                    // Nếu bấm vào quân cùng lượt: đổi lựa chọn
                    if (board[row][col] != null) {
                        if (board[row][col] == currentPlayer) {
                            selectedRow = row;
                            selectedCol = col;
                            repaint();
                        }
                        return;
                    }
                    // Thử di chuyển nếu ô trống và hợp lệ theo đường kẻ
                    if (board[row][col] == null && isAdjacentAndConnected(selectedRow, selectedCol, row, col)) {
                        board[row][col] = board[selectedRow][selectedCol];
                        board[selectedRow][selectedCol] = null;

                        // Hoàn tất nước đi: bỏ chọn và đổi lượt
                        selectedRow = -1;
                        selectedCol = -1;
                        switchTurn();
                        repaint();
                    } else {
                        repaint();
                    }
                }
            }
        });
    }

    private void initBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = null;
            }
        }
        for (int c = 0; c < SIZE; c++) board[0][c] = Color.RED;
        for (int c = 0; c < SIZE; c++) board[4][c] = Color.BLUE;
        board[1][0] = Color.RED;
        board[1][4] = Color.RED;
        board[3][0] = Color.BLUE;
        board[3][4] = Color.BLUE;
        board[2][0] = Color.BLUE; // swapped middle-edge pieces
        board[2][4] = Color.RED;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == Color.BLUE) ? Color.RED : Color.BLUE;
    }

    // Chỉ cho phép đi sang ô kề có đường kẻ nối
    private boolean isAdjacentAndConnected(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        // Kề ngang/dọc: luôn có đường kẻ
        if (dr + dc == 1) return true;

        // Kề chéo: chỉ khi nằm trên các đường chéo được vẽ
        if (dr == 1 && dc == 1) {
            int sum1 = r1 + c1, sum2 = r2 + c2;
            int diff1 = r1 - c1, diff2 = r2 - c2;

            // Đường chéo kiểu r+c = 2, 4, 6
            if (sum1 == sum2 && (sum1 == 2 || sum1 == 4 || sum1 == 6)) return true;

            // Đường chéo kiểu r-c = -2, 0, 2
            if (diff1 == diff2 && (diff1 == -2 || diff1 == 0 || diff1 == 2)) return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Lưới
        g2.setColor(Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            int y = MARGIN + i * CELL_SIZE;
            g2.drawLine(MARGIN, y, MARGIN + (SIZE - 1) * CELL_SIZE, y);
            int x = MARGIN + i * CELL_SIZE;
            g2.drawLine(x, MARGIN, x, MARGIN + (SIZE - 1) * CELL_SIZE);
        }

        // Đường chéo mẫu bàn cờ Gánh
        int[][] diagonals = {
                {0, 0, 4, 4},
                {0, 4, 4, 0},
                {0, 2, 2, 4},
                {0, 2, 2, 0},
                {2, 0, 4, 2},
                {2, 4, 4, 2},
                {2, 0, 0, 2},
                {2, 4, 0, 2}
        };
        for (int[] d : diagonals) {
            g2.drawLine(
                    MARGIN + d[0] * CELL_SIZE,
                    MARGIN + d[1] * CELL_SIZE,
                    MARGIN + d[2] * CELL_SIZE,
                    MARGIN + d[3] * CELL_SIZE
            );
        }

        // Điểm giao
        g2.setColor(Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int x = MARGIN + j * CELL_SIZE;
                int y = MARGIN + i * CELL_SIZE;
                g2.fillOval(x - 4, y - 4, 8, 8);
            }
        }

        // Quân cờ
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Color color = board[row][col];
                if (color != null) {
                    g2.setColor(color);
                    drawPiece(g2, row, col);
                }
            }
        }

        // Highlight quân đang chọn
        if (selectedRow != -1) {
            int x = MARGIN + selectedCol * CELL_SIZE;
            int y = MARGIN + selectedRow * CELL_SIZE;
            g2.setColor(Color.MAGENTA);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - PIECE_SIZE / 2 - 4, y - PIECE_SIZE / 2 - 4, PIECE_SIZE + 8, PIECE_SIZE + 8);
        }

        // Gợi ý lượt hiện tại
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Turn: " + (currentPlayer == Color.BLUE ? "Blue" : "Red"),
                10, 15);
    }

    private void drawPiece(Graphics2D g2, int row, int col) {
        int x = MARGIN + col * CELL_SIZE;
        int y = MARGIN + row * CELL_SIZE;
        g2.fillOval(x - PIECE_SIZE / 2, y - PIECE_SIZE / 2, PIECE_SIZE, PIECE_SIZE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Cờ Gánh – Move along lines only");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new CoGanhBoard());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}