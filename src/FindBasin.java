import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;


public class FindBasin
{
    static int columns;
    static int rows;
    static float[][]grid;
    static List<String> basinList;
    static final ForkJoinPool fjpool = new ForkJoinPool();
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        FileReader file = new FileReader("small_in.txt");   //text file location hardcoded for now will come up with a better implementation
        BufferedReader br = new BufferedReader(file);               //this code creates a hashmap to store points as a string (key) and an associated float height (value)
        String firstLine = br.readLine();
        rows = Integer.parseInt(firstLine.split(" ")[0]);
        columns = Integer.parseInt(firstLine.split(" ")[1]);
        grid = createGrid(br);
        boolean gridBasinStatus[] = new boolean[rows*columns];
        basinList = new ArrayList<String>();
        
        for(int i = 0; i<gridBasinStatus.length; i++)
        {
            gridBasinStatus[i] = false;
        }
        //sequentialBasinFinder();
        parallelBasinFinder(gridBasinStatus);
        basinExtraction(gridBasinStatus);


    }
    private static float[][] createGrid(BufferedReader br) throws IOException
    {
        float[][] grid = new float[rows][columns];
        String[] values = br.readLine().split(" ");
        int columnCount = 0;
        int rowCount = 0;
        for(String value : values)
        {
            if(columnCount + 1 == columns)
            {
                grid[rowCount][columnCount] = Float.parseFloat(value);
                columnCount= 0;
                rowCount += 1;
            }
            else
            {
                grid[rowCount][columnCount] = Float.parseFloat(value);
                columnCount += 1;
            }
        }
        br.close();
        return grid;
    }

    private static void sequentialBasinFinder(boolean gridBasinStatus[])
    {
        for(int i = 0; i<(rows); i++)
        {
            for(int n = 0; n<(columns); n++)
            {
                if(!(i==0 || n == 0 || i==(rows-1) || n==(columns-1)))
                {
                    if(grid[i-1][n-1] - grid[i][n]>= 0.01 && grid[i-1][n] - grid[i][n] >= 0.01 
                    && grid[i-1][n+1] - grid[i][n] >= 0.01 && grid[i][n-1] - grid[i][n] >= 0.01 
                    && grid[i][n+1] - grid[i][n] >= 0.01 && grid[i+1][n-1] - grid[i][n] >= 0.01 
                    && grid[i+1][n] - grid[i][n] >= 0.01 && grid[i+1][n+1] - grid[i][n] >= 0.01 )
                    {
                        gridBasinStatus[((i*columns)+n)] =  true;
                    }
                
                    
                }
            }
        }
    } 

    private static void parallelBasinFinder(boolean array[])
    {
        fjpool.invoke(new ParallelBasin(array, 0, array.length, columns, rows, 0, 0, grid));
    }

    private static void basinExtraction(boolean[] gridBasinStatus)
    {
        for(int i = 0; i<rows; i++)
        {
            for(int n = 0; n<columns; n++)
            {
               if(gridBasinStatus[(i*columns)+n] == true)
               {
                   basinList.add(String.join(" ", Integer.toString(i), Integer.toString(n)));
               }
            }
        }

        System.out.println(basinList.size());
        for(String basin : basinList)
        {
            System.out.println(basin);
        }

    }
}
