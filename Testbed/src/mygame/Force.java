 package mygame;



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
    
    private Vector leftBreakForce = Vector.NULL;
    private Vector rightBreakForce = Vector.NULL;
    private Vector frontBreakForce = Vector.NULL;
    
    private Vector leftRearWheelFrictionForce = Vector.NULL;
    private Vector rightRearWheelFrictionForce = Vector.NULL;



    private float previousDFront;
    private float previousDRR;
    private float previousDLR;
    private float currentDFront;
    private float currentDRR;
    private float currentDLR;

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
        this.setNormalForces();
        this.setFrictionForces();
    }
    
    public void setFrictionForces() {
        this.setLeftRearWheelFrictionForce();
        this.setRightRearWheelFrictionForce();
    }
    
    public void setNormalForces() {
    	this.setFrontWheelD();
    	this.setRightRearD();
    	this.setLeftRearD();
        this.setFrontWheelNormalForce();
        this.setLeftRearWheelNormalForce();
        this.setRightRearWheelNormalForce();
    }

    
    
    public void setBrakeForces(float front, float left, float right) {
        float maxbrake = -(getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll()).getZ() * getAircraft().getTotalMass()/0.01f + getThrustForce().getZ());
        
        System.out.println("forward speed " +  getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll()).getZ());
        System.out.println("thrust" + -getThrustForce().getZ());
       
        
        if (maxbrake > getAircraft().getConfig().getRMax()){
            maxbrake = getAircraft().getConfig().getRMax();
        }
        if (front + right + left < maxbrake){
        this.setFrontWheelBreakForce(front);
        this.setRightRearWheelBreakForce(right);
        this.setLeftRearWheelBreakForce(left);
        } 
        
        else if(maxbrake > 100){
        this.setFrontWheelBreakForce(front * maxbrake/(front+left+right));
        this.setRightRearWheelBreakForce(right* maxbrake/(front+left+right));
        this.setLeftRearWheelBreakForce(left* maxbrake/(front+left+right));
        }else{
        this.setFrontWheelBreakForce(0);
        this.setRightRearWheelBreakForce(0);
        this.setLeftRearWheelBreakForce(0);
        }
        System.out.println("maxbrake:  " + maxbrake);
        getTotalBreakForce().printVector("totalBrake");
    }

    public void checkBrakes(Vector Acc, Vector Vel){
        if (Acc.getZ()< 0 && Vel.getZ()> 0 ){
            this.getAircraft().setVelocity(Vector.NULL);
            this.getAircraft().setAcceleration(Vector.NULL);
            this.getAircraft().setAngularVelocity(Vector.NULL);
            this.getAircraft().setAngularAcceleration(Vector.NULL);
        }
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
        if (thrust > getAircraft().getConfig().getMaxThrust())
            thrust = getAircraft().getConfig().getMaxThrust();
        else if (thrust < 0)
            thrust = 0;
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
        return getLeftWingVelocity().subtract(getWindSpeed()).add(getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(),
                this.getAircraft().getPitch(), this.getAircraft().getRoll()).crossProduct(getAircraft().getWingX().constantProduct(-1)));
    }

    public Vector getRightWingAirSpeed(){
        return getRightWingVelocity().subtract(getWindSpeed()).add(getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(),
                this.getAircraft().getPitch(), this.getAircraft().getRoll()).crossProduct(getAircraft().getWingX()
    ));
    }

    public Vector getHorizontalStabilizerAirSpeed(){
        return getStabilizerVelocity().subtract(getWindSpeed()).add(getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(),
                this.getAircraft().getPitch(), this.getAircraft().getRoll()).crossProduct(getAircraft().getTailSize()));
    }

    public Vector getVerticalStabilizerAirSpeed(){
        return getStabilizerVelocity().subtract(getWindSpeed()).add(getAircraft().getAngularVelocity().inverseTransform(getAircraft().getHeading(),
                this.getAircraft().getPitch(), this.getAircraft().getRoll()).crossProduct(getAircraft().getTailSize()));
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
                //getAircraft().getPitch(), getAircraft().getRoll())));
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
    	//System.out.println("current:" + currentDLR + "previous: "+ previousDLR);

    	return (currentDLR - previousDLR)/0.01f;
    }
    
    public float getRightRearWheelDChange(){
    	return (currentDRR - previousDRR)/0.01f;
    }
    
    public float getFrontWheelDChange(){
    	return (currentDFront - previousDFront)/0.01f;

    }
    
    public float getLeftRearWheelD(){
    	return this.currentDLR;
    }
    
    
    public float getFrontWheelD(){
    	return this.currentDFront;
    }
    
    public float getRightRearWheelD(){
        return this.currentDRR;
    }
    
    private void setFrontWheelD() {
    	         //Vector FWheelDrone = new Vector(0, this.getAircraft().getConfig().getWheelY(), this.getAircraft().getConfig().getFrontWheelZ());
    	     	 Vector FWheelDrone = this.getAircraft().getFrontWheel();
    	Vector FWheelWorld = FWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	         Vector WheelPlace = getAircraft().getCalcCoordinates().add(FWheelWorld);
    	         //System.out.println("WheelPlaceFront: "+ WheelPlace);
    	         previousDFront = currentDFront; 
                 
            //    WheelPlace.printVector("wheelplace");
    	         currentDFront = - WheelPlace.getY() +  getAircraft().getTyreRadius();
    	         if (currentDFront < 0) {
    	             this.currentDFront = 0;
    	         }
    	     }
    	 
    private void setRightRearD() {
    	         //Vector RRWheelDrone = new Vector(this.getAircraft().getConfig().getRearWheelX(), this.getAircraft().getConfig().getWheelY(), this.getAircraft().getConfig().getRearWheelZ());
    	Vector RRWheelDrone = this.getAircraft().getRightRearWheel();     	 
    	Vector RRWheelWorld = RRWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	         Vector WheelPlace = getAircraft().getCalcCoordinates().add(RRWheelWorld);
    	         //System.out.println("WheelPlaceRightRear: "+ WheelPlace);
    	         previousDRR = currentDRR;
    	         currentDRR = - WheelPlace.getY() +  getAircraft().getTyreRadius();
    	         if (currentDRR < 0) {
    	             this.currentDRR = 0;
    	         }
    	     }
    	 
    private void setLeftRearD() {
    	         //Vector LRWheelDrone = new Vector(-this.getAircraft().getConfig().getRearWheelX(), this.getAircraft().getConfig().getWheelY(), this.getAircraft().getConfig().getRearWheelZ());
    	Vector LRWheelDrone = this.getAircraft().getLeftRearWheel();     	 
    	Vector LRWheelWorld = LRWheelDrone.transform(this.getAircraft().getHeading(), this.getAircraft().getPitch(), this.getAircraft().getRoll());
    	
    	         Vector WheelPlace = getAircraft().getCalcCoordinates().add(LRWheelWorld);
    	         //System.out.println("WheelPlaceLeftRear: "+ WheelPlace);
    	         previousDLR = currentDLR;
    	         currentDLR = - WheelPlace.getY() +  getAircraft().getTyreRadius();
    	         if (currentDLR < 0) {
    	             this.currentDLR = 0;
    	         }
    	     }
    
    
    public void setLeftRearWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();
    	float temp = tyreSlope*getLeftRearWheelD()+dampSlope*getLeftRearWheelDChange();
    	if (temp < 0)
                temp = 0;
    	this.leftRearWheelNormalForce = new Vector(0,temp,0).inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll());;

    	
    }

    public void setRightRearWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();
    	
    	float temp = tyreSlope*getRightRearWheelD()+dampSlope*getRightRearWheelDChange();
    	if (temp < 0)
                temp = 0;
    	this.rightRearWheelNormalForce = new Vector(0,temp,0).inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll());;
    	
    }
    
    public void setFrontWheelNormalForce(){
    	float tyreSlope = this.getAircraft().getConfig().getTyreSlope();
    	float dampSlope = this.getAircraft().getConfig().getDampSlope();
    	
    	
    	float temp = tyreSlope*getFrontWheelD()+dampSlope*getFrontWheelDChange();
    	if (temp < 0)
                temp = 0;
    	this.frontWheelNormalForce = new Vector(0,temp,0).inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll());;
    }
    
    public void setTotalNormalForce(){
    	
    	this.setFrontWheelNormalForce();
    	this.setLeftRearWheelNormalForce();
    	this.setRightRearWheelNormalForce();
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
    
    public void setLeftRearWheelBreakForce(float left){
        if (this.getLeftRearWheelD() != 0){
            
    	this.leftBreakForce = new Vector(0, 0, left);
        }
        else {
            this.leftBreakForce = new Vector(0, 0, 0);
        }
        
    }
    
    public void setRightRearWheelBreakForce(float right){
        if (this.getRightRearWheelD() != 0){
    	this.rightBreakForce = new Vector(0, 0, right);
                }else {
            this.rightBreakForce = new Vector(0, 0, 0);
                }
    }
    
    public void setFrontWheelBreakForce(float front){
        if (this.getFrontWheelD() != 0 ){
    	this.frontBreakForce = new Vector(0, 0, front);
        } else{ 
            this.frontBreakForce = new Vector(0, 0, 0);
        }
    }
    
    public Vector getRightRearWheelBreakForce(){
    	return this.rightBreakForce;
        
    }
    
    public Vector getLeftRearWheelBreakForce(){
    	return this.leftBreakForce;
    }
    
    public Vector getFrontWheelBreakForce(){

    	return this.frontBreakForce;
    }
    
    public Vector getTotalBreakForce(){
    	return this.getRightRearWheelBreakForce().add(this.getLeftRearWheelBreakForce()).add(getFrontWheelBreakForce());
    }
    
    public void setLeftRearWheelFrictionForce(){

    	this.leftRearWheelFrictionForce = new Vector(-this.getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll()).
                getX()*this.getLeftRearWheelNormalForce().getY()*this.getAircraft().getConfig().getFcMax(), 0, 0);
        
    }
    
    public void setRightRearWheelFrictionForce(){
        this.rightRearWheelFrictionForce = new Vector(this.getAircraft().getVelocity().inverseTransform(getAircraft().getHeading(), getAircraft().getPitch(), getAircraft().getRoll())
                .getX()*this.getRightRearWheelNormalForce().getY()*this.getAircraft().getConfig().getFcMax(), 0, 0);
    }
    
    public Vector getLeftRearWheelFrictionForce(){
    	return this.leftRearWheelFrictionForce;
    }
    
    public Vector getRightRearWheelFrictionForce(){
    	return this.rightRearWheelFrictionForce;
    }
    
    public Vector getTotalFrictionForce(){
    	return this.getLeftRearWheelFrictionForce().add(this.getRightRearWheelFrictionForce());
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
            return this.getTotalLift().add(this.getTotalGravityForce()).add(this.getThrustForce()).
                    add(getTotalWheelNormalForce()).add(getTotalFrictionForce()).add(getTotalBreakForce());
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
        Vector frontWheel = getAircraft().getFrontWheel().crossProduct(getFrontWheelBreakForce().add(getFrontWheelNormalForce()));
        Vector rearLeft = getAircraft().getLeftRearWheel().crossProduct(getLeftRearWheelNormalForce().add(getLeftRearWheelFrictionForce()).add(getLeftRearWheelBreakForce()));
        Vector rearRight = getAircraft().getRightRearWheel().crossProduct(getRightRearWheelNormalForce().add(getRightRearWheelFrictionForce()).add(getRightRearWheelBreakForce()));

        Vector totalMoment =  wingR.add(wingL).add(tail).add(engine).add(frontWheel).add(rearLeft).add(rearRight);

        return totalMoment;
    }

}