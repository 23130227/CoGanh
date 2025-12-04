import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState state = new GameState();
            GameController controller = new GameController(state);
            View boardView = new View(state, controller);

            JFrame f = new JFrame("Cờ Gánh – Move along lines only");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel root = new JPanel(new BorderLayout());
            root.add(boardView, BorderLayout.CENTER);

            JLabel modeLabel = new JLabel("Chế độ: " +
                    (controller.getPlayMode() == GameController.PlayMode.HUMAN ? "Chơi với người" : "Chơi với máy"));
            modeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            JButton humanBtn = new JButton("Chơi với người");
            JButton aiBtn = new JButton("Chơi với máy");

            humanBtn.addActionListener(e -> {
                controller.setPlayMode(GameController.PlayMode.HUMAN);
                modeLabel.setText("Chế độ: Chơi với người");
                boardView.repaint();
            });
            aiBtn.addActionListener(e -> {
                controller.setPlayMode(GameController.PlayMode.AI);
                modeLabel.setText("Chế độ: Chơi với máy");
                boardView.repaint();
            });

            buttonsPanel.add(humanBtn);
            buttonsPanel.add(aiBtn);

            JPanel controls = new JPanel(new BorderLayout());
            controls.add(modeLabel, BorderLayout.NORTH);
            controls.add(buttonsPanel, BorderLayout.CENTER);

            root.add(controls, BorderLayout.SOUTH);
            f.setContentPane(root);

            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
