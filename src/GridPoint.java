public class GridPoint 
{
    private float height;
    private int row;
    private int column; 
    private boolean isBasin;
    
    public GridPoint(float height, int row, int column)
    {
        this.height = height;
        this.row = row;
        this.column = column;
        this.isBasin = false; //default value
    }

    public float getHeight()
    {
        return this.height;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.column;
    }

    public boolean isBasin()
    {
        return this.isBasin;
    }

    public void setBasinStatus(boolean isBasin)
    {
        this.isBasin = isBasin;
    }
}