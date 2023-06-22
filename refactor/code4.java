import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class Board{
	public String[] board;
	
	public Board() {
		board = new String[9];
		for (int a = 0; a < 9; a++) {
			board[a] = String.valueOf(a+1);
		}
	}
	public String get(int cell) {
		return board[cell];
	}
	public boolean isCellAvailable(int numInput) {
		return board[numInput-1].equals(String.valueOf(numInput));
	}
	public void takeCell(int numInput, String player) {
		board[numInput-1] = player;
	}
	public boolean isFull() {
		for (int a = 0; a < 9; a++) {
			if (Arrays.asList(board).contains(String.valueOf(a+1))) {
				return false;
			}
		}
		return true;
	}
	public String getRow(int rowId) {
		int cell1 = rowId * 3;
		int cell2 = cell1 + 1;
		int cell3 = cell2 + 1;
		return board[cell1] + board[cell2] + board[cell3];
	}
	public String getCol(int colId) {
		int cell1 = colId ;
		int cell2 = cell1 + 3;
		int cell3 = cell2 + 3;
		return board[cell1] + board[cell2] + board[cell3];
	}
	public String getDiagonal1() {
		return board[0] + board[4] + board[8];
	}
	public String getDiagonal2() {
		return board[2] + board[4] + board[6];
	}
}
class Output{
	
	public static void printBoard(Board board) {
		System.out.println("/---|---|---\\");
		System.out.println("| " + board.get(0) + " | " + board.get(1) + " | " + board.get(2) + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board.get(3) + " | " + board.get(4) + " | " + board.get(5) + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board.get(6) + " | " + board.get(7) + " | " + board.get(8) + " |");
		System.out.println("/---|---|---\\");
	}

	public static void printWelcome(Board board) {
		System.out.println("Welcome to 2 Player Tic Tac Toe.");
		System.out.println("--------------------------------");
		Output.printBoard(board);
		System.out.println("X's will play first.");

	}
}
class Algorithm{
	Board board;
	public Algorithm(Board board) {
		this.board = board;
	}
	public boolean isWinner(String line) {
		if (line.equals("XXX") || line.equals("OOO")) {
			return true;
		}
		return false;
	}
    String checkWinner() {
    	String line="";
    	for (int i = 0; i < 3; i++) {
    	    line = board.getRow(i);
    		if (isWinner(line))
    			break;
    		
    		line = board.getCol(i);
    		if (isWinner(line))
    			break;
		}

		if (line.equals("XXX")) {
			return "X";
		} else if (line.equals("OOO")) {
			return "O";
		}

		line = board.getDiagonal1();
		if (line.equals("XXX")) {
			return "X";
		} else if (line.equals("OOO")) {
			return "O";
		}
		
		line = board.getDiagonal2();
		if (line.equals("XXX")) {
			return "X";
		} else if (line.equals("OOO")) {
			return "O";
		}
		
		if(board.isFull())
			return "draw";
		
		return null;
	}

}
class Input{
	Scanner in;
	public Input() {
		in = new Scanner(System.in);
	}
	public int get() {
		int numInput =-1;
		while(numInput < 0)
		{
			try {
				numInput = in.nextInt();
				if (!(numInput > 0 && numInput <= 9)) {
					System.out.println("Invalid input; re-enter slot number:");
				}
				return numInput;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input; re-enter slot number:");
			}
		}
		return -1;
	}
}
class Controller{
	Board board= new Board();
	Algorithm algorithm = new Algorithm(board);
	Input input = new Input();
	String turn ="X";
	public void play() {
		String winner = null;
		Output.printWelcome(board);
		while (winner == null) {
			System.out.println(turn + "'s turn; enter a slot number to place " + turn + " in:");
			int numInput = input.get();
			if (board.isCellAvailable(numInput)) {
				winner = playTurn(numInput);
			} 
			else {
				System.out.println("Slot already taken; re-enter slot number:");
			}
		}
		if (winner.equalsIgnoreCase("draw")) {
			System.out.println("It's a draw! Thanks for playing.");
		} else {
			System.out.println("Congratulations! " + winner + "'s have won! Thanks for playing.");
		}
	}
	String playTurn(int numInput ) {
		board.takeCell(numInput,turn);
		if (turn.equals("X")) {
			turn = "O";
		} else {
			turn = "X";
		}
		Output.printBoard(board);
		return algorithm.checkWinner();
	}
}
public class Main {
	public static void main(String[] args) {
			Controller controller = new Controller();
			controller.play();
		}
}
