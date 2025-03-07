import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class Output{
	
	public void printBoard(Board board) {
		System.out.println("/---|---|---\\");
		System.out.println("| " + board.get(0) + " | " + board.get(1) + " | " + board.get(2) + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board.get(3) + " | " + board.get(4) + " | " + board.get(5) + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board.get(6) + " | " + board.get(7) + " | " + board.get(8) + " |");
		System.out.println("/---|---|---\\");
	}

	public void printWelcome(Board board) {
		System.out.println("Welcome to 2 Player Tic Tac Toe.");
		System.out.println("--------------------------------");
		Output.printBoard(board);
		System.out.println("X's will play first.");

	}
	public void printWinner(Winner winner) {
		if (winner == Winner.Draw) {
			System.out.println("It's a draw! Thanks for playing.");
		} else {
			System.out.println("Congratulations! " + winner + "'s have won! Thanks for playing.");
		}
	}
}

enum Winner
{
	X,
	O,
	Draw,
	None
}
class Board{
  private String[] board;
  public Board() {
		board = new String[9];
  }
  public String get(int cell) {
		return board[cell];
	}
}
class BoardController{
	Board board = new Board();
  
	public BoardController() {
		for (int a = 0; a < 9; a++) {
			board[a] = String.valueOf(a+1);
		}
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
	public Winner isWinner(int cell1,int cell2,int cell3) {
		String line =  board[cell1] + board[cell2] + board[cell3];
		if (line.equals("XXX"))
				return Winner.X;
		if( line.equals("OOO"))
				return Winner.O;
		return Winner.None;
	}
	public Winner checkWinnerInRow(int rowId) {
		int cell1 = rowId * 3;
		int cell2 = cell1 + 1;
		int cell3 = cell2 + 1;
	    return isWinner(cell1,cell2,cell3);
	}
	public Winner checkWinnerInCol(int colId) {
		int cell1 = colId ;
		int cell2 = cell1 + 3;
		int cell3 = cell2 + 3;
		return isWinner(cell1,cell2,cell3);
	}
	public Winner checkWinnerInDiagonal1() {
		int cell1 = 0 ;
		int cell2 = 4;
		int cell3 = 8;
		return isWinner(cell1,cell2,cell3);
	}
	public Winner checkWinnerInDiagonal2() {
		int cell1 = 2 ;
		int cell2 = 4;
		int cell3 = 6;
		return isWinner(cell1,cell2,cell3);
	}
}



class Algorithm{
	Board board;
	String line="";
	
	public Algorithm(Board board) {
		this.board = board;
	}
	
	Winner checkWinnerInRows() {
		for (int i = 0; i < 3; i++) {
    	    Winner winner = board.checkWinnerInRow(i);
    		if (winner  != Winner.None)
    			return  winner;
		}
		return Winner.None;
	}
	Winner checkWinnerInCols() {
		for (int i = 0; i < 3; i++) {
			 Winner winner = board.checkWinnerInCol(i);
			 if (winner  != Winner.None)
	    			return  winner;
		}
		return Winner.None;
	}
	Winner checkWinnerInDiagonal() {
		Winner winner = board.checkWinnerInDiagonal1();
		if (winner  != Winner.None)
 			return  winner;
	    return board.checkWinnerInDiagonal2();
	}
    Winner checkWinner() {
    	Winner winner = checkWinnerInRows();
    	if (winner  != Winner.None)
 			return  winner;
        winner = checkWinnerInCols();
    	if (winner  != Winner.None)
 			return  winner;
    	winner = checkWinnerInDiagonal();
    	if (winner  != Winner.None)
 			return  winner;
  		if(board.isFull())
			return Winner.Draw;	
		return Winner.None;
	}
}
class Input{
	Scanner in;
	public Input() {
		in = new Scanner(System.in);
	}
	
	public int get() {
		int numInput =-1;
		try {
			return in.nextInt();
		} catch (InputMismatchException e) {}
		return numInput;
	}
}
class Controller{
	Board board= new Board();
	Algorithm algorithm = new Algorithm(board);
	Input input = new Input();
	Output output = new Output();
	String turn ="X";
	
	public boolean isValidInput(int num) {
		if (num > 0 && num <= 9) 
			return true;
		return false;
	}
	public void play() {
		Winner winner = Winner.None;
		output.printWelcome(board);
		while (winner == Winner.None) {
			winner = playStep();
		}
		output.printWinner(winner);
	}
	Winner playStep() {
		System.out.println(turn + "'s turn; enter a slot number to place " + turn + " in:");
		int numInput = input.get();
		if (!isValidInput(numInput))
		{
			System.out.println("Invalid input; re-enter slot number:");
			return Winner.None;
		}
		if (!board.isCellAvailable(numInput)) {
			System.out.println("Slot already taken; re-enter slot number:");
			return Winner.None;
		}	
		board.takeCell(numInput,turn);
		switchPlayer();
		output.printBoard(board);
		return algorithm.checkWinner();
	}
	void switchPlayer() {
		if (turn.equals("X")) {
			turn = "O";
		} else {
			turn = "X";
		}
	}
}
public class Main {
	public static void main(String[] args) {
			Controller controller = new Controller();
			controller.play();
		}
}
