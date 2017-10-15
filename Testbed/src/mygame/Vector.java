package mygame;

import java.math.*;

public class Vector {

	private final double x;
	private final double y;
	private final double z;

	public Vector(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
		
	}
	
	public double getZ(){
		return this.z;
	}


	public Vector add(Vector other){
		return new Vector(this.getX()+ other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
	}
	
	public Vector subtract(Vector other){
		return new Vector(this.getX()- other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());

	}
	
	public double euclideanLength(){
		return Math.sqrt(this.dotProduct(this));
	}
	
	/**
	 * returnt de hoek tussen twee vectoren in radialen!
	 */
	public double angleBetween(Vector other){
		return Math.acos(this.dotProduct(other)/(this.euclideanLength()*other.euclideanLength()));
	}
	
	
	// transformatiematrixen, daar kan ik echt nie mee werken
	// ik kijk daar wel eens naar als ik mn boek van mechanica 3 vind, staat daar allemaal in
	public Vector transform(double heading, double pitch, double roll ){
		
		return new Vector(0,0,0);
	}
	
	/**
	 * A.B
	 * @param other
	 * @return
	 */
	public double dotProduct(Vector other){
		return this.x*other.x + this.y*other.y + this.z*other.z;
	}
	
	/**
	 * AxB
	 * @param other
	 * @return
	 */
	public Vector crossProduct(Vector other){
		return new Vector(this.y*other.z - other.y*this.z,
				-this.x*other.z+other.x*this.z, this.x*other.y - other.x*this.y);
	}
	
	public Vector constantProduct(double constant){
		return new Vector(this.x*constant, this.y*constant, this.z*constant);
	}
}	
	
