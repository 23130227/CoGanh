import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class View extends JPanel {
    private static final int CELL_SIZE = 100;
    private static final int MARGIN = 50;
    private static final int PIECE_SIZE = 28;

    private final GameState state;
    private final GameController controller;

    public View(GameState state, GameController controller) {
        this.state = state;
        this.controller = controller;

        setPreferredSize(new Dimension(
                2 * MARGIN + (GameState.SIZE - 1) * CELL_SIZE,
                2 * MARGIN + (GameState.SIZE - 1) * CELL_SIZE
        ));
        setBackground(Color.WHITE);
/**
 * Xử lý sự kiện nhấn chuột
 */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = Math.round((e.getX() - MARGIN) / (float) CELL_SIZE);
                int row = Math.round((e.getY() - MARGIN) / (float) CELL_SIZE);
                controller.handlePress(row, col);
                repaint();
            }
        });
    }

    /**
     * thực hiện vẽ bàn cờ và các quân cờ, có các nút điều khiển và hiển thị các thông tin cần thiết
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        for (int i = 0; i < GameState.SIZE; i++) {
            int y = MARGIN + i * CELL_SIZE;
            g2.drawLine(MARGIN, y, MARGIN + (GameState.SIZE - 1) * CELL_SIZE, y);
            int x = MARGIN + i * CELL_SIZE;
            g2.drawLine(x, MARGIN, x, MARGIN + (GameState.SIZE - 1) * CELL_SIZE);
        }

        int[][] diagonals = {
                {0, 0, 4, 4}, {0, 4, 4, 0},
                {0, 2, 2, 4}, {0, 2, 2, 0},
                {2, 0, 4, 2}, {2, 4, 4, 2},
                {2, 0, 0, 2}, {2, 4, 0, 2}
        };
        for (int[] d : diagonals) {
            g2.drawLine(MARGIN + d[0] * CELL_SIZE, MARGIN + d[1] * CELL_SIZE,
                    MARGIN + d[2] * CELL_SIZE, MARGIN + d[3] * CELL_SIZE);
        }

        g2.setColor(Color.BLACK);
        for (int i = 0; i < GameState.SIZE; i++) {
            for (int j = 0; j < GameState.SIZE; j++) {
                int x = MARGIN + j * CELL_SIZE;
                int y = MARGIN + i * CELL_SIZE;
                g2.fillOval(x - 4, y - 4, 8, 8);
            }
        }

        Color[][] board = state.getBoard();
        for (int row = 0; row < GameState.SIZE; row++) {
            for (int col = 0; col < GameState.SIZE; col++) {
                Color color = board[row][col];
                if (color != null) {
                    g2.setColor(color);
                    drawPiece(g2, row, col);
                }
            }
        }

        if (controller.getSelectedRow() != -1) {
            int x = MARGIN + controller.getSelectedCol() * CELL_SIZE;
            int y = MARGIN + controller.getSelectedRow() * CELL_SIZE;
            g2.setColor(Color.MAGENTA);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - PIECE_SIZE / 2 - 4, y - PIECE_SIZE / 2 - 4, PIECE_SIZE + 8, PIECE_SIZE + 8);
        }

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Turn: " + (state.getCurrentPlayer() == Color.BLUE ? "Blue" : "Red"), 10, 15);
        g2.drawString("Mode: " + (controller.getPlayMode() == GameController.PlayMode.HUMAN ? "Human vs Human" : "Human vs AI"), 10, 32);
    }

    /**
     * thực hiện vẽ một quân cờ tại vị trí cụ thể trên bàn cờ
     * @param g2
     * @param row
     * @param col
     */
    private void drawPiece(Graphics2D g2, int row, int col) {
        int x = MARGIN + col * CELL_SIZE;
        int y = MARGIN + row * CELL_SIZE;
        g2.fillOval(x - PIECE_SIZE / 2, y - PIECE_SIZE / 2, PIECE_SIZE, PIECE_SIZE);
    }


}
