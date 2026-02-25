import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class encapsulates all the game logic of Tetris 2.0
 *
 * @author kphilemon
 */
public class TetrisGame implements KeyListener {

    private int score;
    private int highScore;

    private int X, Y;
    private int[][] map;
    private Tetromino hold;
    private ArrayList<Tetromino> blockQueue;

    private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

    public TetrisGame() {
        map = new int[10][10];
        blockQueue = new ArrayList<>();
        resetXY();
        randomGenerateBlock();
        insertShadow();
        readHighScore();
        displayGameUI();
    }

    // ------------------------------- GAME INTERFACE DISPLAY ------------------------------- //

    private void displayGameUI() {
        clearConsole();
        printMap();
        printBlockQueue();
        System.out.println(" **click on the small window to enable key controls\n MOVE [arrow keys] ROTATE [w] HOLD [s] INSERT [space] EXIT [e]");
    }

    private void printMap() {
        System.out.print("\n |-");
        for (int i = 1; i <= map.length; i++) {
            System.out.print("--");
        }
        System.out.println("|");

        for (int i = 0; i < map.length; i++) {
            System.out.print(" | ");
            for (int j : map[i]) {
                // the essence of printing
                // in the 2D int map, the possible number representations after inserting shadow are as follows:
                // 0            : empty, displayed as spaces
                // -11          : if previously it is empty, after shadow being inserted, the number will become -11 (0 - 11 = -11), shadow appears as + to show valid
                // 1 to 10      : numbers inserted (refer NUMBER_WITHIN_BLOCKS of Tetromino class)
                // -1 to -10    : if numbers has been inserted (1 to 10), after shadow being inserted, numbers will decrease by 11 and fall within this range, shadow appears as x to show overlapping

                if (j == 0) {
                    System.out.print("  ");
                } else if (j == -11) {
                    System.out.print("+ ");
                } else if (j < 0 && j > -11) {
                    System.out.print("x ");
                } else if (j > 0 && j < 11) {
                    // when inserted, it was shift right by one (refer insertValue method), so shift back to meet game requirement
                    System.out.print(j - 1 + " ");
                } else {
                    // WTF... What a terrible failure... will not happen
                    System.out.print("??");
                }
            }
            System.out.print("|");
            switch (i) {
                case 0:
                    System.out.printf("  Highest score: %d (DEV)", highScore);
                    break;
                case 2:
                    System.out.printf("  Score: %d", score);
                    break;
                case 4:
                    System.out.print("  Hold:");
                    break;
                case 6:
                case 7:
                    if (hold != null) {
                        System.out.print(" " + hold.getPattern()[i - 6]);
                    }
                    break;
            }
            System.out.println();
        }

        System.out.print(" |-");
        for (int i = 1; i <= map.length; i++) {
            System.out.print("--");
        }
        System.out.println("|");
    }

    private void printBlockQueue() {
        String first = " ", second = " ";

        for (int i = blockQueue.size() - 1; i >= 0; i--) {
            Tetromino block = blockQueue.get(i);
            first += block.getPattern()[0];
            second += block.getPattern()[1];
        }

        System.out.println("\n" + first + "\n" + second + "\n");
    }

    private void clearConsole() {
        final String os = System.getProperty("os.name");

        try {
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------- GAME UTILITIES ----------------------------------- //

    private void resetXY() {
        X = 3;
        Y = 0;
    }

    private void insertValue(Tetromino block, int x, int y, boolean custom, int customValue) {
        for (int i = 0; i < 4; i++) {
            int by = block.getCoordinates()[2 * i] + y;
            int bx = block.getCoordinates()[2 * i + 1] + x;

            if (custom) {
                map[by][bx] += customValue;
            } else {
                // shifting right by 1 because 0 is used to represent empty
                map[by][bx] += block.getNumbers()[i] + 1;
            }
        }
    }

    private void insertShadow() {
        insertValue(blockQueue.get(0), X, Y, true, -11);
    }

    private void removeShadow() {
        insertValue(blockQueue.get(0), X, Y, true, +11);
    }

    private void clearRowOrColumn() {
        ArrayList<Integer> rowsToClear = new ArrayList<>();
        ArrayList<Integer> columnsToClear = new ArrayList<>();

        // row by row checking
        for (int i = 0; i < map.length; i++) {
            boolean isClearable = true;
            int sum = 0;
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] < 1) {
                    isClearable = false;
                    break;
                }
                sum += map[i][j];
            }
            if (isClearable && sum % 2 == 0) {
                rowsToClear.add(i);
            }
        }

        for (int i = 0; i < map[0].length; i++) {
            boolean isClearable = true;
            int sum = 0;
            for (int j = 0; j < map.length; j++) {
                if (map[j][i] < 1) {
                    isClearable = false;
                    break;
                }
                sum += map[j][i];
            }
            if (isClearable && sum % 2 == 0) {
                columnsToClear.add(i);
            }
        }

