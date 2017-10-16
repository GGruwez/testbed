package mygame;
import mygame.Vector;

/**
 * 
 * @author Gilles
 *
 */
public class Force {

	private Aircraft plane;
	private Vector tailGravityForce;
	private Vector WingGravityForce;
	private Vector engineGravityForce;
	private Vector thrustForce;
	private Vector leftWingAttack;
	private Vector rightWingAttack;
	private Vector horizontalStabilizerAttack;
	private Vector verticalStabilizerAttack;
	private Vector leftWingLift;
	private Vector rightWingLift;
	private Vector horizontalStabilizerLift;
	private Vector verticalStabilizerLift;
	
	private Vector initialSpeed;
	private Vector windSpeed;

	private Vector rightWingAxis = new Vector(1,0,0);
	private Vector leftWingAxis = new Vector(1,0,0);
	private Vector verticalStabilizerAxis = new Vector(0,1,0);
	private Vector horizontalStabilizerAxis = new Vector(1,0,0);
	
	
	Force(float TailMass, float WingMass, float engineMass, float gravityConstant,float thrust,
			float leftWingInclination, float rightWingInclination, float horStabInclination, float verStabInclination){
		
		this.setAttackAngles(leftWingInclination, rightWingInclination, horStabInclination, verStabInclination);

		
		this.setGravityForces(TailMass, WingMass, engineMass, gravityConstant);
		this.setLiftForce();
		this.setThrust(thrust);
			
	}
	
	public Aircraft getAircraft(){
		return this.plane;
	}
	
	
	public void setAttackAngles(float leftWingInclination, float rightWingInclination, float horStabInclination, float verStabInclination){
		this.leftWingAttack = new Vector(0, (float)Math.sin(leftWingInclination), (float) -Math.cos(leftWingInclination));
		this.rightWingAttack = new Vector(0, (float)Math.sin(rightWingInclination), (float) -Math.cos(rightWingInclination));
		this.horizontalStabilizerAttack =new  Vector(0, (float)Math.sin(horStabInclination), (float) -Math.cos(horStabInclination));
		this.verticalStabilizerAttack =new  Vector((float) -Math.sin(verStabInclination), 0, (float) -Math.cos(verStabInclination));
	}

	
	public void setGravityForces(float TailMass, float WingMass, float engineMass, float gravityConstant){
		this.tailGravityForce = new Vector(0,-TailMass * gravityConstant, 0);
		this.WingGravityForce = new Vector(0, -WingMass * gravityConstant, 0);
		this.engineGravityForce = new Vector(0,-engineMass * gravityConstant, 0);
		
		}
	
	public void setThrust(float thrust){
		this.thrustForce = new Vector(0,0,-thrust);
	}
	
//	public void setAngles(double heading, double pitch, double roll){
//		this.heading = heading;
//		this.pitch = pitch;
//		this.roll = roll;
//	}
	
	
	public void setLiftForce(){
		
	}
	
	
	public Vector getLeftWingNormal(){
		return this.leftWingAxis.crossProduct(this.leftWingAttack);
	}
	
	public Vector getRightWingNormal(){
		return this.rightWingAxis.crossProduct(this.rightWingAttack);
	}
	
	public Vector getHorizontalStabilizerNormal(){
		return this.horizontalStabilizerAxis.crossProduct(this.horizontalStabilizerAttack);
	}
	
	public Vector getVerticalStabilizerNormal(){
		return this.verticalStabilizerAxis.crossProduct(verticalStabilizerAttack);
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
	
	public Vector getEngineGravityForce(){
		return this.engineGravityForce;
	}
	
	public Vector getTailGravityForce(){
		return this.tailGravityForce;
	}
	
	public Vector getTotalGravityForce(){
		return this.getEngineGravityForce().add(this.getTailGravityForce().add(this.getWingGravityForce().add(this.getWingGravityForce())));
	}
	
	/**
	 * v = a*t+v0
	 * @return
	 */
	public Vector getAirSpeed(){
		Vector airSpeed = getAircraft().getVelocity().subtract(windSpeed);
		return airSpeed;
	}
	
	
	
	
	public Vector getProjectedAirspeed(){
		Vector projectedAirspeed = new Vector(0,0,0);
		return projectedAirspeed;
		}
	
	public double getProjectedAirspeedSize(){
		return getProjectedAirspeed().euclideanLength();
	}
	
	public double getAngleOfAttack(){
		return Math.atan2(getLeftWingNormal().dotProduct(getProjectedAirspeed()),
				leftWingAttack.dotProduct(getProjectedAirspeed()));
	}
	
	public Vector EnginePlace(){
		return getAircraft().getTailSize().constantProduct(- getAircraft().getTailMass()/getAircraft().getEngineMass());
	}
	
	// in drone assenstelsel
	public Vector getTotalForce(){
		return this.getTotalLift().add(this.getTotalGravityForce()).add(this.getThrustForce());
	}
        
    public Vector getTotalMoment(){
    	Vector wingR = getAircraft().getWingx().crossProduct(getWingGravityForce().add(getRightWingLift()));
    	Vector wingL = getAircraft().getWingx().constantProduct(-1).crossProduct(getWingGravityForce().add(getRightWingLift()));
    	Vector tail  = getAircraft().getTailSize().crossProduct(getTailGravityForce().add(getHorizontalStabilizerLift()).add(getVerticalStabilizerLift()));
    	Vector engine = EnginePlace().crossProduct(getEngineGravityForce().add(getThrustForce()));
    	return wingR.add(wingL).add(tail).add(engine);
    }
	
}