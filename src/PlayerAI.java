import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerAI {
    private static final int MAX_DEPTH = 4;

    private static final int[][] POSITION_WEIGHTS = {
            {1, 1, 1, 1, 1},
            {1, 2, 2, 2, 1},
            {1, 2, 3, 2, 1},
            {1, 2, 2, 2, 1},
            {1, 1, 1, 1, 1}
    };

    public Move findBestMove(GameState currentState) {
        List<Move> validMoves = Rules.generateValidMoves(currentState);
        Collections.shuffle(validMoves);

        Move bestMove = null;
        int temp = -999999999;

        // --- THÊM ALPHA BETA ---
        int alpha = Integer.MIN_VALUE; // Khởi tạo Alpha
        int beta = Integer.MAX_VALUE;  // Khởi tạo Beta

        for (Move move : validMoves) {
            GameState clonedState = currentState.copy();
            clonedState.applyMove(move);

            // --- THÊM ALPHA BETA ---
            // Truyền alpha và beta vào hàm đệ quy
            int value = minimax(false, clonedState, MAX_DEPTH - 1, alpha, beta);

            if (value > temp) {
                temp = value;
                bestMove = move;
            }

            // --- THÊM ALPHA BETA ---
            // Cập nhật Alpha tại node gốc để tối ưu cho các nhánh sau
            alpha = Math.max(alpha, temp);
        }
        return bestMove;
    }

    // --- THÊM ALPHA BETA ---
    // Thêm tham số int alpha, int beta vào hàm
    private int minimax(boolean maxmin, GameState state, int depth, int alpha, int beta) {
        if (depth == 0 || state.isGameOver()) {
            return evaluate(state);
        }

        List<Move> validMoves = Rules.generateValidMoves(state);

        if (validMoves.isEmpty()) {
            return maxmin ? -20000 : 20000;
        }

        if (maxmin == true) {
            int temp = -999999999;

            for (Move move : validMoves) {
                GameState newState = state.copy();
                newState.applyMove(move);

                // --- THÊM ALPHA BETA ---
                // Truyền alpha, beta xuống cấp dưới
                int value = minimax(false, newState, depth - 1, alpha, beta);

                if (value > temp) {
                    temp = value;
                }

                // --- THÊM ALPHA BETA ---
                alpha = Math.max(alpha, temp); // Cập nhật Alpha (giá trị tốt nhất cho MAX)
                if (beta <= alpha) break;      // Cắt tỉa: Nếu Beta <= Alpha thì dừng nhánh này
            }
            return temp;

        } else {
            int temp = 999999999;

            for (Move move : validMoves) {
                GameState newState = state.copy();
                newState.applyMove(move);

                // --- THÊM ALPHA BETA ---
                // Truyền alpha, beta xuống cấp dưới
                int value = minimax(true, newState, depth - 1, alpha, beta);

                if (value < temp) {
                    temp = value;
                }

                // --- THÊM ALPHA BETA ---
                beta = Math.min(beta, temp); // Cập nhật Beta (giá trị tốt nhất cho MIN)
                if (beta <= alpha) break;    // Cắt tỉa: Nếu Beta <= Alpha thì dừng nhánh này
            }
            return temp;
        }
    }

    private int evaluate(GameState state) {
        Color winner = state.getWinner();
        if (winner == Color.RED) return 100000;
        if (winner == Color.BLUE) return -100000;

        int score = 0;
        Color[][] board = state.getBoard();
        List<Point> redPieces = new ArrayList<>();
        List<Point> bluePieces = new ArrayList<>();

        for (int r = 0; r < GameState.SIZE; r++) {
            for (int c = 0; c < GameState.SIZE; c++) {
                if (board[r][c] == Color.RED) {
                    redPieces.add(new Point(r, c));
                    score += POSITION_WEIGHTS[r][c] * 5;
                } else if (board[r][c] == Color.BLUE) {
                    bluePieces.add(new Point(r, c));
                    score -= POSITION_WEIGHTS[r][c] * 5;
                }
            }
        }

        score += (redPieces.size() - bluePieces.size()) * 1000;

        if (state.getCurrentPlayer() == Color.RED) {
            int totalDistance = 0;
            for (Point red : redPieces) {
                int minDis = 100;
                for (Point blue : bluePieces) {
                    int d = Math.abs(red.x - blue.x) + Math.abs(red.y - blue.y);
                    if (d < minDis) minDis = d;
                }
                totalDistance += minDis;
            }
            score -= totalDistance * 10;
        }

        int redMoves = Rules.generateValidMoves(state).size();
        score += redMoves * 5;

        return score;
    }
}