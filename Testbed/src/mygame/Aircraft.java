package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

public class Aircraft extends Geometry {
    
    private Vector coordinates;
    private Vector velocity;
    private Vector acceleration;
    private Force forces;
    private float tailmass;
    private float wingmass;
    private float enginemass;
    private float pitch;
    private float roll;
    private float heading;
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param xVelocity
     * @param yVelocity
     * @param zVelocity
     * @param mass
     * @param thrust
     * @param leftWingInclination
     * @param rightWingInclination
     * @param horStabInclination
     * @param verStabInclination
     */	
    public Aircraft(String name, Mesh mesh, float x, float y, float z, float xVelocity, float yVelocity, float zVelocity,
            float mass, float thrust, float leftWingInclination,float rightWingInclination,
            float horStabInclination, float verStabInclination) {
        super(name, mesh);
    
    }

    public Vector getCoordinates(){
        return this.coordinates;
    }	
    
    public void setCoordinates(Vector coordinates){
    	this.coordinates = coordinates;
    }
    
    public Vector getVelocity() {
        return this.velocity;
    }
    
    public void setVelocity(Vector velocity){
    	this.velocity = velocity;
    	
    }
    
    public Vector getAcceleration(){
    	return this.acceleration;
    }
    
    public void setAcceleration(Vector acceleration){
    	this.acceleration = acceleration;
    }
    
    public Force getForce(){
    	return this.forces;
    }
    
    public float getTotalMass(){
    	return this.enginemass+this.wingmass*2+ this.tailmass;
    }
    
    public void updateAirplane(double time){
    	setCoordinates(getCoordinates().add(getVelocity().constantProduct(time)));
    	setVelocity(getVelocity().add(getAcceleration()).constantProduct(time));
    	setAcceleration(getAcceleration().add(getForce().getTotalForce().transform(heading, pitch, roll).constantProduct(1/getTotalMass())));
    	
    	setPitch();
    	setRoll();
    	setHeading();
    	setAngularVelocity();
    	setAngurlarAcceleration();
    }
}

