
package mygame;

import interfaces.Job;

public class Package {
    
    
    private Airport airportFrom;
    private int gateFrom;
    private Airport airportTo;
    private int gateTo;
    private float x;
    private float y;
    private float z;
    private boolean pickedUp;
    
    public Package(Airport airportFrom, int gateFrom, Airport airportTo, int gateTo) {
       this.airportFrom = airportFrom;
       this.gateFrom = gateFrom;
       this.airportTo = airportTo;
       this.gateTo = gateTo;
       this.pickedUp = false;
       this.x = getStartPosition().getX();
       this.y=0;
       this.z = getStartPosition().getZ();
    }
    
    public Vector getStartPosition() {
        float offsetX;
        if (gateFrom==0) offsetX = Airport.W/2;
        else offsetX = -Airport.W/2;
        return new Vector(airportFrom.getX() + offsetX, 0, airportFrom.getZ());
    }
    
    public Airport getAirportFrom() {return this.airportFrom;}
    public int getGateFrom() {return this.gateFrom;}
    public Airport getAirportTo() {return this.airportTo;}
    public int getGateTo() {return this.gateTo;}
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
    
    public void updatePosition(float x, float z) {
        this.x=x;
        this.z=z;
    }
    
    public boolean isPickedUp() {return this.pickedUp;}
    public void setPickedUp(boolean bool) {this.pickedUp = bool;}
    
}