        for (int i : rowsToClear) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 0;
            }
        }

        for (int i : columnsToClear) {
            for (int j = 0; j < map.length; j++) {
                map[j][i] = 0;
            }
        }

        score += rowsToClear.size() + columnsToClear.size();
        if (score > highScore) {
            highScore = score;
        }
    }

    private void randomGenerateBlock() {
        for (int i = blockQueue.size(); i < 3; i++) {
            blockQueue.add(Tetromino.create());
        }
    }

    // ----------------------------------- PLAYER ACTIONS ----------------------------------- //

    private void moveBlock(int direction) {
        if (validateMovement(direction)) {
            removeShadow();
            switch (direction) {
                case UP:
                    Y--;
                    break;
                case DOWN:
                    Y++;
                    break;
                case LEFT:
                    X--;
                    break;
                case RIGHT:
                    X++;
                    break;
            }
            insertShadow();
            displayGameUI();
        } else {
            System.out.println("Dude, you cant move out of the box...");
        }
    }

    private void holdBlock() {
        removeShadow();
        if (hold == null) {
            hold = blockQueue.remove(0);
            randomGenerateBlock();
        } else {
            blockQueue.add(hold);
            hold = blockQueue.remove(0);
            // to rotate the list
            for (int i = 0; i < blockQueue.size() - 1; i++) {
                blockQueue.add(blockQueue.remove(0));
            }
        }
        resetXY();
        insertShadow();
        displayGameUI();
    }

    private void rotateBlock() {
        if (validateRotation()) {
            removeShadow();
            blockQueue.get(0).rotate();
            insertShadow();
            displayGameUI();
        } else {
            System.out.println("Dude, your rotation will move your block out of the box... Move in inwards and try again");
        }
    }

    private void insertCurrentBlock() {
        if (validateInsertion()) {
            removeShadow();
            insertValue(blockQueue.remove(0), X, Y, false, 0);
            clearRowOrColumn();

            // reset the new current block and generate a new one
            resetXY();
            insertShadow();
            randomGenerateBlock();
            displayGameUI();
        } else {
            System.out.println("Dude, you can't insert there... It's overlapping...");
        }
    }

    // --------------------------------- ACTION VALIDATIONS --------------------------------- //

    private boolean validateMovement(int direction) {
        for (int i = 0; i < 4; i++) {
            int by = blockQueue.get(0).getCoordinates()[2 * i] + Y;
            int bx = blockQueue.get(0).getCoordinates()[2 * i + 1] + X;

            switch (direction) {
                case UP:
                    if (by < 1) return false;
                    break;
                case DOWN:
                    if (by > map.length - 2) return false;
                    break;
                case LEFT:
                    if (bx < 1) return false;
                    break;
                case RIGHT:
                    if (bx > map[0].length - 2) return false;
                    break;
            }
        }
        return true;
    }

    private boolean validateRotation() {
        blockQueue.get(0).rotate();
        for (int i = 0; i < 4; i++) {
            int by = blockQueue.get(0).getCoordinates()[2 * i] + Y;
            int bx = blockQueue.get(0).getCoordinates()[2 * i + 1] + X;

            if (by < 0 || by > map.length - 1 || bx < 0 || bx > map[0].length - 1) {
                blockQueue.get(0).unrotate();
                return false;
            }
        }
        blockQueue.get(0).unrotate();
        return true;
    }

    private boolean validateInsertion() {
        for (int i = 0; i < 4; i++) {
            int by = blockQueue.get(0).getCoordinates()[2 * i] + Y;
            int bx = blockQueue.get(0).getCoordinates()[2 * i + 1] + X;

            if (map[by][bx] != -11) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------ KEY LISTENER ------------------------------------ //

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                moveBlock(UP);
                break;
            case KeyEvent.VK_DOWN:
                moveBlock(DOWN);
                break;
            case KeyEvent.VK_LEFT:
                moveBlock(LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                moveBlock(RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                insertCurrentBlock();
                break;
            case KeyEvent.VK_W:
                rotateBlock();
                break;
            case KeyEvent.VK_S:
                holdBlock();
                break;
            case KeyEvent.VK_E:
                writeHighScore();
                System.exit(0);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    // --------------------------------- HIGH SCORE FILE IO --------------------------------- //

    private void readHighScore() {
        File f = new File("high_score.txt");
        if (f.exists() && !f.isDirectory()) {
            try {
                highScore = Integer.parseInt(new String(Files.readAllBytes(f.toPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeHighScore() {
        try {
            Files.write(Paths.get("high_score.txt"), (highScore + "").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
