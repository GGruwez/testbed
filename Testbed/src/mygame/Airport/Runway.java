package mygame.Airport;

public class Runway extends AirportObject{

    public Runway(int ID, int W, int L) {
        super(ID, W, L);
    }

    @Override
    public int getSurface(){
        return this.getL()*2*this.getW();
    }

}
