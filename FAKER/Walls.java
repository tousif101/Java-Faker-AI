package Players.FAKER;

import Interface.Coordinate;

/**
 * Created by tousifchowdhury on 4/26/15.
 */
public class Walls {
    private Coordinate start;
    private Coordinate end;

    public Walls(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    public Coordinate getStart() {
        return this.start;}

    public Coordinate getEnd(){
        return this.end;
    }

    public Coordinate getMidPoint(){

        int r = ((start.getRow()+end.getRow())/2);
        int c = ((start.getCol()+end.getCol())/2);



        return new Coordinate(r,c);
    }



    public boolean overlap( Walls wall) {

        if (this.getMidPoint().equals(wall.getEnd()) && wall.getMidPoint().equals(this.getStart())) {
            return true;
        }


        if (this.getMidPoint().equals(wall.getStart()) && wall.getMidPoint().equals(this.getEnd())) {
            return true;
        }
        if(wall.getMidPoint().equals(this.getMidPoint())){
            return true;
        }

        return false;
    }






}
