import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a 2-player Tic Tac Toe game.
 * 
 * @author MeneXia (Xavi Ablaza)
 *
 */
public class TicTacToe {
    static Scanner in;
    static String[] board;
    static String turn;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        board = new String[9];
        turn = "X";
        String winner = null;
        populateEmptyBoard();

        System.out.println("Welcome to 2 Player Tic Tac Toe.");
        System.out.println("--------------------------------");
        printBoard();
        System.out.println("X's will play first. Enter a slot number to place X in:");

        while (winner == null) {
            int numInput = getInput();
            if (isValidInput(numInput)) {
                placeMove(numInput);
                printBoard();
                winner = checkWinner();
                toggleTurn();
            }
        }

        if (winner.equalsIgnoreCase("draw")) {
            System.out.println("It's a draw! Thanks for playing.");
        } else {
            System.out.println("Congratulations! " + winner + "'s have won! Thanks for playing.");
        }
    }

    /**
     * Gets the user input for the next move.
     *
     * @return the user input
     */
    static int getInput() {
        int numInput;
        try {
            numInput = in.nextInt();
        } catch (InputMismatchException e) {
            numInput = -1; // Invalid input
        }
        return numInput;
    }

    /**
     * Checks if the user input is a valid move.
     *
     * @param numInput the user input
     * @return true if the input is valid, false otherwise
     */
    static boolean isValidInput(int numInput) {
        if (numInput >= 1 && numInput <= 9 && board[numInput - 1].equals(String.valueOf(numInput))) {
            return true;
        } else {
            System.out.println("Invalid input; re-enter slot number:");
            return false;
        }
    }

    /**
     * Places the player's move on the board.
     *
     * @param numInput the user input representing the move
     */
    static void placeMove(int numInput) {
        board[numInput - 1] = turn;
    }

    /**
     * Toggles the turn between players.
     */
    static void toggleTurn() {
        turn = turn.equals("X") ? "O" : "X";
    }

    /**
     * Checks if there is a winner on the current board configuration.
     *
     * @return the winner ("X" or "O") if there is a winner, "draw" if it's a draw,
     *         or null if the game is ongoing.
     */
    static String checkWinner() {
        String[] winningConditions = { "123", "456", "789", "147", "258", "369", "159", "357" };

        for (String condition : winningConditions) {
            String line = board[Integer.parseInt(condition.substring(0, 1)) - 1]
                    + board[Integer.parseInt(condition.substring(1, 2)) - 1]
                    + board[Integer.parseInt(condition.substring(2, 3)) - 1];
            if (line.equals("XXX")) {
                return "X";
            } else if (line.equals("OOO")) {
                return "O";
            }
        }

        for (String cell : board) {
            if (Arrays.asList(board).contains(cell)) {
                break;
            } else if (cell.equals(board[8])) {
                return "draw";
            }
        }

        System.out.println(turn + "'s turn; enter a slot number to place " + turn + " in:");
        return null;
    }

    /**
     * Prints the current board state.
     */
    static void printBoard() {
        System.out.println("/---|---|---\\");
        System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
        System.out.println("|-----------|");
        System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
        System.out.println("|-----------|");
        System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
        System.out.println("/---|---|---\\");
    }

    /**
     * Populates the board array with initial values.
     */
    static void populateEmptyBoard() {
        for (int a = 0; a < 9; a++) {
            board[a] = String.valueOf(a + 1);
        }
    }
}
