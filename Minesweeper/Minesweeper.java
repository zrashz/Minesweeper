import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Minesweeper {
    private static final int GRID_SIZE = 10;
    private static final int NUM_MINES = 15;
    private JFrame frame;
    private JButton[][] buttons;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean gameOver;

    public Minesweeper() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        mines = new boolean[GRID_SIZE][GRID_SIZE];
        revealed = new boolean[GRID_SIZE][GRID_SIZE];
        gameOver = false;

        initializeButtons();
        placeMines();

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void initializeButtons() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 14));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFocusPainted(false);
                buttons[row][col] = button;
                frame.add(button);

                final int r = row;
                final int c = col;

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver)
                            return;

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            revealCell(r, c);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            toggleFlag(r, c);
                        }
                    }
                });
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < NUM_MINES) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (!mines[row][col]) {
                mines[row][col] = true;
                minesPlaced++;
            }
        }
    }

    private void revealCell(int row, int col) {
        if (row < 0 || col < 0 || row >= GRID_SIZE || col >= GRID_SIZE || revealed[row][col]) {
            return;
        }

        revealed[row][col] = true;
        buttons[row][col].setEnabled(false);

        if (mines[row][col]) {
            buttons[row][col].setText("*");
            buttons[row][col].setBackground(Color.RED);
            endGame(false);
        } else {
            int adjacentMines = countAdjacentMines(row, col);
            if (adjacentMines > 0) {
                buttons[row][col].setText(String.valueOf(adjacentMines));
            } else {
                buttons[row][col].setText(" ");
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        revealCell(row + i, col + j);
                    }
                }
            }

            if (checkWin()) {
                endGame(true);
            }
        }
    }

    private void toggleFlag(int row, int col) {
        if (!revealed[row][col] && buttons[row][col].getText().equals("")) {
            buttons[row][col].setText("F");
            buttons[row][col].setBackground(Color.YELLOW);
        } else if (buttons[row][col].getText().equals("F")) {
            buttons[row][col].setText("");
            buttons[row][col].setBackground(null);
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newCol >= 0 && newRow < GRID_SIZE && newCol < GRID_SIZE && mines[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean checkWin() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (!mines[row][col] && !revealed[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void endGame(boolean win) {
        gameOver = true;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (mines[row][col]) {
                    buttons[row][col].setText("*");
                    buttons[row][col].setBackground(Color.RED);
                }
                buttons[row][col].setEnabled(false);
            }
        }

        if (win) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You've cleared the minefield!", "You Win!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Boom! You hit a mine!", "Game Over", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }
}
