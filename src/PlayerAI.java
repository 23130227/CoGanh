import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerAI {
    private static final int MAX_DEPTH = 4;
    private long nodeCount=0;
    private static final int[][] POSITION_WEIGHTS = {
            {1, 1, 1, 1, 1},
            {1, 2, 2, 2, 1},
            {1, 2, 3, 2, 1},
            {1, 2, 2, 2, 1},
            {1, 1, 1, 1, 1}
    };

    /**
     * thực hiện tìm nước đi tốt nhất sử dụng thuật toán Minimax với cắt tỉa Alpha-Beta
     * @param currentState
     * @return
     */
    public Move findBestMove(GameState currentState) {
        nodeCount=0;
        long startTime = System.currentTimeMillis();

        List<Move> validMoves = Rules.generateValidMoves(currentState);
        Collections.shuffle(validMoves);

        Move bestMove = null;
        int temp = -999999999;


        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : validMoves) {
            GameState clonedState = currentState.copy();
            clonedState.applyMove(move);


            int value = minimax(false, clonedState, MAX_DEPTH - 1, alpha, beta);

            if (value > temp) {
                temp = value;
                bestMove = move;
            }


            alpha = Math.max(alpha, temp);
        }
        /**
         * thực hiện in ra màn hình báo cáo kết quả sau khi AI suy nghĩ xong
         */
        long endTime = System.currentTimeMillis();
        System.out.println("=========================================");
        System.out.println("[ALPHA-BETA REPORT]");
        System.out.println("- Độ sâu (Depth): " + MAX_DEPTH);
        System.out.println("- Số trạng thái đã duyệt (Nodes): " + nodeCount);
        System.out.println("- Thời gian suy nghĩ: " + (endTime - startTime) + " ms");
        System.out.println("=========================================");
        return bestMove;
    }

    /**
     * thực hiện thuật toán Minimax với cắt tỉa Alpha-Beta
     * @param maxmin
     * @param state
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */

    private int minimax(boolean maxmin, GameState state, int depth, int alpha, int beta) {
       nodeCount++;
        if (depth == 0 || state.isGameOver()) {
            return heuristic(state);
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

                int value = minimax(false, newState, depth - 1, alpha, beta);

                if (value > temp) {
                    temp = value;
                }
                alpha = Math.max(alpha, temp);
                if (beta <= alpha) break;
            }
            return temp;

        } else {
            int temp = 999999999;

            for (Move move : validMoves) {
                GameState newState = state.copy();
                newState.applyMove(move);



                int value = minimax(true, newState, depth - 1, alpha, beta);

                if (value < temp) {
                    temp = value;
                }

                beta = Math.min(beta, temp);
                if (beta <= alpha) break;
            }
            return temp;
        }
    }

    /**
     * hàm đánh giá heuristic cho trạng thái trò chơi
     * @param state
     * @return
     */
    private int heuristic(GameState state) {
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