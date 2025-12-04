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
}
