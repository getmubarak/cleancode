/* This file should be able to implement a turn-by-version of the classic
   Snake game. We will complete this by printing the board to terminal each
   turn, then using a scanner from System.in to read user input.
   */
import java.util.*;
import java.lang.*;

public class Snake{
  char[][] board;
  int headCol;
  int headRow;
  int foodCol;
  int foodRow;
  int size;
  int boardHeight;
  int boardWidth;  /*Instanciating class variables*/
  ArrayList<ArrayList<Integer>> headPositions;
  boolean gameState;
  Random r;

  private final char SNAKE_HEAD = 'S';
  private final char SNAKE_BODY = 's';
  private final char SNAKE_FOOD = 'o';
  private final char EMPTY_SPACE = ' ';
  private final char HORIZONTAL_BORDER = '|';
  private final char VERTICAL_BORDER = '-';

/*default Snake class constructor*/
  public Snake(){
    boardHeight = 10;
    boardWidth = 10;
    headCol = 0;
    headRow = 0;
    foodCol = 0;
    foodRow = 0;
    size = 1;
    gameState = true;
    board = new char[boardHeight][boardWidth];
    headPositions = new ArrayList<ArrayList<Integer>>();
    r = new Random();
  }


/*Overloading the constructors with a new constructor that allows us to
instead make a game of larger dimensions, a larger/smaller playing field*/
  public Snake(int boardHeight, int boardWidth){
    this();
    this.boardHeight = boardHeight;
    this.boardWidth = boardWidth;
    board = new char[boardHeight][boardWidth];
  }

/*Each "frame" of the game, this will be called to update the stored position
of the snake's head. Since the snake's body follows places of where the snake's
head was previously, we actually only need to record the snake's head position,
and the snake's body will follow along previous head positions*/
  public void prepareNewState(){
    ArrayList<Integer> thisHeadPos = new ArrayList<Integer>();
    thisHeadPos.add(new Integer(headCol));
    thisHeadPos.add(new Integer(headRow));
    headPositions.add(thisHeadPos);
    /*Because each head position is appended to a list of all head positions,
    this requires that the head positions are stored in a dynamic array, an
    arraylist. We need this to be a two dimensional array, as we need to store
    an arraylist of two dimensional coordinates, however we cannot make an
    arraylists of normal arrays, so we have to make the coordinates which are
    added to the arraylist an arraylist of its own, which is why thisHeadPos is
    stored in an arraylist*/
    board = new char[boardHeight][boardWidth];
    for(int i = 0; i < boardHeight ; i++){
      for(int j = 0; j < boardWidth ; j++){
        board[i][j] = EMPTY_SPACE;  /*Fill the new game board with empty values*/
      }
    }
    /*Create a new board for the next frame, this can be changed to add a new
    food position for the snake, and update the position of the snake*/
  }


/*This gets called at the very first step of the game, or during the game when
a piece of food is eaten, and this will create a new piece of snake food at a
random position in the board, but this snake food cannot be placed on the Snake
itself!*/
  public void newRandomFoodPos(){
    int foodPos = r.nextInt(boardWidth*boardHeight - size - 1);
    /*foodPos tells the game where the random food will be placed*/
    int counter = 0;
    for(int i = 0; i < boardHeight ; i++){
      for(int j = 0; j < boardWidth ; j++){
        if(board[i][j] == EMPTY_SPACE){
          if(counter == foodPos){
            /*We iterate through the board, and once we have iterated through
            the board as many times as our foodPos variable tells us to, we
            place a new food at that position. Because we don't increase our
            counter for iterations when we're on non-empty positions, the food
            has no possibility of ever being placed on position where the snake
            already inhabits*/
            board[i][j] = SNAKE_FOOD;
            foodCol = j;
            foodRow = i; /*Store food position for when the board is updated*/
          }
          counter+=1;
          /*Counts how many empty positions we have iterated through*/
        }
      }
    }
  }

/*This is called every frame. First, we calculate where the input direction we
tell the snake to go is a non-losing move. If we don't lose on this step, then
we can safely check if food has been eaten, and update our board with the new
snake position and potentially new food positions as well*/
  public void move(char d){
    boolean problemFound = false;  /*Keeps track of if we've made a losing move*/
    /*Each of these if conditions check whether the move will move the snake into
    any of the board walls or into its own body. If so, we record that a problem
    has been found in the problemFound variable. If not, we update the snake's
    head position, and later on, the rest of the body follows suit.*/
    if(d == 'w'){  /*Moving up*/
      if(headRow-1>=0 && board[headRow-1][headCol] != SNAKE_BODY){
        headRow -= 1;
      }
      else{
        problemFound = true;
      }
    }
    else if(d == 's'){  /*Moving down*/
      if(headRow+1<boardHeight && board[headRow+1][headCol] != SNAKE_BODY){
        headRow += 1;
      }
      else{
        problemFound = true;
      }
    }
    else if(d == 'a'){  /*Moving left*/
      if(headCol-1>=0 && board[headRow][headCol-1] != SNAKE_BODY){
        headCol -= 1;
      }
      else{
        problemFound = true;
      }
    }
    else if(d == 'd'){  /*Moving right*/
      if(headCol+1<boardWidth && board[headRow][headCol+1] != SNAKE_BODY){
        headCol += 1;
      }
      else{
        problemFound = true;
      }
    }
    else if(d == 'q'){  /*Quit button*/
      problemFound = true;
    }
    if(problemFound){
      gameState = false;
      gameOver();  /*If we made a losing move, this ends the game*/
      return;
    }
    boolean foodFound = false;
    /*This records if food has been found! If the Snake head moves into an empty
    space of the board, obviously food has not been found. In this case, we also
    know that the size of the snake doesn't increase, so the previous tail
    position of the snake, stored as the first index of the headPositions
    arraylist, can be removed because this doesn't store any important
    information about the Snake's position any longer*/
    if(board[headRow][headCol] == EMPTY_SPACE){
      headPositions.remove(0);
    }
    /*If we do encounter food, the size of the snake gets larger, and we keep
    the previous tail position of the snake because this is where the extra
    body part gets added on during this game turn.*/
    else{
      foodFound = true;
      size += 1;
    }
    /*This clears our current game board so we can update everything, and also
    stores the new head position in our headPositions arraylist*/
    prepareNewState();
    for(int i = 0; i < size; i++){
      /*Every snake body part still existing in the current turn is stored in
      the headPositions arraylist, so we can make every coordinate in this
      a snake body part in the board.*/
      board[headPositions.get(i).get(1)][headPositions.get(i).get(0)] = SNAKE_BODY;
    }
    /*The most recently appended coordinate in the headPositions arraylist is
    the actual head of the snake, so we make this final coordinate a snake head
    body part in our board.*/
    board[headPositions.get(size-1).get(1)][headPositions.get(size-1).get(0)] = SNAKE_HEAD;
    if(!(foodFound)){
      board[foodRow][foodCol] = SNAKE_FOOD;
      /*The board is cleared each turn with the prepareNewState() function, so
      we keep track of our food position, and if the food isn't eaten by the end
      of a turn, we put the food back in that same position.*/
    }
    /*If food is found, we put a new piece of food in a random position!*/
    else{
      newRandomFoodPos();
    }
  }

/*Call before the game starts. Will set the board up, get things ready!*/
  public void initializeBoard(){
    prepareNewState();
    newRandomFoodPos();
    board[headRow][headCol] = SNAKE_HEAD; /*Let user know where the snake is*/
  }

