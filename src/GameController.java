// File: GameController.java

import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane; // Thư viện để hiện bảng thông báo

public class GameController {
    public enum PlayMode {HUMAN, AI}

    private final GameState state;
    private View view;
    private PlayMode playMode = PlayMode.HUMAN;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private final PlayerAI aiPlayer = new PlayerAI();

    public GameController(GameState state) {
        this.state = state;
    }

    public void setView(View view) {
        this.view = view;
    }

    public GameState getState() {
        return state;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode mode) {
        this.playMode = mode;
        if (mode == PlayMode.AI && state.getCurrentPlayer() == Color.RED) {
            makeAIMove();
        }
    }

    public int getSelectedRow() { return selectedRow; }
    public int getSelectedCol() { return selectedCol; }

    public void handlePress(int row, int col) {
        // Chặn click chuột khi AI đang nghĩ hoặc game đã kết thúc
        if (playMode == PlayMode.AI && state.getCurrentPlayer() == Color.RED) return;
        if (state.isGameOver()) return;

        if (row < 0 || row >= GameState.SIZE || col < 0 || col >= GameState.SIZE) return;

        Color[][] board = state.getBoard();
        Color currentPlayer = state.getCurrentPlayer();

        // Xử lý chọn quân
        if (selectedRow == -1) {
            if (board[row][col] != null && board[row][col] == currentPlayer) {
                selectedRow = row;
                selectedCol = col;
            }
            return;
        }

        if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
            return;
        }

        if (board[row][col] != null) {
            if (board[row][col] == currentPlayer) {
                selectedRow = row;
                selectedCol = col;
            }
            return;
        }

        // --- NGƯỜI CHƠI DI CHUYỂN ---
        if (board[row][col] == null && Rules.isAdjacentAndConnected(selectedRow, selectedCol, row, col)) {
            Move move = new Move(selectedRow, selectedCol, row, col);
            state.applyMove(move);

            selectedRow = -1;
            selectedCol = -1;

            if (view != null) view.repaint();

            // 1. KIỂM TRA THẮNG NGAY SAU KHI NGƯỜI ĐI
            // Nếu thắng rồi thì dừng luôn, không cho AI đi nữa
            if (checkGameOver()) return;

            // Nếu chưa thắng -> Đến lượt AI
            if (playMode == PlayMode.AI && state.getCurrentPlayer() == Color.RED) {
                makeAIMove();
            }
        }
    }

    private void makeAIMove() {
        new Thread(() -> {
            try {
                Thread.sleep(500); // Nghỉ chút cho tự nhiên

                Move bestMove = aiPlayer.findBestMove(state);

                if (bestMove != null) {
                    state.applyMove(bestMove);

                    SwingUtilities.invokeLater(() -> {
                        if (view != null) view.repaint();

                        // 2. KIỂM TRA THẮNG NGAY SAU KHI MÁY ĐI
                        checkGameOver();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * HÀM MỚI: Kiểm tra game over và hiện JOptionPane
     * Trả về true nếu game đã kết thúc
     */
    private boolean checkGameOver() {
        if (state.isGameOver()) {
            Color winner = state.getWinner();
            String message;

            if (winner == Color.RED) {
                message = "MÁY (QUÂN ĐỎ) ĐÃ THẮNG!\nBạn có muốn chơi lại không?";
            } else if (winner == Color.BLUE) {
                message = "CHÚC MỪNG! BẠN (QUÂN XANH) ĐÃ THẮNG!\nBạn có muốn chơi lại không?";
            } else {
                message = "HÒA CỜ!\nChơi lại nhé?";
            }

            // Hiển thị hộp thoại
            int option = JOptionPane.showConfirmDialog(
                    view,
                    message,
                    "Kết thúc trò chơi",
                    JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                state.reset(); // Reset bàn cờ
                if (view != null) view.repaint(); // Vẽ lại bàn cờ mới
            } else {
                System.exit(0); // Thoát game
            }
            return true;
        }
        return false;
    }
}