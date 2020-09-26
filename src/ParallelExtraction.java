import java.util.concurrent.RecursiveAction;
import java.util.List;
/**
 * This class extracts basins from a 1D array of values and inserts their coordinates into a 1D array of Strings.
 * @author HTGTIM001
 */

public class ParallelExtraction extends RecursiveAction
{
    boolean[] gridBasinStatus;
    String[] basinArray;
    int rows;
    int columns;
    int hi;
    int lo;
    int colPosition;
    int rowPosition;
    static final int SEQUENTIAL_CUTOFF = 500;
    
    ParallelExtraction(boolean[] gridBasinStatus, int lo, int hi, int columns, int rows, int colPosition, int rowPosition, String[] basinArray)
    {
        this.gridBasinStatus = gridBasinStatus;
        this.lo = lo;
        this.hi = hi;
        this.columns  = columns;
        this.rows = rows;
        this.colPosition = colPosition;
        this.rowPosition = rowPosition;
        this.basinArray = basinArray;
    }

    /**
     * Uses much of the same code as the ParallelBasin class with regards to converting between 1D and 2D 
     * coordinates. To extract each basin a true value is looked for and when found the corresponding coordinates 
     * are added to the String array.
     */
    @Override
    protected void compute() 
    {
        if((hi - lo)<SEQUENTIAL_CUTOFF)
        {
            int columnCount = colPosition;
            int rowCount = rowPosition;
            for(int i = lo; i<hi; i++)
            {
                if (columnCount +1 == columns)
                {
                    if(gridBasinStatus[(rowCount*columns)+columnCount] == true)
                    {
                        basinArray[i] = String.join(" ", Integer.toString(rowCount), Integer.toString(columnCount));
                    }
                    columnCount = 0;
                    rowCount += 1;
                }
                else
                {
                    if(gridBasinStatus[(rowCount*columns)+columnCount] == true)
                    {
                        basinArray[i] = String.join(" ", Integer.toString(rowCount), Integer.toString(columnCount));
                    }
                    columnCount += 1;
                }
            }
        }
        else
        {
            ParallelExtraction left = new ParallelExtraction(gridBasinStatus, lo, (hi+lo)/2, columns, rows, colPosition, rowPosition, basinArray);
            ParallelExtraction right = new ParallelExtraction(gridBasinStatus, (hi+lo)/2, hi, columns, rows, ((hi+lo)/2)%columns, ((hi+lo)/2)/columns, basinArray);
            left.fork();
            right.compute();
            left.join();
        }
    }
}