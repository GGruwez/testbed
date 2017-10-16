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
	private Vector angularAcceleration;
	private Vector angularVelocity;
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
    
    public float getPitch(){
    	return this.pitch;
    }
    
    public void setPitch(float pitch){
    	this.pitch = pitch;
    }
    
    public float getRoll(){
    	return this.roll;
    }
    
    public void setRoll(float roll){
    	this.roll = roll;
    }
    
    public float getHeading(){
    	return this.heading;
    }
    
    public void setHeading(float heading){
    	this.heading = heading;
    }
    
    public Vector getAngularVelocity(){
    	return this.angularVelocity;
    }
    
    public void setAngularVelocity(Vector aVelocity){
    	this.angularVelocity = aVelocity;
    }
    
    public Vector getAngularAcceleration(){
    	return this.angularAcceleration;
    }
    
    public void setAngularAcceleration(Vector aAcceleration){
    	this.angularAcceleration = aAcceleration;
    }
    
    
    public void updateAirplane(float time){
    	setCoordinates(getCoordinates().add(getVelocity().constantProduct(time)));
    	setVelocity(getVelocity().add(getAcceleration().constantProduct(time)));
    	setAcceleration(getAcceleration().add(getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll()).constantProduct(1/getTotalMass())));
    	
    	setPitch(getPitch() + getAngularVelocity().getX());
    	setRoll(getRoll() + getAngularVelocity().getZ());
    	setHeading(getHeading() + getAngularVelocity().getY());
    	setAngularVelocity(getAngularVelocity().add(getAngularAcceleration().constantProduct(time)));
    	setAngularAcceleration(getAngularAcceleration().add(getForce().getTotalMoment().transform(heading,pitch,roll).applyTraagheidsmatrix()));
    }
}

