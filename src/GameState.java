// java
import java.awt.Color;

public class GameState {
    public static final int SIZE = 5;

    private final Color[][] board = new Color[SIZE][SIZE];
    private Color currentPlayer = Color.BLUE;

    public GameState() {
        reset();
    }

    public void reset() {
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
        board[2][0] = Color.BLUE;
        board[2][4] = Color.RED;
        currentPlayer = Color.BLUE;
    }

    public Color[][] getBoard() {
        return board;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == Color.BLUE) ? Color.RED : Color.BLUE;
    }

    /**
     *  thực hiện coppy trạng thái trò chơi hiện tại
     * @return
     */
    public GameState copy() {
        GameState newState = new GameState();
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(this.board[r], 0, newState.board[r], 0, SIZE);
        }
        newState.currentPlayer = this.currentPlayer;
        return newState;
    }

    /**
     *  đếm số quân cờ của người chơi
     * @param player
     * @return
     */
    public int countPieces(Color player) {
        int count = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == player) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * kiểm tra game kết thúc hay chưa :
     * 1 kết thúc khi một người chơi không còn quân cờ nào trên bàn
     * 2 kết thúc do bị vây: Nếu đến lượt ai mà người đó không có nước đi hợp lệ
     * @return
     */
    public boolean isGameOver() {
        if (countPieces(Color.BLUE) == 0 || countPieces(Color.RED) == 0) return true;

        // không còn nước đi hợp lệ tương đương với vây
        if (Rules.generateValidMoves(this).isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Xác định người chơi thắng cuộc
     * @return
     */
    public Color getWinner() {
        int blueCount = countPieces(Color.BLUE);
        int redCount = countPieces(Color.RED);

        if (blueCount == 0) return Color.RED;
        if (redCount == 0) return Color.BLUE;


        if (Rules.generateValidMoves(this).isEmpty()) {
            // Nếu đến lượt Blue mà Blue không đi được -> Red thắng
            if (currentPlayer == Color.BLUE) return Color.RED;
            // Ngược lại
            return Color.BLUE;
        }

        return null;
    }

    /**
     * thực hiện di chuyển quân và cập nhật trạng thái bàn cờ, có cả gánh
     * @param move
     */
    public void applyMove(Move move) {
        int r1 = move.getFromRow();
        int c1 = move.getFromCol();
        int r2 = move.getToRow();
        int c2 = move.getToCol();

        // Di chuyển quân
        board[r2][c2] = board[r1][c1];
        board[r1][c1] = null;

        // Kiểm tra Gánh
        Rules.checkGanh(board, currentPlayer, r2, c2);

        // Đổi lượt
        switchTurn();
    }
}
