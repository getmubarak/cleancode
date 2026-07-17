public class Output{
  void printBoard(board ?) {
		System.out.println("/---|---|---\\");
		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
		System.out.println("/---|---|---\\");
	}
  void printWelcome(board ?){
    System.out.println("Welcome to 2 Player Tic Tac Toe.");
		System.out.println("--------------------------------");
		printBoard();
		System.out.println("X's will play first. Enter a slot number to place X in:");
  }
}
public class Input{
  int getInput(){
    int numInput;
			try {
			   numInput = getKeyboardInput();
			} catch (InputMismatchException e) {
				output.ShowInvalidInputMessage();
			}
    return numInput;
  }
  int getKeyboardInput(){
    	numInput = in.nextInt();
			if (isValidSlot(numInput)) {
					System.out.println("Invalid input; re-enter slot number:");
			}
      return numInput;
  }
  bool isValidSlot(numInput){
    if (!(numInput > 0 && numInput <= 9)) 
      return false;
    else
      return true;
  }
}
class GameControl{
  void play(){
    while (winner == null) {
			winner = playTurn();
		}
		showGameResults(winner);
  }
  void showGameResults(winner)
  {
    if (winner.equalsIgnoreCase("draw")) {
			output.printDrawMessage();
		} else {
			output.printWinner(winner);
		}
  }
  String playTurn(){
      int numInput = input.getInput();
      if (!isSlotAvailable(numInput)) {
        output.printMessageSlotTakenAlready();
        return;
      }
			setSlot(numInput, turn);
			switchTurn();
			printBoard();
			return checkWinner();
  }
  void setSlot(numInput, turn){
    board[numInput-1] = turn;
  }
  bool isSlotAvailable(numInput){
    if (board[numInput-1].equals(String.valueOf(numInput)))
      return true;
    else
      return false;
  }
  void switchTurn(){
   if (turn.equals("X")) {
					turn = "O";
				} else {
					turn = "X";
				}
   }
}
class GameAlgorithm{
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

