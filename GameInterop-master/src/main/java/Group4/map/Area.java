package Group4.map;

public class Area {
    protected int leftBoundary;
    protected int rightBoundary;
    protected int topBoundary;
    protected int bottomBoundary;

    public Area(){
        leftBoundary=0;
        rightBoundary=1;
        topBoundary=0;
        bottomBoundary=1;
    }

    public Area(int x1, int y1, int x2, int y2){
        leftBoundary=Math.min(x1,x2);
        rightBoundary=Math.max(x1,x2);
        topBoundary=Math.max(y1,y2);
        bottomBoundary=Math.min(y1,y2);
    }



    /*
        Check whether a point is in the target area
    */
    public boolean isHit(double x,double y){
        return (y>bottomBoundary+2)&(y<topBoundary-2)&(x>leftBoundary-2)&(x<rightBoundary+2);
    }

    /*
        Check whether something with a radius is in the target area
        STILL TO BE IMPLEMENTED
    */
    public boolean isHit(double x,double y,double radius){
        return false;
    }
}
