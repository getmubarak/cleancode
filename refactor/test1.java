class Controller{
	IBoard board;
	IAlgorithm algorithm;
	IInput input;
	IOutput output;
	String turn ="X";
  
	public Controller(IBoard board,
	IAlgorithm algorithm,
	IInput input,
	IOutput output){
		this.board = board;
		this.algorithm = algorithm;
		this.input = input;
		this.output = output;
  }    	
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
