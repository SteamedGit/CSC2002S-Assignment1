import java.util.concurrent.RecursiveAction;
/***
 * This class classifies basins and nonbasins in a grid of height values from a 2D array of floats and 
 * stores their basin status in a 1D boolean array.
 * @author HTGTIM001
 */

public class ParallelBasin extends RecursiveAction
{
    int colPosition; 
    int rowPosition;
    int columns;
    int rows;
    int lo;
    int hi;
    boolean[] array;
    static int SEQUENTIAL_CUTOFF = 500;
    float[][] grid;

    ParallelBasin(boolean[] array, int lo, int hi, int columns, int rows, int colPosition, int rowPosition, float[][] grid)
    {
        this.array = array;
        this.lo = lo;
        this.hi = hi;
        this.columns = columns;
        this.rows = rows;
        this.colPosition = colPosition;
        this.rowPosition = rowPosition;
        this.grid = grid;
    }

    /**
     * Used to set the sequential cutoff from inside another class.
     * @param cutoff
     */
    public static void setSequentialCutoff(int cutoff)
    {
        SEQUENTIAL_CUTOFF = cutoff;
    }

    /**
     * Classifies basins and nonbasins in the 2D grid and stores the classification in a 1D array.
     * All gridpoints on the edges of the terrain are not considered. The relative positions around 
     * a gridpoint are constant, thus from the 2D coordinates of the gridpoint we can access all of its 
     * neighbours. If all of the neighbours have a height values greater by 0.01m or more, then the gridpoint 
     * is classified as a basin.
     */
    @Override
    protected void compute() 
    {
        if((hi-lo)< SEQUENTIAL_CUTOFF)
        {
            int columnCount = colPosition;
            int rowCount = rowPosition;
            for(int i = lo; i<hi; i++)
            {
                if (columnCount +1 == columns)
                {
                    if(!(rowCount==0 || columnCount == 0 || rowCount==(rows-1) || columnCount==(columns-1)))
                    {
                        if(grid[rowCount-1][columnCount-1] - grid[rowCount][columnCount]>= 0.01 && grid[rowCount-1][columnCount] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount-1][columnCount+1] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount][columnCount-1] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount][columnCount+1] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount+1][columnCount-1] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount+1][columnCount] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount+1][columnCount+1] - grid[rowCount][columnCount] >= 0.01 )
                        {
                            array[i] = true;
                        }
                    }
                    columnCount = 0;
                    rowCount += 1;
                }
                else
                {
                    if(!(rowCount==0 || columnCount == 0 || rowCount==(rows-1) || columnCount==(columns-1)))
                    {
                        if(grid[rowCount-1][columnCount-1] - grid[rowCount][columnCount]>= 0.01 && grid[rowCount-1][columnCount] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount-1][columnCount+1] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount][columnCount-1] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount][columnCount+1] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount+1][columnCount-1] - grid[rowCount][columnCount] >= 0.01 
                        && grid[rowCount+1][columnCount] - grid[rowCount][columnCount] >= 0.01 && grid[rowCount+1][columnCount+1] - grid[rowCount][columnCount] >= 0.01 )
                        {
                            array[i] = true;
                        }
                    }
                    columnCount += 1;
                }
            }
        }
        else
        {
            ParallelBasin left = new ParallelBasin(array, lo, (hi+lo)/2, columns, rows, colPosition, rowPosition, grid);
            ParallelBasin right = new ParallelBasin(array, (hi+lo)/2, hi, columns, rows, ((hi+lo)/2)%columns, ((hi+lo)/2)/columns, grid);
            left.fork();
            right.compute();
            left.join();
        }
    
    }
}