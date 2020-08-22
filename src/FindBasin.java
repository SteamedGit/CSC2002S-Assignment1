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


public class FindBasin
{
    static int columns;
    static int rows;
    static float[][]grid;
    static List<String> basinList;
    static final ForkJoinPool fjpoolBasin = new ForkJoinPool();
    static final ForkJoinPool fjpoolExtraction = new ForkJoinPool();
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        
        if(args.length > 3 || args.length<2) // will change to 2 for output file
        {
            System.out.println("Incorrect Input!");
        }
        else if(args.length == 2) // will change to 2 for output file
        {
            FileReader file = new FileReader(args[0]);   
            File fileOut = new File(args[1]);
            fileOut.createNewFile();
            FileWriter fw = new FileWriter(fileOut);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedReader br = new BufferedReader(file);               
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
           
            long startTime = System.currentTimeMillis();
            parallelBasinFinder(gridBasinStatus);
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(elapsedTime);
            //sequentialBasinExtraction(gridBasinStatus);
            parallelBasinExtraction(gridBasinStatus);
            basinWriter(bw);
        }

        else if(args.length == 3)
        {
            FileReader file = new FileReader(args[1]);   
            int numIterations = Integer.parseInt(args[2]);
            BufferedReader br = new BufferedReader(file);               
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
        fjpoolBasin.invoke(new ParallelBasin(array, 0, array.length, columns, rows, 0, 0, grid));
    }

    private static void sequentialBasinExtraction(boolean[] gridBasinStatus)
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
    }
   
    private static void parallelBasinExtraction(boolean gridBasinStatus[])
    {
        fjpoolExtraction.invoke(new  ParallelExtraction(gridBasinStatus, 0, gridBasinStatus.length, columns, rows, 0, 0, basinList));
    }

    private static void basinWriter(BufferedWriter bw) throws  IOException
    {
        bw.write(Integer.toString(basinList.size()));
        bw.newLine();
        for(String basin : basinList)
        {
            bw.write(basin);
            bw.newLine();
        }
        bw.close();

    }

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
