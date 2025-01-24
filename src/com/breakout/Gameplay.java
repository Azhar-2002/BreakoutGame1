package com.breakout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int paddleX = 310; // Paddle's starting position
    private int ballPosX = 120, ballPosY = 350; // Ball's starting position
    private double ballDirX = -1, ballDirY = -2; // Ball's direction (use double for smooth speed increase)

    private MapGenerator map;

    private int level = 1; // Current level of the game

    public Gameplay() {
        startLevel(level);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(delay, this);
        timer.start();
    }

    private void startLevel(int level) {
        int rows = 3 + level; // Increase rows with level
        int cols = 7 + level; // Increase columns with level
        map = new MapGenerator(rows, cols);

        totalBricks = rows * cols;
        ballPosX = 120;
        ballPosY = 350;
        // Increase speed by 1.5 times each level
        ballDirX = -1 * Math.pow(1.5, level - 1); // Speed increase per level (1.5x per level)
        ballDirY = -2 * Math.pow(1.5, level - 1);
        paddleX = 310;
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Drawing map
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Paddle
        g.setColor(Color.green);
        g.fillRect(paddleX, 550, 100, 8);

        // Ball
        g.setColor(Color.red);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // Scores and Level
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Score: " + score, 550, 30);
        g.drawString("Level: " + level, 50, 30);

        if (totalBricks <= 0) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.GREEN);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won Level " + level + "!", 220, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Start Level " + (level + 1), 200, 350);
        }

        if (ballPosY > 570) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over!", 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            ballPosX += ballDirX;
            ballPosY += ballDirY;

            // Paddle collision
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(paddleX, 550, 100, 8))) {
                ballDirY = -ballDirY;
            }

            // Brick collision
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }

                            break A;
                        }
                    }
                }
            }

            // Wall collision
            if (ballPosX < 0 || ballPosX > 670) {
                ballDirX = -ballDirX;
            }
            if (ballPosY < 0) {
                ballDirY = -ballDirY;
            }

            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && paddleX < 600) {
            paddleX += 20;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && paddleX > 0) {
            paddleX -= 20;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                if (totalBricks <= 0) {
                    level++; // Move to the next level
                    startLevel(level);
                } else {
                    // Prompt to play again at the same level
                    if (level >= 2) {
                        int response = JOptionPane.showConfirmDialog(null, "You lost at Level " + level + ". Do you want to play again at this level?", "Game Over", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            startLevel(level); // Restart the same level
                        } else {
                            // Restart the game from level 1
                            level = 1;
                            startLevel(level);
                        }
                    } else {
                        level = 1; // Restart the game from level 1
                        startLevel(level);
                    }
                }
                play = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
