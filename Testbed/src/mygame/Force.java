package mygame;
import mygame.Vector;

/**
 * 
 * @author Gilles
 *
 */
public class Force {

	private Aircraft plane;
	private Vector tailGravityForce = Vector.NULL;
	private Vector WingGravityForce = Vector.NULL;
	private Vector engineGravityForce = Vector.NULL;
	private Vector thrustForce = Vector.NULL;
	private Vector leftWingAttack = Vector.NULL;
	private Vector rightWingAttack = Vector.NULL;
	private Vector horizontalStabilizerAttack = Vector.NULL;
	private Vector verticalStabilizerAttack = Vector.NULL;
	private Vector leftWingLift = Vector.NULL;
	private Vector rightWingLift = Vector.NULL;
	private Vector horizontalStabilizerLift = Vector.NULL;
	private Vector verticalStabilizerLift = Vector.NULL;
	private Vector liftForce = Vector.NULL;
	
	private Vector windSpeed = Vector.NULL;

	private Vector rightWingAxis = new Vector(1,0,0);
	private Vector leftWingAxis = new Vector(1,0,0);
	private Vector verticalStabilizerAxis = new Vector(0,1,0);
	private Vector horizontalStabilizerAxis = new Vector(1,0,0);
	
	private float wingLiftSlope = 1f;
        private float horStabLiftSlope = 0.1f;
        private float verStabLiftSlope = 0.1f;
	
	
	Force(float thrust,Aircraft plane){
		
                this.plane = plane;
		this.setAttackAngles(this.getAircraft().getLeftWingInclination(), this.getAircraft().getRightWingInclination(), this.getAircraft().getHorStabInclination(), this.getAircraft().getVerStabInclination());
		
		this.setGravityForces(this.getAircraft().getTailMass(), this.getAircraft().getWingMass(), this.getAircraft().getEngineMass(), this.getAircraft().getGravityConstant());
		this.setLiftForce();
		this.setThrust(thrust);			
	}
	
        public void UpdateForce(){

		this.setAttackAngles(this.getAircraft().getLeftWingInclination(), this.getAircraft().getRightWingInclination(), this.getAircraft().getHorStabInclination(), this.getAircraft().getVerStabInclination());
		
		this.setGravityForces(this.getAircraft().getTailMass(), this.getAircraft().getWingMass(), this.getAircraft().getEngineMass(), this.getAircraft().getGravityConstant());
		this.setLiftForce();
				
	
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
		this.tailGravityForce = new Vector(0,-TailMass * gravityConstant, 0).inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
		this.WingGravityForce = new Vector(0, -WingMass * gravityConstant, 0).inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
		this.engineGravityForce = new Vector(0,-engineMass * gravityConstant, 0).inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
		
		}
	
	public void setThrust(float thrust){
		this.thrustForce = new Vector(0,0,-thrust);
	}
	
//	public void setAngles(double heading, double pitch, double roll){
//		this.heading = heading;
//		this.pitch = pitch;
//		this.roll = roll;
//	}
	
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
	
	
	///////////////////////////////////LIFT//////////////////////////////////////////
	
	public Vector getLeftWingAirSpeed(){
		Vector leftWingAirSpeed = getLeftWingVelocity().subtract(windSpeed);
		return leftWingAirSpeed;
	}
	
	public Vector getRightWingAirSpeed(){
		Vector rightWingAirSpeed = getRightWingVelocity().subtract(windSpeed);
		return rightWingAirSpeed;
	}
	
	public Vector getHorizontalStabilizerAirSpeed(){
		Vector horizontalStabilizerAirSpeed = getStabilizerVelocity().subtract(windSpeed);
		return	horizontalStabilizerAirSpeed;
	}
	
	public Vector getVerticalStabilizerAirSpeed(){
		Vector verticalStabilizerAirSpeed = getStabilizerVelocity().subtract(windSpeed);
		return verticalStabilizerAirSpeed;
	}
	
