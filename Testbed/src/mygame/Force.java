 package mygame;

import interfaces.PreviousInputs;

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
    
    private Vector leftRearWheelNormalForce = Vector.NULL;
    private Vector rightRearWheelNormalForce = Vector.NULL;
    private Vector frontWheelNormalForce = Vector.NULL;
    
    private Vector leftRearWheelBreakForce = Vector.NULL;
    private Vector rightRearWheelBreakForce = Vector.NULL;
    private Vector frontWheelBreakForce = Vector.NULL;


    private float previousDFront;
    private float previousDRR;
    private float previousDLR;

    private Vector windSpeed = Vector.NULL;
    
    
    private Vector rightWingAxis = new Vector(1,0,0);
    private Vector leftWingAxis = new Vector(1,0,0);
    private Vector verticalStabilizerAxis = new Vector(0,1,0);
    private final Vector horizontalStabilizerAxis = new Vector(1,0,0);
    
    private Vector wheelNormal = new Vector(0,1,0);
    

    private final float FORCE_NEGLECT  = 0.00001f;

    Force(float thrust,Aircraft plane){
        this.setAircraft(plane);
        this.setAttackAngles(this.getAircraft().getLeftWingInclination(), 
                this.getAircraft().getRightWingInclination(), 
                this.getAircraft().getHorStabInclination(), 
                this.getAircraft().getVerStabInclination());
        this.setGravityForces(this.getAircraft().getTailMass(), this.getAircraft().getWingMass(), this.getAircraft().getEngineMass());
        this.setLiftForce();
        this.setThrust(thrust);			
    }

    public void UpdateForce(){
        this.setAttackAngles(this.getAircraft().getLeftWingInclination(), 
                this.getAircraft().getRightWingInclination(), 
                this.getAircraft().getHorStabInclination(), 
                this.getAircraft().getVerStabInclination());
        this.setGravityForces(this.getAircraft().getTailMass(), this.getAircraft().getWingMass(), 
                this.getAircraft().getEngineMass());
        this.setLiftForce();
    }

    public Aircraft getAircraft(){
        return this.plane;
    }

    public void setAircraft(Aircraft plane) {
        this.plane = plane;
    }

    public void setAttackAngles(float leftWingInclination, float rightWingInclination, float horStabInclination, float verStabInclination){
        this.leftWingAttack = new Vector(0, (float)Math.sin(leftWingInclination), 
                (float) -Math.cos(leftWingInclination));
        this.rightWingAttack = new Vector(0, (float)Math.sin(rightWingInclination), 
                (float) -Math.cos(rightWingInclination));
        this.horizontalStabilizerAttack =new  Vector(0, (float)Math.sin(horStabInclination), 
                (float) -Math.cos(horStabInclination));
        this.verticalStabilizerAttack =new  Vector((float) -Math.sin(verStabInclination), 0, 
                (float) -Math.cos(verStabInclination));
    }

    public void setGravityForces(float TailMass, float WingMass, float engineMass){
        this.tailGravityForce = new Vector(0,-TailMass * getAircraft().getGravityConstant(), 0).
                inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
        this.WingGravityForce = new Vector(0, -WingMass * getAircraft().getGravityConstant(), 0).
                inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
        this.engineGravityForce = new Vector(0,-engineMass * getAircraft().getGravityConstant(), 0).
                inverseTransform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    }

    public void setThrust(float thrust){
        this.thrustForce = new Vector(0,0,-thrust);
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
        return this.getEngineGravityForce().add(this.getTailGravityForce().add(this.getWingGravityForce().
                add(this.getWingGravityForce())));
    }

    // LIFT //

    public Vector getWindSpeed() {
        return this.windSpeed.inverseTransform(
            getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll());
    }

    public Vector getLeftWingAirSpeed(){
        return getLeftWingVelocity().subtract(getWindSpeed());
    }

    public Vector getRightWingAirSpeed(){
        return getRightWingVelocity().subtract(getWindSpeed());
    }

    public Vector getHorizontalStabilizerAirSpeed(){
        return getStabilizerVelocity().subtract(getWindSpeed());
    }

    public Vector getVerticalStabilizerAirSpeed(){
        return getStabilizerVelocity().subtract(getWindSpeed());
    }

    public Vector getRightWingVelocity(){

        return this.getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), 

                getAircraft().getPitch(), getAircraft().getRoll());//.add(this.getAircraft().
                //getWingX().crossProduct(this.getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(), 
                //getAircraft().getPitch(), getAircraft().getRoll())));
    }

    public Vector getLeftWingVelocity(){
        return this.getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), 
                getAircraft().getPitch(), getAircraft().getRoll());//.add(this.getAircraft().
                //getWingX().constantProduct(-1).crossProduct(this.getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(), 
               // getAircraft().getPitch(), getAircraft().getRoll())));
    }

    public Vector getStabilizerVelocity(){
        return this.getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), 
                getAircraft().getPitch(), getAircraft().getRoll());//.add(this.getAircraft().
                //getTailSize().crossProduct(this.getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(), 
                //getAircraft().getPitch(), getAircraft().getRoll())));

    }

    public Vector getRightWingProjectedAirspeed(){
        return new Vector(0, this.getRightWingAirSpeed().getY(), this.getRightWingAirSpeed().getZ());
    }

    public Vector getLeftWingProjectedAirspeed(){
        return new Vector(0, this.getLeftWingAirSpeed().getY(), this.getLeftWingAirSpeed().getZ());
    }

    public Vector getHorizontalStabilizerProjectedAirspeed(){
        return new Vector(0, this.getHorizontalStabilizerAirSpeed().getY(), 
                this.getHorizontalStabilizerAirSpeed().getZ());
    }

    public Vector getVerticalStabilizerProjectedAirspeed(){
        return new Vector(this.getVerticalStabilizerAirSpeed().getX(), 0,
                this.getVerticalStabilizerAirSpeed().getZ());
    }

    public float getRightWingAngleOfAttack(){
        double angle = -Math.atan2(getRightWingNormal().dotProduct(getRightWingProjectedAirspeed()), rightWingAttack.dotProduct(getRightWingProjectedAirspeed()) );

        return (float) angle;
    }

    public float getLeftWingAngleOfAttack(){

        double angle = -Math.atan2(getLeftWingNormal().dotProduct(getLeftWingProjectedAirspeed()),leftWingAttack.dotProduct(getLeftWingProjectedAirspeed())
                        );

        return (float) angle;
    }

    public float getHorizontalStabilizerAngleOfAttack(){

        double angle = -Math.atan2(  getHorizontalStabilizerNormal().dotProduct(getHorizontalStabilizerProjectedAirspeed()),horizontalStabilizerAttack.dotProduct(getHorizontalStabilizerProjectedAirspeed())
                      );

        return (float) angle;
    }

    public float getVerticalStabilizerAngleOfAttack(){

        double angle = -Math.atan2( getVerticalStabilizerNormal().dotProduct(getVerticalStabilizerProjectedAirspeed()),verticalStabilizerAttack.dotProduct(getVerticalStabilizerProjectedAirspeed())
               );

        return (float) angle;
    }


    public void setLeftWingLift(){
        float s2 = getLeftWingProjectedAirspeed().dotProduct(getLeftWingProjectedAirspeed());
 //       System.out.println("s^2: " + s2);
        float ct = s2*getAircraft().getConfig().getWingLiftSlope()*getLeftWingAngleOfAttack();
 //       System.out.println("S " +getLeftWingProjectedAirspeed());
        this.leftWingLift = getLeftWingNormal().constantProduct(ct).checkAndNeglect(FORCE_NEGLECT);
    }

    public void setRightWingLift(){
        float s2 = getRightWingProjectedAirspeed().dotProduct(getRightWingProjectedAirspeed());
        float ct = s2*getAircraft().getConfig().getWingLiftSlope()*getRightWingAngleOfAttack();
        this.rightWingLift = getRightWingNormal().constantProduct(ct).checkAndNeglect(FORCE_NEGLECT);
    }


    public void setHorizontalStabilizerLift(){
        float s2 = getHorizontalStabilizerProjectedAirspeed().dotProduct(getHorizontalStabilizerProjectedAirspeed());
        float ct = s2*getAircraft().getConfig().getHorStabLiftSlope()*getHorizontalStabilizerAngleOfAttack();
        this.horizontalStabilizerLift = getHorizontalStabilizerNormal().constantProduct(ct).checkAndNeglect(FORCE_NEGLECT);
    }

    public void setVerticalStabilizerLift(){
        float s2 = getVerticalStabilizerProjectedAirspeed().dotProduct(getVerticalStabilizerProjectedAirspeed());
        float ct = s2*getAircraft().getConfig().getVerStabLiftSlope()*getVerticalStabilizerAngleOfAttack();
        this.verticalStabilizerLift = getVerticalStabilizerNormal().constantProduct(ct).checkAndNeglect(FORCE_NEGLECT);
    }


    public void setLiftForce(){
        this.setLeftWingLift();
        this.setRightWingLift();
        this.setHorizontalStabilizerLift();
        this.setVerticalStabilizerLift();
//		this.leftWingLift.printVector("Llift");
//		this.rightWingLift.printVector("Rlift");
//		this.horizontalStabilizerLift.printVector("horLift");
//		this.verticalStabilizerLift.printVector("verLift");
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
        return this.getLeftWingLift().add(this.getRightWingLift().add(this.getHorizontalStabilizerLift().
                add(this.getVerticalStabilizerLift())));
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
        return getAircraft().getTailSize().
                constantProduct(-getAircraft().getTailMass()/getAircraft().getEngineMass());
    }

    /////////////////////////////////// WHEELS /////////////////////////////////
    
   
    public float getLeftRearWheelDChange(){
    	return Math.abs(previousDLR - this.getLeftRearWheelD());
    }
    
    public float getRightRearWheelDChange(){
    	return Math.abs(previousDRR - this.getRightRearWheelD());
    }
    
    public float getFrontWheelDChange(){
    	return Math.abs(previousDFront - this.getFrontWheelD());
    }
    
    public float getLeftRearWheelD(){
    	
    	Vector LRWheelDrone = new Vector(-this.getAircraft().getConfig().getRearWheelX(), this.getAircraft().getConfig().getWheelY(), this.getAircraft().getConfig().getRearWheelZ());
    	Vector LRWheelWorld = LRWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	
    	previousDLR = LRWheelWorld.getY() - this.getAircraft().getConfig().getTyreRadius() ;
    	return previousDLR;
    }
    
    public float getRightRearWheelD(){
    	Vector RRWheelDrone = new Vector(this.getAircraft().getConfig().getRearWheelX(), this.getAircraft().getConfig().getWheelY(), this.getAircraft().getConfig().getRearWheelZ());
    	Vector RRWheelWorld = RRWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	
    	previousDRR = RRWheelWorld.getY() - this.getAircraft().getConfig().getTyreRadius() ;
    	return previousDRR;
    }
    
    public float getFrontWheelD(){
    	
    	Vector FrontWheelDrone = new Vector(0,this.getAircraft().getConfig().getWheelY(),this.getAircraft().getConfig().getFrontWheelZ());
    	Vector FrontWheelWorld = FrontWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	
    	previousDFront = FrontWheelWorld.getY() - this.getAircraft().getConfig().getTyreRadius();
    	return previousDFront;
    	
    }
    
    
    public void setLeftRearWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();
    	
    	this.leftRearWheelNormalForce = new Vector(0,tyreSlope*getLeftRearWheelD()+dampSlope*getLeftRearWheelDChange(),0);
    }

    public void setRightRearWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();
    	
    	this.rightRearWheelNormalForce = new Vector(0,tyreSlope*getRightRearWheelD()+dampSlope*getRightRearWheelDChange(),0);

    }
    
    public void setFrontWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();

    	this.frontWheelNormalForce = new Vector(0,tyreSlope*getFrontWheelD()+dampSlope*getFrontWheelDChange(),0);

    }
    
    public Vector getRightRearWheelNormalForce(){
    	return this.rightRearWheelNormalForce;
    }
    
    public Vector getLeftRearWheelNormalForce(){
    	return this.leftRearWheelNormalForce;
    }
    
    public Vector getFrontWheelNormalForce(){
    	return this.frontWheelNormalForce;
    }
    
    
    public Vector getTotalWheelNormalForce(){
    	return this.getLeftRearWheelNormalForce().add(this.getRightRearWheelNormalForce().add(this.getFrontWheelNormalForce()
                ));    
    	}
    
    //is voor autopilot?
    public void setLeftRearWheelBreakForce(){
    	this.leftRearWheelBreakForce = new Vector(0,0,0);
    }
    
    public void setRightRearWheelBreakForce(){
    	this.leftRearWheelBreakForce = new Vector(0,0,0);
    }
    
    public void setFrontWheelBreakForce(){
    	this.leftRearWheelBreakForce = new Vector(0,0,0);
    }
    
    public Vector getRightRearWheelBreakForce(){
    	return this.rightRearWheelBreakForce;
    }
    
    public Vector getLeftRearWheelBreakForce(){
    	return this.leftRearWheelBreakForce;
    }
    
    public Vector getFrontWheelBreakForce(){
    	return this.frontWheelBreakForce;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returnt een vector! Maar is eigenlijk een diagonaalmatrix
     * 
     */
    public Vector getInertiaTensor(){
            //elementen vd matrix berekenen - alles behalve elementen op de diagonaal zijn 0

        double Ixx1 = Math.pow(getAircraft().getTailSize()
                .getZ(),2)*getAircraft().getTailMass() + Math.pow(getAircraft().getEnginePlace().getZ(),2)*getAircraft().getEngineMass();
        float Ixx = (float)Ixx1;


        double Iyy1 = 2*Math.pow(getAircraft().getWingX()
                .getX(), 2)*getAircraft().getWingMass() + Math.pow(getAircraft().getTailSize().getZ(),2)*getAircraft().getTailMass() + Math.pow(getAircraft().getEnginePlace().getZ(),2)*getAircraft().getEngineMass();

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
            return this.getTotalLift().add(this.getTotalGravityForce()).add(this.getThrustForce()).add(getTotalWheelNormalForce());
    }
    
    public Vector getTotalMoment(){
        Vector wingR = getAircraft().getWingX()
                .crossProduct(getWingGravityForce().add(getRightWingLift()));
        //System.out.println(wingR.getX() + " " + wingR.getY() + " " + wingR.getZ());
        Vector wingL = getAircraft().getWingX()
                .constantProduct(-1).crossProduct(getWingGravityForce().add(getLeftWingLift()));
        //System.out.println(wingL.getX() + " " + wingL.getY() + " " + wingL.getZ());
        Vector tail  = getAircraft().getTailSize()
                .crossProduct(getTailGravityForce().add(getHorizontalStabilizerLift()).add(getVerticalStabilizerLift()));
        Vector engine = getEnginePlace().crossProduct(getEngineGravityForce());   
        return wingR.add(wingL).add(tail).add(engine);
    }

}