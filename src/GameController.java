// java

import java.awt.Color;

public class GameController {
    public enum PlayMode {HUMAN, AI}

    private final GameState state;
    private PlayMode playMode = PlayMode.HUMAN;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameController(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode mode) {
        this.playMode = mode;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void handlePress(int row, int col) {
        if (row < 0 || row >= GameState.SIZE || col < 0 || col >= GameState.SIZE) return;

        Color[][] board = state.getBoard();
        Color currentPlayer = state.getCurrentPlayer();

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

        if (board[row][col] == null && Rules.isAdjacentAndConnected(selectedRow, selectedCol, row, col)) {
            board[row][col] = board[selectedRow][selectedCol];
            board[selectedRow][selectedCol] = null;

            Rules.checkGanh(state.getBoard(), currentPlayer, row, col);

            selectedRow = -1;
            selectedCol = -1;
            state.switchTurn();

            if (playMode == PlayMode.AI && state.getCurrentPlayer() == Color.RED) {
                // TODO: implement AI move
            }
        }
    }
}