	public Vector getRightWingVelocity(){
		return this.getAircraft().getVelocity().add(this.getAircraft().getWingX().crossProduct(this.getAircraft().getAngularVelocity()).inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()));
	}
	
	public Vector getLeftWingVelocity(){
		return this.getAircraft().getVelocity().add(this.getAircraft().getWingX().constantProduct(-1).crossProduct(this.getAircraft().getAngularVelocity()).inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()));
	}
	
	public Vector getStabilizerVelocity(){
		return this.getAircraft().getVelocity().add(this.getAircraft().getTailSize().crossProduct(this.getAircraft().getAngularVelocity()).inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()));
	}
	
	
	public Vector getRightWingProjectedAirspeed(){
                Vector airspeed = this.getRightWingAirSpeed().inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()); 
		Vector projairspeed = new Vector(0,airspeed.getY(),airspeed.getZ());

		
		return projairspeed.transform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll());
		}
	
	public Vector getLeftWingProjectedAirspeed(){
		Vector airspeed = this.getLeftWingAirSpeed().inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()); 
		Vector projairspeed = new Vector(0,airspeed.getY(),airspeed.getZ());
		return projairspeed.transform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll());
		}
	
	
	public Vector getHorizontalStabilizerProjectedAirspeed(){
                Vector airspeed = this.getHorizontalStabilizerAirSpeed().inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()); 
		Vector projairspeed = new Vector(0,airspeed.getY(),airspeed.getZ());

		
		return projairspeed.transform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll());
		}
	
	public Vector getVerticalStabilizerProjectedAirspeed(){
		Vector airspeed = this.getRightWingAirSpeed().inverseTransform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll()); 
		Vector projairspeed = new Vector(airspeed.getX(),0,airspeed.getZ());

		
		return projairspeed.transform(this.getAircraft().getHeading(),this.getAircraft().getPitch(),this.getAircraft().getRoll());
		}
	
	
	public float getRightWingAngleOfAttack(){
		double angle = -Math.atan2(getRightWingNormal().dotProduct(getRightWingProjectedAirspeed()),
				rightWingAttack.dotProduct(getRightWingProjectedAirspeed()));
		float AOA = (float)angle;
		return AOA;
	}
	
	public float getLeftWingAngleOfAttack(){
		double angle = -Math.atan2(getLeftWingNormal().dotProduct(getLeftWingProjectedAirspeed()),
				leftWingAttack.dotProduct(getLeftWingProjectedAirspeed()));
		float AOA = (float)angle;
		return AOA;
	}
	
	public float getHorizontalStabilizerAngleOfAttack(){
		double angle = -Math.atan2(getHorizontalStabilizerNormal().dotProduct(getHorizontalStabilizerProjectedAirspeed()),
				horizontalStabilizerAttack.dotProduct(getHorizontalStabilizerProjectedAirspeed()));
		float AOA = (float)angle;
		return AOA;
	}
	
	public float getVerticalStabilizerAngleOfAttack(){
		double angle = -Math.atan2(getVerticalStabilizerNormal().dotProduct(getVerticalStabilizerProjectedAirspeed()),
				verticalStabilizerAttack.dotProduct(getVerticalStabilizerProjectedAirspeed()));
		float AOA = (float)angle;
		return AOA;
	}
	
	
	public void setLeftWingLift(){
		float s2 = getLeftWingProjectedAirspeed().dotProduct(getLeftWingProjectedAirspeed());
		float ct = s2*wingLiftSlope*getLeftWingAngleOfAttack();
		this.leftWingLift = getLeftWingNormal().constantProduct(ct);
	}
	
	public void setRightWingLift(){
		float s2 = getRightWingProjectedAirspeed().dotProduct(getRightWingProjectedAirspeed());
		float ct = s2*wingLiftSlope*getRightWingAngleOfAttack();
		this.rightWingLift = getRightWingNormal().constantProduct(ct);
	}
	
	
	public void setHorizontalStabilizerLift(){
		float s2 = getHorizontalStabilizerProjectedAirspeed().dotProduct(getHorizontalStabilizerProjectedAirspeed());
		float ct = s2*horStabLiftSlope*getHorizontalStabilizerAngleOfAttack();
		this.horizontalStabilizerLift = getHorizontalStabilizerNormal().constantProduct(ct);
	}
	
	public void setVerticalStabilizerLift(){
		float s2 = getVerticalStabilizerProjectedAirspeed().dotProduct(getVerticalStabilizerProjectedAirspeed());
		float ct = s2*verStabLiftSlope*getVerticalStabilizerAngleOfAttack();
		this.verticalStabilizerLift = getVerticalStabilizerNormal().constantProduct(ct);
	}
	

	public void setLiftForce(){
		this.setLeftWingLift();
		this.setRightWingLift();
		this.setHorizontalStabilizerLift();
		this.setVerticalStabilizerLift();
                this.leftWingLift.printVector("Llift");
                this.rightWingLift.printVector("Rlift");
                this.horizontalStabilizerLift.printVector("horLift");
                this.verticalStabilizerLift.printVector("verLift");
		this.liftForce = this.leftWingLift.add(this.rightWingLift).add(this.horizontalStabilizerLift).add(this.verticalStabilizerLift);
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
		
		return this.liftForce;
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

	

	public Vector getEnginePlace(){
		return getAircraft().getTailSize().constantProduct(- getAircraft().getTailMass()/getAircraft().getEngineMass());
	}
	
	/**
	 * Returnt een vector! Maar is eigenlijk een diagonaalmatrix
	 * 
	 */
	public Vector getInertiaTensor(){
		//elementen vd matrix berekenen - alles behalve elementen op de diagonaal zijn 0
		
		double Ixx1 = Math.pow(getAircraft().getTailSize().getZ(),2)*getAircraft().getTailMass() + Math.pow(getAircraft().getEnginePlace().getZ(),2)*getAircraft().getEngineMass();
		float Ixx = (float)Ixx1;
		
		double Iyy1 = 2*Math.pow(getAircraft().getWingX().getX(), 2)*getAircraft().getWingMass() + Math.pow(getAircraft().getTailSize().getZ(),2)*getAircraft().getTailMass() + Math.pow(getAircraft().getEnginePlace().getZ(),2)*getAircraft().getEngineMass();
		float Iyy = (float)Iyy1;
		
		double Izz1 = 2*Math.pow(getAircraft().getWingX().getX(), 2)*getAircraft().getWingMass();
		float Izz = (float)Izz1;
		
		return new Vector(Ixx,Iyy,Izz);
	}
        
        public Vector getInverseInertia(){
            Vector InertiaTensor = this.getInertiaTensor();
            //System.out.println(1/InertiaTensor.getX() + " " + 1/InertiaTensor.getY() + " " + 1/InertiaTensor.getZ());
            return new Vector(1/InertiaTensor.getX(),1/InertiaTensor.getY(),1/InertiaTensor.getZ());
        }
	
	
	// in drone assenstelsel
	public Vector getTotalForce(){
		return this.getTotalLift().add(this.getTotalGravityForce()).add(this.getThrustForce());
	}
        
    public Vector getTotalMoment(){
    	Vector wingR = getAircraft().getWingX().crossProduct(getWingGravityForce().add(getRightWingLift()));
        //System.out.println(wingR.getX() + " " + wingR.getY() + " " + wingR.getZ());
    	Vector wingL = getAircraft().getWingX().constantProduct(-1).crossProduct(getWingGravityForce().add(getLeftWingLift()));
    	//System.out.println(wingL.getX() + " " + wingL.getY() + " " + wingL.getZ());
        Vector tail  = getAircraft().getTailSize().crossProduct(getTailGravityForce().add(getHorizontalStabilizerLift()).add(getVerticalStabilizerLift()));
        Vector engine = getEnginePlace().crossProduct(getEngineGravityForce().add(getThrustForce()));   
    	return wingR.add(wingL).add(tail).add(engine);
    }
	
    
    
    
}