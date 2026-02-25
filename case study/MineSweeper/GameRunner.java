import java.util.Scanner;

/* Author: Mortyyy
 * 
 * This is the class that actually runs the methods of 
 * the game. It calls the methods from the Game class
 * and contains the logic that keeps the game running 
 * while the player hasn't won or lost.
 * 
 */ 

public class GameRunner{
  
  //Displays rules at beginning of game.
  public static void init(){
    System.out.println("The object of the game is to clear the field of all safe tiles.");
    System.out.println("Play by entering the coordinates of a tile you'd like to select.");
    System.out.println("The range of the tiles is 1-10. The origin is the upper left tile.");
    System.out.println("There are 16 mines. Selecting a tile with a mine will end the game.");
  }
  
  public static void test(){  
    Game game = new Game();
    game.generateMines(16);
    game.update();
    Scanner scan = new Scanner(System.in); 
    
    int x, y;
    System.out.print("Enter an x coordinate.");
    x = scan.nextInt();
    System.out.print("Enter a y coordinate.");
    y = scan.nextInt();
    
    /* 
     * To ensure that the player does not lose on their first move,
     * the game will move a mine to another tile if the player
     * happens to select a tile with a mine present.
     */ 
    if(game.getTile(x,y).equals(" * ") == true){
      game.generateMines(1);
      game.field[x][y] = " ? ";
    }
    
    game.clear(x,y);
    game.detect();
    game.update();
    
    //After first move, loops until the game ends.
    while(true){
      if(game.getDone() == true && game.getWin() == true){    //If the player wins.
        System.out.println("You win!");
        game.onEnd();
        break;
      }else if(game.getDone() == true){                       //If the player loses.
        game.onEnd(); 
        break;
      }else if(game.getDone() == false){                      //While the player hasn't lost or won.
        x = -1;
        y = -1;     //Resets values.
        System.out.print("Enter an x coordinate.");
        y = scan.nextInt();
        System.out.print("Enter a y coordinate.");
        x = scan.nextInt();
        game.turn(x,y);
        game.isVictory();
        game.detect();
        game.update();
      }
      
    }   
  }
}
