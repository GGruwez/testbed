package mygame.Airport;

public class Airport extends AirportObject{

    private Runway runway0;
    private Gate gate0;
    private Gate gate1;
    private Runway runway1;

    public Airport(int ID, int W, int L){
        super(ID, W, L);
        this.runway0 = new Runway(0, W, L);
        this.gate0 = new Gate(0, W);
        this.gate1 = new Gate(1, W);
        this.runway1 = new Runway(1, W, L);
    }

    @Override
    public int getSurface(){
        return (2*this.getL()+this.getW())*2*this.getW();
    }

}
