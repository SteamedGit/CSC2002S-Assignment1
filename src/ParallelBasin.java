import java.util.concurrent.RecursiveTask;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class ParallelBasin extends RecursiveTask<List>
{
    int columns;
    int rows;
    int lo;
    int hi;
    float[] array;
    static final int SEQUENTIAL_CUTOFF=500;
    List<String> basins = new ArrayList<String>();
    HashMap<String, Float> grid;

    ParallelBasin(float[] array, int lo, int hi, int columns, int rows, HashMap grid)
    {
        this.array = array;
        this.lo = lo;
        this.hi = hi;
        this.columns = columns;
        this.rows = rows;
        this.grid = grid;
    }

    protected List<String> compute()
    {
        System.out.println("we are lamming");
        if((hi-lo)<SEQUENTIAL_CUTOFF)
       {
            for(int i = lo; i<hi; i++)
            {
                int rowPos = i/(columns-3);
                int colPos = i%(columns-3);
                String key = String.join(" ",Integer.toString(rowPos),Integer.toString(colPos));
                
                if(grid.get(String.join(" ",Integer.toString(rowPos-1),Integer.toString(colPos-1))) - grid.get(key) > 0.01 
                    && grid.get(String.join(" ",Integer.toString(rowPos-1),Integer.toString(colPos))) - grid.get(key) > 0.01 
                    && grid.get(String.join(" ",Integer.toString(rowPos-1),Integer.toString(colPos+1))) - grid.get(key) > 0.01)
                {
                    if(grid.get(String.join(" ",Integer.toString(rowPos),Integer.toString(colPos-1))) - grid.get(key) > 0.01 
                        && grid.get(String.join(" ",Integer.toString(rowPos),Integer.toString(colPos+1))) - grid.get(key) > 0.01)
                    {
                        if(grid.get(String.join(" ",Integer.toString(rowPos+1),Integer.toString(colPos-1))) - grid.get(key) > 0.01 
                            && grid.get(String.join(" ",Integer.toString(rowPos+1),Integer.toString(colPos))) - grid.get(key) > 0.01 
                            && grid.get(String.join(" ",Integer.toString(rowPos+1),Integer.toString(colPos+1))) - grid.get(key) > 0.01)
                        {
                            basins.add(key);
                        }
                    }
                }


            }
            return basins;

       }
       else
       {
           ParallelBasin left = new ParallelBasin(array, lo, (hi+lo)/2, columns, rows, grid);
           ParallelBasin right = new ParallelBasin(array, (hi+lo)/2, hi, columns, rows, grid);
           
           left.fork();
           List<String> rightAns = right.compute();
           List<String> leftAns = left.join();
           
           List<String> combinedAns = new ArrayList<String>(rightAns);
           combinedAns.addAll(leftAns);
           return combinedAns;
       }
    }


}