  public void gameOver(){ /*Game over message to player*/
    System.out.println("YOU LOST!");
    System.out.println("Better luck next time, Bucko");
  }

/*This prints the board out to the console every frame*/
  public String toString(){
    /*This variable takes into account how much more narrow the "-" character
    is when compared to other characters. This is because the top and bottom
    of the game board is bounded by boundaries formed by the "-" character, so
    we want these characters to span the entire width of the board. To do so,
    I found that the rest of the board is a bit wider, so I add some extra
    hyphens to span this entire baord.*/
    int boardWidthRatio = boardWidth + (boardWidth/5);
    for(int i = 0; i < boardWidthRatio; i++){
      System.out.print(VERTICAL_BORDER);
      System.out.print(EMPTY_SPACE);
    }
    System.out.println();
    /*Print the rest of the board as expected, we add some empty spaces inside
    the board, as without these spaces, the board looks misshapen*/
    for(int i = 0; i < boardHeight ; i++){
      System.out.print(HORIZONTAL_BORDER);
      for(int j = 0; j < boardWidth ; j++){
        System.out.print(EMPTY_SPACE);
        System.out.print(board[i][j]);
      }
      System.out.print(EMPTY_SPACE);
      System.out.println(HORIZONTAL_BORDER);
    }
    /*Printing the lower game border*/
    for(int i = 0 ; i < boardWidthRatio; i++){
      System.out.print(VERTICAL_BORDER);
      System.out.print(EMPTY_SPACE);
    }
    System.out.println();
    return ""; /*Returning a string, which is required by .toString() methods*/
  }


/*If we run this file on its own, we will be able to see the game frame by frame,
taking our time! This main argument will wait for user input each frame.*/
  public static void main(String[] args){
    System.out.println("Welcome to Snake: Use the WASD keys to move, and 'q' to quit. Have fun!");
    /*Scanner looks for console input*/
    Scanner s = new Scanner(System.in);
    Snake snake = new Snake();
    snake.initializeBoard();
    while(snake.gameState){ /*Runs until game ends*/
      System.out.println(snake); /*Print the game board*/
      System.out.println("Enter a direction!");
      char input;
      if(s.hasNext())
        input = s.nextLine().charAt(0); /*Take the first character of console
        input as the direction or move to make*/
      else{
        continue;
      }
      if(input == 'w' || input == 'a' || input == 's' || input == 'd'
      || input == 'q'){
        snake.move(input); /*Make the move!*/
      }
      else{
        System.out.println("If you want to quit, enter 'q'. Otherwise, use the WASD keys to move.");
      }
    }
  }
}
