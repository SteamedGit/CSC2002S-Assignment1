import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
/**
 * This class allows the user to use the ParallelBasin and ParallelExtraction classes to find and extract basins
 * from a 2D grid of height values. Additonally,it allows the user to benchmark the basin classification process. 
 * It also includes sequential methods that do the same thing for the purposes of comparison when benchmarking.
 * @author HTGTIM001
 */

public class FindBasin
{
    static int columns;
    static int rows;
    static float[][]grid;
    static String[] basinArray;
    static int basinArrayCounter;
    static final ForkJoinPool fjpoolBasin = new ForkJoinPool();
    static final ForkJoinPool fjpoolExtraction = new ForkJoinPool();
    /**
     * This main method manages user import and either finds and extracts basins or benchmarks finding basins.
     * The data file from which the grid is constructed is expected to have its dimensions on its first line and 
     * all of its values on its second line.
     * Various options are specificied through args, see the README for specifics.
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        
        if(args.length > 4 || args.length<2) 
        {
            System.out.println("Incorrect Input!");
        }
        else if(args.length == 4) 
        {
            BufferedReader br  = createReader(args[0]);
            BufferedWriter bw = createWriter(args[1]);             
            String firstLine = br.readLine();
            rows = Integer.parseInt(firstLine.split(" ")[0]);
            columns = Integer.parseInt(firstLine.split(" ")[1]);
            grid = createGrid(br);
            boolean gridBasinStatus[] = new boolean[rows*columns];
            basinArray = new String[(rows*columns)];
            basinArrayCounter = 0;
            
            for(int i = 0; i<gridBasinStatus.length; i++)
            {
                gridBasinStatus[i] = false;
            }

            if(args[2].equals("sFind"))
            {
                sequentialBasinFinder(gridBasinStatus);
            }
            else if(args[2].equals("pFind"))
            {
                parallelBasinFinder(gridBasinStatus);
            }

            if(args[3].equals("sExtract"))
            {
                sequentialBasinExtraction(gridBasinStatus);
            }
            else if(args[3].equals("pExtract"))
            {
                parallelBasinExtraction(gridBasinStatus);
            }
            basinWriter(bw);
        }

        else if(args.length == 3 || args.length == 2)
        {    
            int numIterations = 50;
            BufferedReader br = createReader(args[1]);            
            String firstLine = br.readLine();
            rows = Integer.parseInt(firstLine.split(" ")[0]);
            columns = Integer.parseInt(firstLine.split(" ")[1]);
            grid = createGrid(br);
            boolean gridBasinStatus[] = new boolean[rows*columns];
            basinArray = new String[(rows*columns)];
            basinArrayCounter = 0;
            
            for(int i = 0; i<gridBasinStatus.length; i++)
            {
                gridBasinStatus[i] = false;
            }
        
            if(args[0].equals("st"))
            {
                System.gc();
                long[] times =  new long[numIterations];
                for(int i = 0; i<numIterations; i++)
                {
                    long startTime = System.nanoTime();
                    sequentialBasinFinder(gridBasinStatus);
                    times[i] = (System.nanoTime() - startTime);
                }
                double total = 0;
                for(long time : times)
                {
                    System.out.println(((double)time)/1000000);
                    total += time;
                }
                timingResults(total, times, numIterations);
            }

            else if(args[0].equals("pt"))
            {
                System.gc();
                int cutoff = Integer.parseInt(args[2]);
                ParallelBasin.setSequentialCutoff(cutoff);
                long[] times =  new long[numIterations];
                for(int i = 0; i<numIterations; i++)
                {
                    long startTime = System.nanoTime();
                    parallelBasinFinder(gridBasinStatus);
                    times[i] = (System.nanoTime() - startTime);
                }
                double total = 0;
                for(long time : times)
                {
                    System.out.println(((double)time)/1000000);
                    total += time;
                }
                timingResults(total, times, numIterations);
            }

        }
    }

    /**
     * Creates a 2D grid of float values by reading them in from a bufferedreader.
     * The function expects all values to be stored on one line.
     * @param br
     * @return float[][]
     * @throws IOException
     */
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

