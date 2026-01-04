import java.awt.Color;
import java.util.ArrayList; // Cần thiết để tạo danh sách
import java.util.List;      // Cần thiết để khai báo kiểu List

public final class Rules {

    private Rules() {
    }

    /**
     * Kiểm tra tính liền kề và có đường nối (đường chéo hoặc ngang dọc)
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @return
     */
    public static boolean isAdjacentAndConnected(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        // Trường hợp đi ngang hoặc dọc 1 ô
        if (dr + dc == 1) return true;

        // Trường hợp đi chéo
        if (dr == 1 && dc == 1) {
            if (((r1 + c1) % 2) == 0 && ((r2 + c2) % 2) == 0) return true;
        }
        return false;
    }

    /**
     * kiểm tra Gánh (đổi màu quân đối phương)
     * @param board
     * @param currentPlayer
     * @param r
     * @param c
     */
    public static void checkGanh(Color[][] board, Color currentPlayer, int r, int c) {
        //  Gánh dọc
        if (r > 0 && r < board.length - 1) {
            if (board[r + 1][c] != currentPlayer && board[r - 1][c] != currentPlayer && board[r + 1][c] != null && board[r - 1][c] != null) {
                board[r + 1][c] = currentPlayer;
                board[r - 1][c] = currentPlayer;
            }
        }
        //  Gánh ngang
        if (c > 0 && c < board.length - 1) {
            if (board[r][c + 1] != currentPlayer && board[r][c - 1] != currentPlayer && board[r][c + 1] != null && board[r][c - 1] != null) {
                board[r][c + 1] = currentPlayer;
                board[r][c - 1] = currentPlayer;
            }
        }
        //  Gánh chéo
        if (r > 0 && r < board.length - 1 && c > 0 && c < board.length - 1) {
            // Chéo chính
            if (board[r + 1][c + 1] != currentPlayer && board[r - 1][c - 1] != currentPlayer && board[r + 1][c + 1] != null && board[r - 1][c - 1] != null) {
                if ((r + 1 + c + 1) % 2 == 0 && ((r - 1 + c - 1) % 2 == 0)) {
                    board[r + 1][c + 1] = currentPlayer;
                    board[r - 1][c - 1] = currentPlayer;
                }
            }
            // Chéo phụ
            if (board[r - 1][c + 1] != currentPlayer && board[r + 1][c - 1] != currentPlayer && board[r - 1][c + 1] != null && board[r + 1][c - 1] != null) {
                if ((r - 1 + c + 1) % 2 == 0 && ((r + 1 + c - 1) % 2 == 0)) {
                    board[r - 1][c + 1] = currentPlayer;
                    board[r + 1][c - 1] = currentPlayer;
                }
            }
        }
    }

    /**
     * Tìm tất cả nước đi hợp lệ cho người chơi hiện tại
     * @param state
     * @return
     */
    public static List<Move> generateValidMoves(GameState state) {
        List<Move> moves = new ArrayList<>();
        Color[][] board = state.getBoard();
        Color player = state.getCurrentPlayer();
        int size = GameState.SIZE;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                // Nếu ô này là quân của người chơi hiện tại
                if (board[r][c] == player) {
                    // Kiểm tra 8 ô xung quanh (hoặc các ô lân cận)
                    for (int nr = r - 1; nr <= r + 1; nr++) {
                        for (int nc = c - 1; nc <= c + 1; nc++) {
                            // Kiểm tra trong biên bàn cờ
                            if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
                                // Nếu ô đích trống VÀ có đường nối hợp lệ
                                if (board[nr][nc] == null && isAdjacentAndConnected(r, c, nr, nc)) {
                                    moves.add(new Move(r, c, nr, nc));
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}