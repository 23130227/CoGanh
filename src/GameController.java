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

        // xử lý người chơi di chuyển quân
        if (board[row][col] == null && Rules.isAdjacentAndConnected(selectedRow, selectedCol, row, col)) {
            Move move = new Move(selectedRow, selectedCol, row, col);
            state.applyMove(move);

            selectedRow = -1;
            selectedCol = -1;

            if (view != null) view.repaint();

            // Kiểm tra có ai thắng chưa nếu thắng rồi kết thúc game luôn
            if (checkGameOver()) return;

            // Nếu chưa thắng Đến lượt AI đi, nếu như ở đây là chế độ chơi với AI
            if (playMode == PlayMode.AI && state.getCurrentPlayer() == Color.RED) {
                makeAIMove();
            }
        }
    }

    private void makeAIMove() {
        new Thread(() -> {
            try {
                Thread.sleep(500);

                Move bestMove = aiPlayer.findBestMove(state);

                if (bestMove != null) {
                    state.applyMove(bestMove);

                    SwingUtilities.invokeLater(() -> {
                        if (view != null) view.repaint();

                        // kiểm tra máy có thắng sau khi nó đi hay không
                        checkGameOver();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * thực hiện kiểm tra game đã kết thúc chưa
     * @return
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
                state.reset();
                if (view != null) view.repaint();
            } else {
                System.exit(0);
            }
            return true;
        }
        return false;
    }
}