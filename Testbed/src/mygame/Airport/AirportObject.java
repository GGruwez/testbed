package mygame.Airport;

import com.jme3.scene.Geometry;

public abstract class AirportObject extends Geometry {

    private int ID;
    private int W;
    private int L;

    public AirportObject(int ID, int W, int L){
        this.ID = ID;
        this.W = W;
        this.L = L;
    }

    public abstract int getSurface();

    public int getID(){
        return this.ID;
    }

    public int getW(){
        return this.W;
    }

    public int getL(){
        return this.L;
    }

}
