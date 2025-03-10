import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class InputBoundary{
	static Scanner in;

  public void collectInput(){
    	in = new Scanner(System.in);
	
  }
  public int GetInput(){
  int numInput;
			try {
				numInput = in.nextInt();
				if (!(numInput > 0 && numInput <= 9)) {
					System.out.println("Invalid input; re-enter slot number:");
					continue;
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid input; re-enter slot number:");
				continue;
			}
	  return numInput();
	  
  }
}
class OutputBoundary{
 void printBoard() {
		System.out.println("/---|---|---\\");
		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
		System.out.println("/---|---|---\\");
	}
	void printWelcome(){
		System.out.println("Welcome to 2 Player Tic Tac Toe.");
		System.out.println("--------------------------------");
	}
	void printRules(){
		System.out.println("X's will play first. Enter a slot number to place X in:");
	}
}
class BoardDTO{
   String[] board;
  
  ~BoardDto(){
      board = new String[9];
	}
}
class BoardDomain{
  BoardDTO board;
 	String turn;
  String winner = null;
  
  BoardDomain(BoardDTO board){
 	turn = "X";
		populateEmptyBoard();
  }
  void populateEmptyBoard() {
		for (int a = 0; a < 9; a++) {
			board[a] = String.valueOf(a+1);
		}
	}
  void SetState(int numInput){
  	if (board[numInput-1].equals(String.valueOf(numInput))) {
				board[numInput-1] = turn;
				if (turn.equals("X")) {
					turn = "O";
				} else {
					turn = "X";
				}
	}
  }
}
class GameDomain{
  bool IsComplete(){
   ....
  }
  String checkWinner() {
		for (int a = 0; a < 8; a++) {
			String line = null;
			switch (a) {
			case 0:
				line = board[0] + board[1] + board[2];
				break;
			case 1:
				line = board[3] + board[4] + board[5];
				break;
			case 2:
				line = board[6] + board[7] + board[8];
				break;
			case 3:
				line = board[0] + board[3] + board[6];
				break;
			case 4:
				line = board[1] + board[4] + board[7];
				break;
			case 5:
				line = board[2] + board[5] + board[8];
				break;
			case 6:
				line = board[0] + board[4] + board[8];
				break;
			case 7:
				line = board[2] + board[4] + board[6];
				break;
			}
			if (line.equals("XXX")) {
				return "X";
			} else if (line.equals("OOO")) {
				return "O";
			}
		}

		for (int a = 0; a < 9; a++) {
			if (Arrays.asList(board).contains(String.valueOf(a+1))) {
				break;
			}
			else if (a == 8) return "draw";
		}

		System.out.println(turn + "'s turn; enter a slot number to place " + turn + " in:");
		return null;
	}
}
class Controller{
  void Execute(){
	  BoardDTO dto = new BoardDTO();
  	  OutputBoundary output = new OutputBoundary();
	  output.printWelcome();
	  output.printBoard(dto);
	  output.PrintRules();
	  GameDomain game = new GameDomain();
	  InputBoundary input = new InputBoundary();
	  BoardDomain boardDomain = new BoardDomain(dto);
	  while (game.IsCompleted()) {
			int numInput = input.getInput();
		        if(game.IsPlayValid(numInput))
			if (board[numInput-1].equals(String.valueOf(numInput))) {
				boardDomain.SetState(numInput);
				output.printBoard();
				game.checkWinner();
			} else {
				output.printSlotTaken();
			}
		}
		if (game.IsDraw("draw")) {
			output.printDraw();
		} else {
			output.printWinner(game.getWinnner());
		}
  }
}
public class TicTacToe {
	public static void main(String[] args) {
	  Controller controller = new Controller();
    controller.execute();
	}
}