    /**
     * Creates a BufferedReader from the specified filepath.
     * @param filePath
     * @return BufferedReader
     * @throws IOException
     */
    private static BufferedReader createReader(String filePath) throws IOException
    {
        FileReader file = new FileReader(filePath); 
        BufferedReader br =  new BufferedReader(file);
        return br;
    }

    /**
     * Crates a BufferedWriter from the specified filepath.
     * @param filePath
     * @return BufferedWriter
     * @throws IOException
     */
    private static BufferedWriter createWriter(String filePath) throws IOException
    {
        File fileOut = new File(filePath);
        fileOut.createNewFile();
        FileWriter fw = new FileWriter(fileOut);
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    /**
     * Sequentially finds basins in a 2D grid and stores that classification in a 1D array of boolean values.
     * @param gridBasinStatus
     * 
     */
    private static void sequentialBasinFinder(boolean gridBasinStatus[])
    {
        int columnCount = 0;
        int rowCount = 0;
        for (int i = 0; i<gridBasinStatus.length; i++)
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
                        gridBasinStatus[i] = true;
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
                        gridBasinStatus[i] = true;
                    }
                }
                columnCount += 1;
            }
        }
    } 
    /**
     * Invokes the Fork Join Pool for the ParallelBasin class.
     * @param array
     * 
     */
    private static void parallelBasinFinder(boolean array[])
    {
        fjpoolBasin.invoke(new ParallelBasin(array, 0, array.length, columns, rows, 0, 0, grid));
    }

    /**
     * Sequentially extracts basins from a 1D array of boolean values and stores their coordinates in an array
     * of strings.
     * @param gridBasinStatus
     * 
     */
    private static void sequentialBasinExtraction(boolean[] gridBasinStatus)
    {
        for(int i = 0; i<rows; i++)
        {
            for(int n = 0; n<columns; n++)
            {
               if(gridBasinStatus[(i*columns)+n] == true)
               {
                   basinArray[(i*columns)+n] = String.join(" ", Integer.toString(i), Integer.toString(n));
               }
            }
        }
    }
   
    /**
     * Invokes the Fork Join Pool for the ParallelExtraction class.
     * @param gridBasinStatus
     * 
     */
    private static void parallelBasinExtraction(boolean gridBasinStatus[])
    {
        fjpoolExtraction.invoke(new  ParallelExtraction(gridBasinStatus, 0, gridBasinStatus.length, columns, rows, 0, 0, basinArray));
    }

    /**
     * Writes the basins to a file using a BufferedWriter.
     * @param bw
     * @throws IOException
     * 
     */
    private static void basinWriter(BufferedWriter bw) throws  IOException
    {
        for(int i = 0; i<basinArray.length; i++)
        {
            if(basinArray[i] != null)
            {
                basinArrayCounter+=1;
            }
        }
        bw.write(Integer.toString(basinArrayCounter));
        bw.newLine();
        for(int i = 0; i<basinArray.length; i++)
        {
            if(basinArray[i] != null)
            {
                bw.write(basinArray[i]);
                bw.newLine();
            }
        }
        bw.close();
    }
    /**
     * Sorts an array of times and calculates its mean, min and max.
     * @param total
     * @param times
     * @param numIterations
     * 
     */
    private static void timingResults(double total, long[] times, int numIterations)
    {
        Arrays.sort(times);
        double minTime = (((double)times[0])/1000000);
        double maxTime = (((double)times[numIterations-1])/1000000);
        System.out.println("Min in ms: "  + Double.toString(minTime));
        System.out.println("Max in ms: "  + Double.toString(maxTime));
        System.out.println("Average in ms: " + Double.toString(total/(numIterations*1000000)));
    }
}
