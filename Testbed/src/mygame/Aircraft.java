package mygame;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
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
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float elapsedTime;
    private boolean manualControl = false;
    private float NeglectValue  = 0.00001f;
    private byte[] image = new byte[0];
    
    private Spatial aircraftGeometry;
    private Camera aircraftCamera;
    private CameraNode aircraftCameraNode;

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
    public Aircraft(String name, Node model, float x, float y, float z, float xVelocity, float yVelocity, float zVelocity,
            float thrust, float leftWingInclination,float rightWingInclination,
            float horStabInclination, float verStabInclination) {   
        
        model.getChild(0).scale(0.5f, 0.5f, 0.5f);
        model.getChild(0).move(0, 0, 1.2f);
        model.getChild(0).rotate(FastMath.PI, 0, FastMath.PI);
        this.aircraftGeometry = (Spatial) model.getChild(0);
        
        // Plane camera
        this.aircraftCamera = new Camera(200, 200);
        this.aircraftCamera.setFrustumPerspective(120,1,1,1000);
        this.aircraftCamera.setViewPort(4f, 5f, 0f, 1f);
        this.aircraftCameraNode = new CameraNode("Camera Node", this.aircraftCamera);
        this.aircraftCameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        this.attachChild(this.aircraftGeometry);
        this.attachChild(this.aircraftCameraNode);
        this.aircraftCameraNode.setLocalTranslation(Vector3f.ZERO);
        this.aircraftCameraNode.lookAt(new Vector3f(0,0,-1), Vector3f.UNIT_Y); // Front of the plane is in -z direction
        
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

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    
    public void updateAirplane(float time){
        this.getForce().UpdateForce();
        this.setElapsedTime(this.getElapsedTime()+time);

        setAcceleration(getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll()).constantProduct(1/getTotalMass()).checkAndNeglect(NeglectValue));
        setVelocity(getVelocity().add(getAcceleration().constantProduct(time).checkAndNeglect(NeglectValue)));
    	setCalcCoordinates(getCalcCoordinates().add(getVelocity().constantProduct(time))); // TODO: put back

        //getForce().getTotalForce().printVector("voor transform ");
        //Vector totalF = getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll());
        //totalF.printVector("na transform");
        
        setAngularAcceleration(getForce().getTotalMoment().applyInertiaTensor(this.getForce().getInverseInertia()).checkAndNeglect(NeglectValue).transform(getHeading(), getPitch(), getRoll()));
        setAngularVelocity(getAngularVelocity().add(getAngularAcceleration().constantProduct(time)).checkAndNeglect(NeglectValue));
    	Vector angularVel = getAngularVelocity().inverseTransform(getHeading(), getPitch(), getRoll());
        setPitch(getPitch() + angularVel.getX()*time);
    	setRoll(getRoll() + angularVel.getZ()*time);
    	setHeading(getHeading() + angularVel.getY()*time);

        //Vector totalM = getForce().getTotalMoment().applyInertiaTensor(this.getForce().getInverseInertia());
        //totalM.printVector("totalm ");
        //getAngularVelocity().printVector("angleacc");



//        System.out.println("time" + time);
//        System.out.println("Velocity: " + getVelocity().getX() + " " + getVelocity().getY() + " " + getVelocity().getZ());
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

}

