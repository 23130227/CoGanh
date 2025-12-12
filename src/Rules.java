import java.awt.Color;
import java.util.ArrayList; // Cần thiết để tạo danh sách
import java.util.List;      // Cần thiết để khai báo kiểu List

public final class Rules {

    private Rules() {
    }

    // Kiểm tra tính liền kề và có đường nối (đường chéo hoặc ngang dọc)
    public static boolean isAdjacentAndConnected(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        // Trường hợp đi ngang hoặc dọc 1 ô
        if (dr + dc == 1) return true;

        // Trường hợp đi chéo
        if (dr == 1 && dc == 1) {
            // Logic bàn cờ gánh: Chỉ những ô có tổng (r+c) chẵn mới có đường chéo
            // Ví dụ: (0,0) -> chẵn (có chéo), (0,1) -> lẻ (không chéo)
            if (((r1 + c1) % 2) == 0 && ((r2 + c2) % 2) == 0) return true;
        }
        return false;
    }

    // Logic kiểm tra Gánh (đổi màu quân đối phương)
    public static void checkGanh(Color[][] board, Color currentPlayer, int r, int c) {
        // 1. Gánh dọc
        if (r > 0 && r < board.length - 1) {
            if (board[r + 1][c] != currentPlayer && board[r - 1][c] != currentPlayer && board[r + 1][c] != null && board[r - 1][c] != null) {
                board[r + 1][c] = currentPlayer;
                board[r - 1][c] = currentPlayer;
            }
        }
        // 2. Gánh ngang
        if (c > 0 && c < board.length - 1) {
            if (board[r][c + 1] != currentPlayer && board[r][c - 1] != currentPlayer && board[r][c + 1] != null && board[r][c - 1] != null) {
                board[r][c + 1] = currentPlayer;
                board[r][c - 1] = currentPlayer;
            }
        }
        // 3. Gánh chéo
        if (r > 0 && r < board.length - 1 && c > 0 && c < board.length - 1) {
            // Chéo chính (\)
            if (board[r + 1][c + 1] != currentPlayer && board[r - 1][c - 1] != currentPlayer && board[r + 1][c + 1] != null && board[r - 1][c - 1] != null) {
                if ((r + 1 + c + 1) % 2 == 0 && ((r - 1 + c - 1) % 2 == 0)) {
                    board[r + 1][c + 1] = currentPlayer;
                    board[r - 1][c - 1] = currentPlayer;
                }
            }
            // Chéo phụ (/)
            if (board[r - 1][c + 1] != currentPlayer && board[r + 1][c - 1] != currentPlayer && board[r - 1][c + 1] != null && board[r + 1][c - 1] != null) {
                if ((r - 1 + c + 1) % 2 == 0 && ((r + 1 + c - 1) % 2 == 0)) {
                    board[r - 1][c + 1] = currentPlayer;
                    board[r + 1][c - 1] = currentPlayer;
                }
            }
        }
    }

    /**
     * Hàm mới: Tìm tất cả nước đi hợp lệ cho người chơi hiện tại
     * (Cần thiết cho AI)
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