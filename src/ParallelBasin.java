import java.util.concurrent.RecursiveAction;


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

    public static void setSequentialCutoff(int cutoff)
    {
        SEQUENTIAL_CUTOFF = cutoff;
    }

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