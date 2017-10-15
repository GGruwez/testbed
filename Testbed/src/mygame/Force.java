package mygame;
import mygame.Vector;

public class Force {

	private Aircraft plane;
	private Vector tailGravityForce;
	private Vector WingGravityForce;
	private Vector BodyGravityForce;
	private Vector thrustForce;
//	private double heading;
//	private double pitch;
//	private double roll;
	private Vector attackLeftWing;
	private Vector attackRightWing;
	private Vector attackHorizontalStabilizer;
	private Vector attackVerticalStabilizer;
	private Vector leftWingLift;
	private Vector rightWingLift;
	private Vector horizontalStabilizerLift;
	private Vector verticalStabilizerLift;


	Force(double TailMass, double WingMass, double BodyMass, double gravityConstant,double thrust,
			double leftWingInclination, double rightWingInclination, double horStabInclination, double verStabInclination){
		
		this.setAttackAngles(leftWingInclination, rightWingInclination, horStabInclination, verStabInclination);
//		this.setAngles(heading, pitch, roll);
		
		this.setGravityForces(TailMass, WingMass, BodyMass, gravityConstant);
		this.setLiftForce();
		this.setThrust(thrust);
		
		
	}
	
	
	public void setAttackAngles(double leftWingInclination, double rightWingInclination, double horStabInclination, double verStabInclination){
		this.attackLeftWing = new Vector(0, Math.sin(leftWingInclination), -Math.cos(leftWingInclination));
		this.attackRightWing = new Vector(0, Math.sin(rightWingInclination), -Math.cos(rightWingInclination));
		this.attackHorizontalStabilizer =new  Vector(0, Math.sin(horStabInclination), -Math.cos(horStabInclination));
		this.attackVerticalStabilizer =new  Vector(-Math.sin(verStabInclination), 0, -Math.cos(verStabInclination));
	}

	
	public void setGravityForces(double TailMass, double WingMass, double BodyMass, double gravityConstant){
		this.tailGravityForce = new Vector(0,-TailMass * gravityConstant, 0);
		this.WingGravityForce = new Vector(0, -WingMass * gravityConstant, 0);
		this.BodyGravityForce = new Vector(0,-BodyMass * gravityConstant, 0);
		
		}
	
	public void setThrust(double thrust){
		this.thrustForce = new Vector(0,0,-thrust);
	}
	
//	public void setAngles(double heading, double pitch, double roll){
//		this.heading = heading;
//		this.pitch = pitch;
//		this.roll = roll;
//	}
	
	
	public void setLiftForce(){
		
	}
	
	
	
	public Vector getTotalLift(){
		return this.getLeftWingLift().add(this.getRightWingLift()).add(this.getHorizontalStabilizerLift()).add(this.getVerticalStabilizerLift());			
	}
	
	public Vector getLeftWingLift(){
		return this.leftWingLift;
	}
	
	public Vector getRightWingLift(){
		return this.rightWingLift;
	}
	
	public Vector getHorizontalStabilizerLift(){
		return this.horizontalStabilizerLift;
	}
	
	public Vector getVerticalStabilizerLift(){
		return this.verticalStabilizerLift;
	}
	
	public Vector getThrustForce(){
		return this.thrustForce;
	}
	
	public Vector getWingGravityForce(){
		return this.WingGravityForce;
	}
	
	public Vector getBodyGravityForce(){
		return this.BodyGravityForce;
	}
	
	public Vector getTailGravityForce(){
		return this.tailGravityForce;
	}
	
	public Vector getTotalGravityForce(){
		return this.getBodyGravityForce().add(this.getTailGravityForce().add(this.getWingGravityForce().add(this.getWingGravityForce())));
	}
	
	
	// in drone assenstelsel
	public Vector getTotalForce(){
		return this.getTotalLift().add(this.getTotalGravityForce()).add(this.getThrustForce());
	}
	
}