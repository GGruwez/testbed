package mygame;
import java.lang.Object;
import java.Vector;
import java.math;

public class Forces {
	
	private double x;
	private double y;
	private double z;
	
	protected Forces(double x, double y, double z){
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	
	public void setX(double x){
		this.x = x;
	}
	
	
	public void setY(double y){
		this.y = y;
	}
	
	
	public void setZ(double z){
		this.z = z;
	}
	
	public Vector axisWing = Vector3D(1,0,0);
	public Vector axisHorizontalStabilizer = Vector(1,0,0);
	public Vector axisVerticalStabilizer = Vector(0,1,0);
	
	public Vector attackLeftWing = Vector(0, sin(this.leftWingInclination), -cos(this.leftWingInclination));
	public Vector attackRightWing = Vector(0, sin(this.rightWingInclination), -cos(this.rightWingInclination));
	public Vector attackHorizontalStabilizer = Vector(0, sin(horStabInclination), -cos(horStabInclination));
	public Vector attackVerticalStabilizer = Vector(-sin(verStabInclination), 0, -cos(verStabInclination));
	
	public Vector getAttackVector(){
		if 
	}
			
	
	public Vector getTotalLift(){
		return this.getLeftWingLift() + this.getRightWingLift() 
		+ this.getHorizontalStabilizerLift() + this.getVerticalStabilizerLift();
				
	}
	
	public Vector getLeftWingLift(){
		
	}
	
	public Vector getRightWingLift(){
		
	}
	
	public Vector getHorizontalStabilizerLift(){
		
	}
	
	public Vector getVerticalStabilizerLift(){
		
	}
	
	
	
	public Vector getMass(){
		
	}
	
	
}
