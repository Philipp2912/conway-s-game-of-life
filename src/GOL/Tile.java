package GOL;

public class Tile {
    int neighbours = 0;
    //0=not 1=will be
    int status = 0;
    //0=was not 1= was
    int cellStatus = 0;
    boolean occupied = false;
    int x;
    int y;
    public Tile(int x, int y)
    {
        this.x=x;
        this.y=y;
    }
    public void determineAction(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
        //counts the amount of living tiles around it and determines its own status based on it
            if (a)
                neighbours = neighbours + 1;
            if (b)
                neighbours = neighbours + 1;
            if (c)
                neighbours = neighbours + 1;
            if (d)
                neighbours = neighbours + 1;
            if (e)
                neighbours = neighbours + 1;
            if (f)
                neighbours = neighbours + 1;
            if (g)
                neighbours = neighbours + 1;
            if (h)
                neighbours = neighbours + 1;

            status=(occupied&&neighbours == 2 || neighbours == 3?1:0);
            cellStatus=(neighbours==3?1:cellStatus);
        neighbours = 0;
    }

    public  int getX()
    {
        return x;
    }
    public int getY() {
        return y;
    }

        public void performAction ()
    {
        if (status==1)
            occupied=true;
        else
            occupied=false;
    }
    public void determineAction(boolean setOccupied)
    {
        if (setOccupied)
            status=1;
        else
            status=0;
    }
}