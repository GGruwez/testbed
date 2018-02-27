package mygame.Airport;

public class Gate extends AirportObject{

    public Gate(int ID, int W){
        super(ID, W, W);
    }

    @Override
    public int getSurface(){
        return this.getW()*this.getW();
    }

}
