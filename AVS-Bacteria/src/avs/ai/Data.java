package avs.ai;
import avs.game.GameGrid;


/**
 * This class contains the results of a specific turn
 * grid contains the resulting grid
 * rating contains the rating of the grid
 * x and y contain the coordinates of the turned stone
 * 
 * TODO: HASH
 **/
public class Data {
    GameGrid grid;
    int rating, x, y, turn;
    
    public Data(GameGrid grid, int rating, int x, int y, int turn){
        grid = this.grid;
        rating = this.rating;
        x = this.x;
        y = this.y;
    }
}
