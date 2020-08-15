import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;


public class FindBasin
{
    static int columns;
    static int rows;
    static HashMap<String, Float> grid;
    static final ForkJoinPool fjpool = new ForkJoinPool();
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        FileReader file = new FileReader("small_in.txt");   //text file location hardcoded for now will come up with a better implementation
        BufferedReader br = new BufferedReader(file);               //this code creates a hashmap to store points as a string (key) and an associated float height (value)
        String firstLine = br.readLine();
        rows = Integer.parseInt(firstLine.split(" ")[0]);
        columns = Integer.parseInt(firstLine.split(" ")[1]);
        grid = createGrid(br);
        //List<String> basinList = sequentialBasinFinder();
        int arrayIndex = 0;
        float array[] = new float[(rows-1)*(columns-1)];
        
        for(int i = 1; i<(rows-1); i++)
        {
            for(int n=1; n<(columns-1); n++)
            {
                array[arrayIndex] = grid.get(String.join(" ",Integer.toString(i), Integer.toString(n)));
                arrayIndex += arrayIndex;
            }
        }
        List<String> basinList = parallelBasinFinder(array);
        
        System.out.println(basinList.size());
        
        for(String basin : basinList)
        {
          System.out.println(basin);
        } 
    }
   
    private static HashMap createGrid(BufferedReader br) throws IOException
    {
        HashMap<String, Float> grid = new HashMap<String, Float>();
        String[] values = br.readLine().split(" ");
        int columnCount = 0;
        int rowCount = 0;
        for (int entry = 0; entry<(rows*columns); entry++)
        {
            if(columnCount+1  == columns)
            {
                String coords = String.join(" ", Integer.toString(rowCount), Integer.toString(columnCount));
                grid.put(coords, Float.parseFloat(values[entry]));
                columnCount = 0;
                rowCount += 1;
            }
            else
            {
                String coords = String.join(" ", Integer.toString(rowCount), Integer.toString(columnCount));
                grid.put(coords, Float.parseFloat(values[entry]));
                columnCount += 1;
            }
        }
        br.close();
        return grid;
    }

    private static List sequentialBasinFinder()
    {
        List<String> basinList = new ArrayList<String>();
        for(int i = 1; i<(rows-1); i++)
        {
            for(int n = 1; n<(columns-1); n++)
            {
                if(grid.get(String.join(" ",Integer.toString(i-1),Integer.toString(n-1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01 
                    && grid.get(String.join(" ",Integer.toString(i-1),Integer.toString(n))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01 
                    && grid.get(String.join(" ",Integer.toString(i-1),Integer.toString(n+1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01  )
                {
                    if(grid.get(String.join(" ",Integer.toString(i),Integer.toString(n-1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) >0.01
                        && grid.get(String.join(" ",Integer.toString(i),Integer.toString(n+1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01)
                        {
                            if(grid.get(String.join(" ",Integer.toString(i+1),Integer.toString(n-1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01
                                && grid.get(String.join(" ",Integer.toString(i+1),Integer.toString(n))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01
                                && grid.get(String.join(" ",Integer.toString(i+1),Integer.toString(n+1))) - grid.get(String.join(" ",Integer.toString(i), Integer.toString(n))) > 0.01)
                            {
                                String basinCoords = String.join(" ", Integer.toString(i), Integer.toString(n));
                                basinList.add(basinCoords);
                            }
                        }                   

                }
            }
        }
        return basinList;
    }

    public static HashMap getGrid()
    {
        return grid;
    }

    public static List<String> parallelBasinFinder(float[] array)
    {
        System.out.println("we are lamming");
        return fjpool.invoke(new ParallelBasin(array, 0, array.length, columns, rows, grid));
    }

}
