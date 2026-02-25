import javax.swing.*;
import java.awt.event.KeyListener;
import java.util.Scanner;

/**
 * Entry point of the game.
 *
 * @author kphilemon
 */
public class Main {

    public static void main(String[] args) {
        System.out.print("   ______________________  _________    ___    ____ \n" +
                "  /_  __/ ____/_  __/ __ \\/  _/ ___/   |__ \\  / __ \\\n" +
                "   / / / __/   / / / /_/ // / \\__ \\    __/ / / / / /\n" +
                "  / / / /___  / / / _, _// / ___/ /   / __/_/ /_/ / \n" +
                " /_/ /_____/ /_/ /_/ |_/___//____/   /____(_)____/  \n\n" +
                "Howdy! Welcome to Tetris 2.0!\n" +
                "\n" +
                "Imagine, Tetris blocks are no longer falling from the top because it is boring and annoying " +
                "as hell. In Tetris 2.0, Tetris blocks can be placed by yourself anywhere you want on the " +
                "Tetris board (of course it must be able to fit in and not overlapped), without any time " +
                "constraint and gravity. No more pressure of Tetris blocks falling!\n" +
                "\n" +
                "The scoring rules are about the same but it is more interesting this time - you get points " +
                "for eliminating not only fully-filled horizontal rows but also vertical columns as well if " +
                "and only if the numbers inside the squares of the row or column sums up to even!\n" +
                "\n" +
                "Have fun! Press enter to continue...");

        // wait for player to press enter
        Scanner s = new Scanner(System.in);
        String input;
        do {
            input = s.nextLine();
        } while (!input.equals(""));

        TetrisGame game = new TetrisGame();
        setupWindowForKeyListener(game);
    }

    // setting up a JFrame to capture key events
    private static void setupWindowForKeyListener(KeyListener k) {
        JFrame jFrame = new JFrame();
        jFrame.setVisible(true);
        jFrame.setSize(100, 100);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.addKeyListener(k);
    }
}
