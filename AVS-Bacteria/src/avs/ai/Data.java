package avs.ai;
import avs.game.GameGrid;


/**
 * This class contains the results of a specific turn
 * grid contains the resulting grid
 * rating contains the rating of the grid
 * x and y contain the coordinates of the turned stone
 * 
 **/
public class Data {
    private GameGrid grid;
    int rating, x, y, turn;
    
    
    /**
     * 
     * Initializes a new {@link Data}.
     * @param grid The Grid
     * @param rating The Rating
     * @param x
     * @param y
     * @param turn
     */
    public Data(GameGrid grid, int rating, int x, int y, int turn){
        this.setGrid(grid.getCopy());
        this.rating = rating;
        this.turn = turn;
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + x;
        hash = hash * 13 + y;
        hash = hash * 113 + turn;
        return hash;
    }

    public GameGrid getGrid() {
        return grid;
    }

    public void setGrid(GameGrid grid) {
        this.grid = grid;
    }
    
}
