package mygame;

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
	
	
	// transformatiematrixen, daar kan ik echt nie mee werken
	public Vector transform(double heading, double pitch, double roll ){
		
		
		
		return new Vector(0,0,0);
		
		
	}
	
}	
	
