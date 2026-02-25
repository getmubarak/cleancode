/* Author: Mortyyy
 * 
 * This class contains the logic and methods that allow
 * the Mine Sweeper game to run properly. Two 12x12 arrays
 * are created when the program is run. The arrays are
 * filled with question marks and one is given mines.
 * This is so that the computer is able to view the array
 * with mines and track the player's progress, while the 
 * player is shown the array with question marks only; all
 * the tiles appear identical to the player so they are
 * unaware of where the mines are.
 * 
 * Methods in this class update the console, determine if
 * the game is done, if the game has been won, get the value 
 * of a tile, print the arrays, and the amount of mines 
 * around a tile.
 * 
 */

public class Game{
  public String[][] field = new String[12][12];   //12 rows, 12 columns.
  public String[][] display = new String[12][12]; //This is the field that is visible to the player.
  public Boolean isDone = false;
  public Boolean isWin = false;
  
  private String unknown = " ? ";
  private String mine = " * ";
  private String empty = "   ";
  
  //Constructor places empty spaces in tiles.
  public Game(){
    int row = 0;
    int column = 0;
    
    for(int x = 0; x < field.length; x++){
      for(int y = 0; y < field[0].length; y++){
        //Places blank spaces in buffer zones.
        if((x == 0 || x == field.length - 1)||(y == 0 || y == field[0].length - 1)){
          field[x][y] = empty;
          display[x][y] = empty;
        }
        //Places ? in game field.
        else{
          field[x][y] = unknown;
          display[x][y] = unknown;
        }
      }
    }
  }
  
  //Displays the field.
  public static void printGame(String[][] str){
    for(int x = 1; x < str.length - 1; x++){   
      for(int y = 0; y < str[0].length ; y++){
        //Formats row.
        if(y > 0 && y < str[0].length)
          System.out.print("|");
        else
          System.out.println("");
        
        System.out.print(str[x][y]);  //Prints out content of each tile.
      }
    }
  }
  
  //Updates the console after every match.
  public void update(){
    printGame(display);
    System.out.println("");
  }
  
  //Places n mines at random on the field.
  public void generateMines(int n){
    for(int m = 0; m < n; m++){
      //Loops until a mine is placed.
      while(true){
        int x, y = 0;   //Clears vars.
        x = (int)(10*Math.random());
        y = (int)(10*Math.random());
        
        //So that a mine is placed in a tile visible to the player.
        if(x >= 1 && x <= 10){
          if(y >= 1 && y <= 10){
            //Checks if a mine is present in a spot.
            if(!field[x][y].equals(mine)){
              field[x][y] = mine;
              break;
            }
          }
        }
      }
    }
  }
  
  //On first move, this clears the area around the selected tile.
  public void clear(int x, int y){  
    for(int i = (x - 1); i <= (x + 1); i++){
      for(int j = (y - 1); j <= (y + 1); j++){
        if(field[i][j].equals(unknown) == true){
          display[i][j] = empty;
          field[i][j] = empty;
        }
      }
    }
  }
  
  //Gets the value of a tile.
  public String getTile(int x, int y){
    return field[x][y];
  }
  
  //Detects number of mines around a selected tile.
  public void detect(){
    for(int x = 1; x < display.length - 2; x++){     //Cycles thru the entire visible display.
      for(int y = 1; y < display.length - 2; y++){
        if(field[x][y].equals(empty) == true){
          int nums = 0;                              //Var for counting mines.
          for(int i = (x - 1); i <= (x + 1); i++){
            for(int j = (y - 1); j <= (y + 1); j++){
              if(field[i][j].equals(mine) == true)
                nums++;                              //Incrememnts nums var when a mine is detected.
            }
          }
          display[x][y] = " " + nums + " ";
        }
      }
    }
  }
  
  //Takes user's selected coordinates and adjusts the board.
  public void turn(int x, int y){
    if(field[x][y].equals(unknown) == true){           //If the spot hasn't been selected, it is cleared.
      isDone = false;
      display[x][y] = empty;
      field[x][y] = empty;
    }else if(field[x][y].equals(mine) == true){        //If the user selects a mine.
      isDone = true;                                   //Game is done.
      isWin = false;                                   //User doesn't win.
      System.out.println("You've lost!");
    }else if(display[x][y].equals(empty) == true && field[x][y].equals(empty)){
      isDone = false;
      System.out.println("This tile's been cleared!");
    }
  }
  
  //Determines if a player has cleared all safe tiles.
  public void isVictory(){
    int tile = 0;                                      //Var for the number of uncleared tiles in the array.
    for(int i = 0; i < field.length; i++){
      for(int j = 0; j < field[0].length; j++){
        if(field[i][j].equals(unknown) == true)
          tile++;                                      //If there are uncleared tiles, var is incremented.
      }
    }
    if(tile != 0)
      isWin = false;  //If there are still uncleared tiles, player hasn't won.
    else{
      isWin = true;
      isDone = true;
    }
  }
  
  //Determines if the game is finished.
  public Boolean getDone(){
    return isDone;
  }
  
  //Determines if a player won.
  public Boolean getWin(){
    return isWin;
  }
  
  //Displays location of mines at end of game.
  public void onEnd(){
    printGame(field);
  }
  
}
