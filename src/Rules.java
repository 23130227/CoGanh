import java.awt.*;

// java
public final class Rules {
    private Rules() {
    }

    public static boolean isAdjacentAndConnected(int r1, int c1, int r2, int c2) {
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);

        if (dr + dc == 1) return true;

        if (dr == 1 && dc == 1) {
//            int sum1 = r1 + c1, sum2 = r2 + c2;
//            int diff1 = r1 - c1, diff2 = r2 - c2;
//
//            if (sum1 == sum2 && (sum1 == 2 || sum1 == 4 || sum1 == 6)) return true;
//            if (diff1 == diff2 && (diff1 == -2 || diff1 == 0 || diff1 == 2)) return true;
            if (((r1 + c1) % 2) == 0 && ((r2 + c2) % 2) == 0) return true;
        }
        return false;
    }

    public static void checkGanh(Color[][] board, Color currentPlayer, int r, int c) {
        if (r > 0 && r < board.length - 1) {
            if (board[r + 1][c] != currentPlayer && board[r - 1][c] != currentPlayer && board[r + 1][c] != null && board[r - 1][c] != null) {
                board[r + 1][c] = currentPlayer;
                board[r - 1][c] = currentPlayer;
            }
        }
        if (c > 0 && c < board.length - 1) {
            if (board[r][c + 1] != currentPlayer && board[r][c - 1] != currentPlayer && board[r][c + 1] != null && board[r][c - 1] != null) {
                board[r][c + 1] = currentPlayer;
                board[r][c - 1] = currentPlayer;
            }
        }
        if (r > 0 && r < board.length - 1 && c > 0 && c < board.length - 1) {
            if (board[r + 1][c + 1] != currentPlayer && board[r - 1][c - 1] != currentPlayer && board[r + 1][c + 1] != null && board[r - 1][c - 1] != null) {
                if ((r + 1 + c + 1) % 2 == 0 && ((r - 1 + c - 1) % 2 == 0)) {
                    board[r + 1][c + 1] = currentPlayer;
                    board[r - 1][c - 1] = currentPlayer;
                }
            }
            if (board[r - 1][c + 1] != currentPlayer && board[r + 1][c - 1] != currentPlayer && board[r - 1][c + 1] != null && board[r + 1][c - 1] != null) {
                if ((r - 1 + c + 1) % 2 == 0 && ((r + 1 + c - 1) % 2 == 0)) {
                    board[r - 1][c + 1] = currentPlayer;
                    board[r + 1][c - 1] = currentPlayer;
                }
            }
        }
    }
}