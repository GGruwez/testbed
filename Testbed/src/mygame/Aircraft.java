package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class Aircraft extends Node {
    
    private Vector calcCoordinates = Vector.NULL;
    private Vector velocity = Vector.NULL;
    private Vector acceleration = Vector.NULL;
    private Force forces;
    private float pitch;
    private float roll = 0f;
    private float heading = 0;
    private Vector wingX = new Vector(1, 0, 0);
    private Vector tailSize = new Vector(0, 0, 1);
    private Vector angularAcceleration = Vector.NULL;
    private Vector angularVelocity = Vector.NULL;
    private World world;
    private AutopilotConfig config = new AutopilotConfig();
    private float leftWingInclination = 0.1994837f;
    private float rightWingInclination = 0.1994837f;
    private float horStabInclination;
    private float verStabInclination;
    private float elapsedTime;
    private boolean manualControl = false;
    private float neglectValue = 0.00001f;
    private byte[] image = new byte[0];
    
    private AirplaneModel aircraftGeometry;
    private Camera aircraftCamera;
    private CameraNode aircraftCameraNode;
    
    private float thrust;
   
    private Vector frontBreakForce;
    private Vector leftBreakForce;
    private Vector rightBreakForce;

    /**
     *
     * @param name
     * @param assetManager
     * @param x
     * @param y
     * @param z
     * @param xVelocity
     * @param yVelocity
     * @param zVelocity
     * @param thrust
     * @param leftWingInclination
     * @param rightWingInclination
     * @param horStabInclination
     * @param verStabInclination
     */
    public Aircraft(String name, AssetManager assetManager, float x, float y, float z, float xVelocity, float yVelocity, float zVelocity,
                    float thrust, float leftWingInclination, float rightWingInclination,
                    float horStabInclination, float verStabInclination) {

        this.aircraftGeometry = new AirplaneModel(assetManager, this);

        // Plane camera
        this.aircraftCamera = new Camera(200, 200);
        this.aircraftCamera.setFrustumPerspective(120,1,1,1000);
        this.aircraftCamera.setViewPort(4f, 5f, 0f, 1f);
        this.aircraftCameraNode = new CameraNode("Camera Node", this.aircraftCamera);
        this.aircraftCameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        this.attachChild(this.aircraftGeometry);
        this.attachChild(this.aircraftCameraNode);
        this.aircraftCameraNode.setLocalTranslation(0, 0, -aircraftGeometry.getPlaneTailMassOffset());
        this.aircraftCameraNode.lookAt(new Vector3f(0,0,-10), Vector3f.UNIT_Y); // Front of the plane is in -z direction
        
        // Fysica
        this.setCalcCoordinates(new Vector(x, y, z));
        this.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
        this.forces = new Force(0,this);
    }

    public Spatial getAircraftGeometry(){
        return this.aircraftGeometry;
    }
    
    public Camera getCamera(){
        return this.aircraftCamera;
    }

    public Vector getCalcCoordinates(){
        return this.calcCoordinates;
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z){ // TODO: use in evolve
        this.setCalcCoordinates(new Vector(x, y, z));
        super.setLocalTranslation(x, y, z);
    }

    public void updateVisualRotation(){
        // Rotatie tonen
        Quaternion pitchQuat = new Quaternion();
        pitchQuat.fromAngleAxis(getPitch(), new Vector3f(1, 0, 0));
        Quaternion rollQuat = new Quaternion();
        rollQuat.fromAngleAxis(getRoll(), (new Vector3f(0, 0, 1)));
        Quaternion yawQuat = new Quaternion();
        yawQuat.fromAngleAxis(getHeading(), new Vector3f(0, 1, 0));
        Quaternion totalQuat = (pitchQuat.mult(rollQuat)).mult(yawQuat);
        this.setLocalRotation(totalQuat); // TODO: put back
    }

    public void setCoordinates(Vector calcCoordinates){
        this.setLocalTranslation(calcCoordinates.getX(), calcCoordinates.getY(), calcCoordinates.getZ());
    }

    public void setCalcCoordinates(Vector coordinates){
        this.calcCoordinates = coordinates;
    }

    public void updateVisualCoordinates(){
        this.setCoordinates(this.getCalcCoordinates());
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
    
    public float getWingMass(){
    return this.getConfig().getWingMass();
    }
    
    public float getTotalMass(){
    	return this.getEngineMass()+this.getWingMass()*2+ this.getTailMass();
    }
    
    public float getPitch(){
    	return this.pitch;
    }
    
    public void setPitch(float pitch){
    	this.pitch = (float) pitch;
    }
    
    public float getRoll(){
    	return this.roll;
    }
    
    public void setRoll(float roll){
    	this.roll = (float) roll;
    }
    
    public float getHeading(){
    	return this.heading;
    }
    
    public void setHeading(float heading){
    	this.heading = (float) heading;
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
    
    public Vector getWingX(){
    	return this.wingX;
    }
    
    public Vector getTailSize(){
    	return this.tailSize;
    }
    
    public float getTailMass(){
    	return this.getConfig().getTailMass();
    }
    
    public float getEngineMass(){
    	return this.getConfig().getEngineMass();
    }
    
    public Vector getEnginePlace(){
	return this.getTailSize().constantProduct(-this.getTailMass()/this.getEngineMass());
    }

    public Vector getFrontWheel(){
    	return new Vector(0,this.getConfig().getWheelY(),this.getConfig().getFrontWheelZ());
    }
    
    public Vector getRightRearWheel(){
    	return new Vector(this.getConfig().getRearWheelX(),this.getConfig().getWheelY(),this.getConfig().getRearWheelZ());
    }
    
    public Vector getLeftRearWheel(){
    	return new Vector(-this.getConfig().getRearWheelX(),this.getConfig().getWheelY(),this.getConfig().getRearWheelZ());
    }
    
    public float getLeftWingInclination() {
        return leftWingInclination;
    }

    public void setLeftWingInclination(float leftWingInclination) {
        this.leftWingInclination = leftWingInclination;
    }

    public float getRightWingInclination() {
        return rightWingInclination;
    }

    public void setRightWingInclination(float rightWingInclination) {
        this.rightWingInclination = rightWingInclination;
    }

    public float getHorStabInclination() {
        return horStabInclination;
    }

    public void setHorStabInclination(float horStabInclination) {
        this.horStabInclination = horStabInclination;
    }

    public float getVerStabInclination() {
        return verStabInclination;
    }

    public void setVerStabInclination(float verStabInclination) {
        this.verStabInclination = verStabInclination;
    }
    
    public float getTyreRadius(){
    	return this.getConfig().getTyreRadius();
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    
    public void updateAirplane(float time){
        this.getForce().UpdateForce();
        //System.out.println("Total Y-force: " + (480*9.81-this.getForce().getTotalLift().transform(this.getHeading(), this.getPitch(), this.getRoll()).getY()));
        this.setElapsedTime(this.getElapsedTime()+time);

        setAcceleration(getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll()).constantProduct(1/getTotalMass()).checkAndNeglect(neglectValue));
        setVelocity(getVelocity().add(getAcceleration().constantProduct(time).checkAndNeglect(neglectValue)));
    	setCalcCoordinates(getCalcCoordinates().add(getVelocity().constantProduct(time))); // TODO: put back

        //getForce().getTotalForce().printVector("voor transform ");
        //Vector totalF = getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll());
        //totalF.printVector("na transform");
        
        setAngularAcceleration(getForce().getTotalMoment().applyInertiaTensor(this.getForce().getInverseInertia()).checkAndNeglect(neglectValue).transform(getHeading(), getPitch(), getRoll()));
        setAngularVelocity(getAngularVelocity().add(getAngularAcceleration().constantProduct(time)).checkAndNeglect(neglectValue));
    	
        setPitch(getPitch() + getAngularVelocity().inverseTransform(getHeading(), 0, 0).getX()*time);
    	setRoll(getRoll() + getAngularVelocity().inverseTransform(getHeading(), getPitch(), getRoll()).getZ()*time);
    	setHeading(getHeading() + getAngularVelocity().getY()*time);

        //Vector totalM = getForce().getTotalMoment().applyInertiaTensor(this.getForce().getInverseInertia());
        //totalM.printVector("totalm ");
        //getAngularVelocity().printVector("angleacc");



//        System.out.println("time" + time);
//        System.out.println("Coordinates: " + getCalcCoordinates().getX() + " " + getCalcCoordinates().getY() + " " + getCalcCoordinates().getZ());
//        System.out.println("Angular velocity: " + getAngularVelocity().getX() + " " + getAngularVelocity().getY() + " " + getAngularVelocity().getZ());
//        System.out.println("Moment: " + getForce().getTotalMoment().getX() + " " + getForce().getTotalMoment().getY() + " " + getForce().getTotalMoment().getZ());
        
//        System.out.println("pitch " + pitch);
//        this.getForce().getTotalForce().transform(heading, pitch, roll).printVector("force");
//        this.getAcceleration().printVector("acc ");
 //       this.getVelocity().printVector("vel ");
//        System.out.println("h-p-r"+ this.getHeading() + " " + this.getPitch() + " " + this.getRoll());
////        System.out.println("");
//        this.getForce().getTotalGravityForce().transform(heading, pitch, roll).printVector("gravity");
//        this.getForce().getLeftWingLift().transform(heading, pitch, roll).printVector("leftlift");
//       System.out.println("incl: "+ this.getLeftWingInclination());
//       System.out.println("Left wing: " + this.getLeftWingInclination());
    	//System.out.println("Back left normal:"+this.getForce().getLeftRearWheelNormalForce());
    	//System.out.println("Back right normal:"+this.getForce().getRightRearWheelNormalForce());

    	//System.out.println("Y coordinates wheels: "+ (this.getCalcCoordinates().getY()-this.getConfig().getWheelY()));
    	//System.out.println("Velocity: " + getVelocity().getX() + " " + getVelocity().getY() + " " + getVelocity().getZ());
        //System.out.println("Normal Force: "+ this.getForce().getTotalWheelNormalForce());

    	
    }

    public float getGravityConstant(){
        return this.getConfig().getGravity();
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public boolean isManualControlEnabled(){
        return this.manualControl;
    }
    
    public void setManualControl(boolean control){
        this.manualControl = control;
        if(control == true){
            this.getForce().setThrust(0);
            this.setLeftWingInclination(0);
            this.setRightWingInclination(0);
            this.setHorStabInclination(0);
            this.setVerStabInclination(0);
        }
    }
    
    public void toggleManualControl(){
        this.setManualControl(!this.manualControl);
    }
    
    public AutopilotConfig getConfig() {
        return this.config;
    }
    
    public void setConfig(AutopilotConfig config) {
        this.config = config; 
    }
    
    public void readAutopilotOutputs(AutopilotOutputs autopilotOutputs){
        if(this.isManualControlEnabled()){
            return;
        }
        this.getForce().setThrust(autopilotOutputs.getThrust());
        this.getForce().setBreakForces(autopilotOutputs.getFrontBrakeForce(), autopilotOutputs.getLeftBrakeForce(), 
                autopilotOutputs.getRightBrakeForce());
        this.setLeftWingInclination(autopilotOutputs.getLeftWingInclination());
        this.setRightWingInclination(autopilotOutputs.getRightWingInclination());
        this.setHorStabInclination(autopilotOutputs.getHorStabInclination());
        this.setVerStabInclination(autopilotOutputs.getVerStabInclination());
    }
    
    void setImage(byte[] imageArray){
        this.image = imageArray;
    }

    byte[] getImage(){
        return this.image;
    }

    public AutopilotInputs getAutopilotInputs(){
        return new AutopilotInputs() {
            @Override

            public byte[] getImage() {
                return Aircraft.this.getImage();
            }

            @Override
            public float getX() {
                return Aircraft.this.getCalcCoordinates().getX();
            }

            @Override
            public float getY() {
                return Aircraft.this.getCalcCoordinates().getY();
            }

            @Override
            public float getZ() {
                return Aircraft.this.getCalcCoordinates().getZ();
            }

            @Override
            public float getHeading() {
                return Aircraft.this.getHeading();
            }

            @Override
            public float getPitch() {
                return Aircraft.this.getPitch();
            }

            @Override
            public float getRoll() {
                return Aircraft.this.getRoll();
            }

            @Override
            public float getElapsedTime() {
                return Aircraft.this.getElapsedTime();
            }
        };
    }

    public AutopilotOutputs getAutopilotOutputs(){
        return new AutopilotOutputs() {
        	  @Override
        	    public float getThrust() {
        	        return Aircraft.this.thrust;
        	    }

        	    @Override
        	    public float getLeftWingInclination() {
        	        return Aircraft.this.leftWingInclination;
        	    }

        	    @Override
        	    public float getRightWingInclination() {
        	        return Aircraft.this.rightWingInclination;
        	    }

        	    @Override
        	    public float getHorStabInclination() {
        	        return Aircraft.this.horStabInclination;
        	    }

        	    @Override
        	    public float getVerStabInclination() {
        	        return Aircraft.this.verStabInclination;
        	    }

            @Override
            public float getFrontBrakeForce() {
                return 0; // TODO: implement
            }

            @Override
            public float getLeftBrakeForce() {
                return 0; // TODO: implement
            }

            @Override
            public float getRightBrakeForce() {
                return 0; // TODO: implement
            }
        };
    }

    
}

