package org.studyeasy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Game2048GUI extends JPanel {

    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 100;
    private static final int TILE_MARGIN = 16;
    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private int score = 0;
    private int highScore = 0;

    public Game2048GUI() {
        setFocusable(true);
        setPreferredSize(new Dimension(500, 600));
        setBackground(new Color(0xBBADA0));
        setFont(new Font("Arial", Font.BOLD, 36));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> moveUp();
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> moveLeft();
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> moveDown();
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> moveRight();
                    default -> false;
                };

                if (moved) {
                    spawnNumber();
                    repaint();
                    if (isGameOver()) {
                        JOptionPane.showMessageDialog(null, "Game Over! Final Score: " + score);
                        if (score > highScore) {
                            highScore = score;
                        }
                        resetGame();
                    }
                }
            }
        });
        resetGame();
    }

    private void resetGame() {
        score = 0;
        grid = new int[GRID_SIZE][GRID_SIZE];
        spawnNumber();
        spawnNumber();
        repaint();
    }

    private void spawnNumber() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(GRID_SIZE);
            col = random.nextInt(GRID_SIZE);
        } while (grid[row][col] != 0);
        grid[row][col] = random.nextBoolean() ? 2 : 4;
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < GRID_SIZE; i++) {
            int[] newRow = new int[GRID_SIZE];
            int index = 0;

            // Extract non-zero values
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] != 0) {
                    newRow[index++] = grid[i][j];
                }
            }

            // Merge values
            index = 0;
            while (index < GRID_SIZE - 1) {
                if (newRow[index] != 0 && newRow[index] == newRow[index + 1]) {
                    newRow[index] *= 2;
                    score += newRow[index];
                    newRow[index + 1] = 0;
                    moved = true;
                    index += 2; // Skip next index
                } else {
                    index++;
                }
            }

            // Shift values to the left
            index = 0;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (newRow[j] != 0) {
                    if (grid[i][index] != newRow[j]) {
                        grid[i][index] = newRow[j];
                        moved = true;
                    }
                    index++;
                }
            }
            while (index < GRID_SIZE) {
                if (grid[i][index] != 0) {
                    grid[i][index] = 0;
                    moved = true;
                }
                index++;
            }
        }
        return moved;
    }

    private boolean moveRight() {
        rotateGrid(180);
        boolean moved = moveLeft();
        rotateGrid(180);
        return moved;
    }

    private boolean moveUp() {
        rotateGrid(-90);
        boolean moved = moveLeft();
        rotateGrid(90);
        return moved;
    }

    private boolean moveDown() {
        rotateGrid(90);
        boolean moved = moveLeft();
        rotateGrid(-90);
        return moved;
    }

    private void rotateGrid(int angle) {
        int[][] newGrid = new int[GRID_SIZE][GRID_SIZE];
        switch (angle) {
            case 90:
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        newGrid[j][GRID_SIZE - 1 - i] = grid[i][j];
                    }
                }
                break;
            case -90:
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        newGrid[GRID_SIZE - 1 - j][i] = grid[i][j];
                    }
                }
                break;
            case 180:
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        newGrid[GRID_SIZE - 1 - i][GRID_SIZE - 1 - j] = grid[i][j];
                    }
                }
                break;
        }
        grid = newGrid;
    }

    private boolean isGameOver() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    return false;
                }
                if (i < GRID_SIZE - 1 && grid[i][j] == grid[i + 1][j]) {
                    return false;
                }
                if (j < GRID_SIZE - 1 && grid[i][j] == grid[i][j + 1]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
    }

    private void drawGrid(Graphics g) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                drawTile(g, row, col);
            }
        }
        g.setColor(new Color(0x776E65));
        g.drawString("Score: " + score, 20, 50);
        g.drawString("High Score: " + highScore, 300, 50);
    }

    private void drawTile(Graphics g, int row, int col) {
        int value = grid[row][col];
        int xOffset = TILE_MARGIN + col * (TILE_SIZE + TILE_MARGIN);
        int yOffset = TILE_MARGIN + row * (TILE_SIZE + TILE_MARGIN) + 50;
        g.setColor(getTileColor(value));
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 10, 10);
        g.setColor(getTextColor(value));
        if (value != 0) {
            String text = String.valueOf(value);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g.drawString(text, xOffset + (TILE_SIZE - textWidth) / 2, yOffset + (TILE_SIZE + textHeight) / 2);
        }
    }

    private Color getTileColor(int value) {
        return switch (value) {
            case 2 -> new Color(0xEEE4DA);
            case 4 -> new Color(0xEDE0C8);
            case 8 -> new Color(0xF2B179);
            case 16 -> new Color(0xF59563);
            case 32 -> new Color(0xF67C5F);
            case 64 -> new Color(0xF65E3B);
            case 128 -> new Color(0xEDCF72);
            case 256 -> new Color(0xEDCC61);
            case 512 -> new Color(0xEDC850);
            case 1024 -> new Color(0xEDC53F);
            case 2048 -> new Color(0xEDC22E);
            default -> new Color(0xCDC1B4);
        };
    }

    private Color getTextColor(int value) {
        return value < 16 ? new Color(0x776E65) : new Color(0xF9F6F2);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Game2048GUI game = new Game2048GUI();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
