import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TetrisGame extends JPanel {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private static final Color[] COLORS = {
        Color.CYAN, Color.YELLOW, Color.MAGENTA, 
        Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED
    };
    
    private final int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private final int[][][] tetrominoes = {
        {{1,1,1,1}},           // I
        {{1,1}, {1,1}},        // O
        {{1,1,1}, {0,1,0}},    // T
        {{1,1,1}, {1,0,0}},    // L
        {{1,1,1}, {0,0,1}},    // J
        {{0,1,1}, {1,1,0}},    // S
        {{1,1,0}, {0,1,1}}     // Z
    };
    
    private int currentX = 5;
    private int currentY = 0;
    private int currentPiece;
    private int[][] currentShape;
    private Timer timer;
    private boolean isGameOver = false;
    private int score = 0;
    
    public TetrisGame() {
        setPreferredSize(new Dimension(BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isGameOver) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  moveLeft();  break;
                    case KeyEvent.VK_RIGHT: moveRight(); break;
                    case KeyEvent.VK_UP:    rotate();    break;
                    case KeyEvent.VK_DOWN:  dropDown();  break;
                }
                repaint();
            }
        });
        
        newGame();
    }
    
    private void newGame() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = 0;
            }
        }
        newPiece();
        timer = new Timer(500, e -> gameStep());
        timer.start();
    }
    
    private void newPiece() {
        currentPiece = (int)(Math.random() * tetrominoes.length);
        currentShape = tetrominoes[currentPiece];
        currentX = BOARD_WIDTH / 2 - currentShape[0].length / 2;
        currentY = 0;
        
        if (!canMove(currentShape, currentX, currentY)) {
            isGameOver = true;
            timer.stop();
        }
    }
    
    private void gameStep() {
        if (!moveDown()) {
            addToBoard();
            clearLines();
            newPiece();
        }
        repaint();
    }
    
    private boolean moveDown() {
        return canMove(currentShape, currentX, currentY + 1) && tryMove(currentShape, currentX, currentY + 1);
    }
    
    private void moveLeft() {
        tryMove(currentShape, currentX - 1, currentY);
    }
    
    private void moveRight() {
        tryMove(currentShape, currentX + 1, currentY);
    }
    
    private void rotate() {
        int[][] rotated = new int[currentShape[0].length][currentShape.length];
        for (int i = 0; i < currentShape.length; i++) {
            for (int j = 0; j < currentShape[i].length; j++) {
                rotated[j][currentShape.length - 1 - i] = currentShape[i][j];
            }
        }
        if (canMove(rotated, currentX, currentY)) {
            currentShape = rotated;
        }
    }
    
    private void dropDown() {
        while (moveDown());
    }
    
    private boolean canMove(int[][] shape, int newX, int newY) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 0) continue;
                
                int x = newX + j;
                int y = newY + i;
                
                if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) return false;
                if (y < 0) continue;
                if (board[y][x] != 0) return false;
            }
        }
        return true;
    }
    
    private boolean tryMove(int[][] shape, int newX, int newY) {
        if (canMove(shape, newX, newY)) {
            currentX = newX;
            currentY = newY;
            return true;
        }
        return false;
    }
    
    private void addToBoard() {
        for (int i = 0; i < currentShape.length; i++) {
            for (int j = 0; j < currentShape[i].length; j++) {
                if (currentShape[i][j] == 1) {
                    board[currentY + i][currentX + j] = currentPiece + 1;
                }
            }
        }
    }
    
    private void clearLines() {
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == 0) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                score += 100;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, BOARD_WIDTH);
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] > 0) {
                    drawBlock(g, j, i, COLORS[board[i][j] - 1]);
                }
            }
        }
        
        // Draw current piece
        if (!isGameOver) {
            for (int i = 0; i < currentShape.length; i++) {
                for (int j = 0; j < currentShape[i].length; j++) {
                    if (currentShape[i][j] == 1) {
                        drawBlock(g, currentX + j, currentY + i, COLORS[currentPiece]);
                    }
                }
            }
        }
        
        // Draw game over
        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over!", 50, BOARD_HEIGHT * BLOCK_SIZE / 2);
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 25);
    }
    
    private void drawBlock(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new TetrisGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
