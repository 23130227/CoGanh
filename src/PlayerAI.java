import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections; // <-- MỚI: Dùng để xáo trộn nước đi
import java.util.List;

public class PlayerAI {
    // Độ sâu 4 là đủ tốt cho Cờ Gánh 5x5
    private static final int MAX_DEPTH = 4;

    // Bảng điểm vị trí: Ưu tiên chiếm tâm (3 điểm) và các giao điểm (2 điểm)
    // Các ô góc hoặc biên ít đường đi sẽ ít điểm hơn (1 điểm)
    private static final int[][] POSITION_WEIGHTS = {
            {1, 1, 1, 1, 1},
            {1, 2, 2, 2, 1},
            {1, 2, 3, 2, 1}, // Ô (2,2) ở giữa là quan trọng nhất
            {1, 2, 2, 2, 1},
            {1, 1, 1, 1, 1}
    };

    public Move findBestMove(GameState currentState) {
        return minimaxRoot(currentState, MAX_DEPTH, true);
    }

    private Move minimaxRoot(GameState state, int depth, boolean isMaximizing) {
        List<Move> validMoves = Rules.generateValidMoves(state);

        // --- QUAN TRỌNG: Xáo trộn các nước đi ---
        // Để nếu có nhiều nước đi bằng điểm nhau, AI sẽ chọn ngẫu nhiên
        // giúp phá vỡ vòng lặp vô tận.
        Collections.shuffle(validMoves);

        Move bestMove = null;
        int bestValue = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : validMoves) {
            GameState clonedState = state.copy();
            clonedState.applyMove(move);

            int value = minimax(clonedState, depth - 1, alpha, beta, !isMaximizing);

            if (isMaximizing) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                alpha = Math.max(alpha, bestValue);
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                beta = Math.min(beta, bestValue);
            }
        }
        return bestMove;
    }

    private int minimax(GameState state, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || state.isGameOver()) {
            return evaluate(state);
        }

        List<Move> validMoves = Rules.generateValidMoves(state);

        // Xử lý luật Vây: Hết nước đi là thua
        if (validMoves.isEmpty()) {
            return isMaximizing ? -20000 : 20000;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : validMoves) {
                GameState clonedState = state.copy();
                clonedState.applyMove(move);

                int eval = minimax(clonedState, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : validMoves) {
                GameState clonedState = state.copy();
                clonedState.applyMove(move);

                int eval = minimax(clonedState, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    // --- CẬP NHẬT HÀM CHẤM ĐIỂM SĂN MỒI ---
    private int evaluate(GameState state) {
        // 1. Kiểm tra thắng thua tuyệt đối (Vẫn giữ nguyên)
        Color winner = state.getWinner();
        if (winner == Color.RED) return 100000;
        if (winner == Color.BLUE) return -100000;

        int score = 0;
        Color[][] board = state.getBoard();

        // Danh sách toạ độ các quân cờ để tính khoảng cách
        List<Point> redPieces = new ArrayList<>();
        List<Point> bluePieces = new ArrayList<>();

        // 2. Quét bàn cờ
        for (int r = 0; r < GameState.SIZE; r++) {
            for (int c = 0; c < GameState.SIZE; c++) {
                if (board[r][c] == Color.RED) {
                    redPieces.add(new Point(r, c));
                    // Giảm bớt sự quan trọng của vị trí tĩnh
                    // Để AI không quá luyến tiếc ô trung tâm
                    score += POSITION_WEIGHTS[r][c] * 5;
                } else if (board[r][c] == Color.BLUE) {
                    bluePieces.add(new Point(r, c));
                    score -= POSITION_WEIGHTS[r][c] * 5;
                }
            }
        }

        // 3. Điểm quân số (Vẫn là quan trọng nhất - Hệ số to)
        score += (redPieces.size() - bluePieces.size()) * 1000;

        // 4. --- LOGIC SĂN MỒI (MỚI) ---
        // Nếu AI (RED) đang có lợi thế hoặc ngang bằng, hãy ép nó áp sát địch.
        if (state.getCurrentPlayer() == Color.RED) {
            int totalDistance = 0;
            // Với mỗi quân Đỏ, tìm quân Xanh gần nhất
            for (Point red : redPieces) {
                int minDis = 100;
                for (Point blue : bluePieces) {
                    // Tính khoảng cách Manhattan (trị tuyệt đối)
                    int d = Math.abs(red.x - blue.x) + Math.abs(red.y - blue.y);
                    if (d < minDis) minDis = d;
                }
                // Cộng tổng khoảng cách ngắn nhất
                totalDistance += minDis;
            }

            // Mẹo: Trừ điểm khoảng cách vào Score.
            // Khoảng cách càng nhỏ (gần địch) -> Bị trừ ít -> Điểm cao hơn.
            // Khoảng cách càng lớn (xa địch) -> Bị trừ nhiều -> Điểm thấp.
            score -= totalDistance * 10;
        }

        // 5. Điểm linh hoạt (Mobility)
        int redMoves = Rules.generateValidMoves(state).size();
        int blueMoves = 0;
        // (Để tối ưu tốc độ, ta có thể không tính blueMoves nếu không cần thiết,
        // hoặc chỉ ước lượng. Ở đây tính redMoves để khuyến khích AI mở đường).
        score += redMoves * 5;

        return score;
    }
}