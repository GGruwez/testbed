package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import p_en_o_cw_2017.AutopilotConfig;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import p_en_o_cw_2017.AutopilotInputs;
import p_en_o_cw_2017.AutopilotOutputs;

public class Aircraft extends Node {
    
    private Vector coordinates;
    private Vector velocity;
    private Vector acceleration = new Vector(0, 0, 0);
    private Force forces;
    private float pitch;
    private float roll;
    private float heading;
    private Vector wingx;
    private Vector tailSize;
    private Vector angularAcceleration;
    private Vector angularVelocity;
    private World world;
    private AutopilotConfig config;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float elapsedTime;
    
    private Geometry aircraftGeometry;
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
    public Aircraft(String name, Mesh mesh, float x, float y, float z, float xVelocity, float yVelocity, float zVelocity,
            float mass, float thrust, float leftWingInclination,float rightWingInclination,
            float horStabInclination, float verStabInclination) {   
        
        this.aircraftGeometry = new Geometry(name, mesh);
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
        this.setCoordinates(new Vector(x, y, z));
        this.setVelocity(new Vector(xVelocity, yVelocity, zVelocity));
    }
    
   
    
    public Geometry getAircraftGeometry(){
        return this.aircraftGeometry;
    }
    
    public Camera getCamera(){
        return this.aircraftCamera;
    }

    public Vector getCoordinates(){
        return this.coordinates;
    }	
    
    public void move(Vector v){
        this.setLocalTranslation(this.coordinates.getX() + (float)v.getX(), this.coordinates.getY() + (float)v.getY(), this.coordinates.getZ() + (float)v.getZ());
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z){
        this.coordinates = new Vector(x, y, z);
        super.setLocalTranslation(x, y, z);
    }

    public void setCoordinates(Vector coordinates){
        this.setLocalTranslation(coordinates.getX(), coordinates.getY(), coordinates.getZ());
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
    	this.pitch = pitch;
    }
    
    public float getRoll(){
    	return this.roll;
    }
    
    public void setRoll(float roll){
    	this.roll = roll;
    }
    
    public float getHeading(){
    	return this.heading;
    }
    
    public void setHeading(float heading){
    	this.heading = heading;
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
    	return this.wingx;
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
    	setCoordinates(getCoordinates().add(getVelocity().constantProduct(time)));
    	setVelocity(getVelocity().add(getAcceleration().constantProduct(time)));
//    	setAcceleration(getForce().getTotalForce().transform(getHeading(), getPitch(), getRoll()).constantProduct(1/getTotalMass()));
//
//    	setPitch(getPitch() + getAngularVelocity().getX());
//    	setRoll(getRoll() + getAngularVelocity().getZ());
//    	setHeading(getHeading() + getAngularVelocity().getY());
//    	setAngularVelocity(getAngularVelocity().add(getAngularAcceleration().constantProduct(time)));

//    	setAngularAcceleration(getForce().getTotalMoment().transform(heading,pitch,roll).applyTraagheidsmatrix());

        this.setElapsedTime(this.getElapsedTime()+time);
    }

    public void setWorld(World world) {
        this.world = world;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public AutopilotConfig getConfig() {
        return this.config;
    }
    
    public void setConfig(AutopilotConfig config) {
        this.config = config; 
    }

    public void readAutopilotOutputs(AutopilotOutputs autopilotOutputs){
        this.getForce().setThrust(autopilotOutputs.getThrust());
        this.setLeftWingInclination(autopilotOutputs.getLeftWingInclination());
        this.setRightWingInclination(autopilotOutputs.getRightWingInclination());
        this.setHorStabInclination(autopilotOutputs.getHorStabInclination());
        this.setVerStabInclination(autopilotOutputs.getVerStabInclination());
    }

    public AutopilotInputs getAutopilotInputs(){
        return new AutopilotInputs() {
            @Override
            public byte[] getImage() {
                return new byte[0]; // TODO: get image
            }

            @Override
            public float getX() {
                return Aircraft.this.getCoordinates().getX();
            }

            @Override
            public float getY() {
                return Aircraft.this.getCoordinates().getY();
            }

            @Override
            public float getZ() {
                return Aircraft.this.getCoordinates().getZ();
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

