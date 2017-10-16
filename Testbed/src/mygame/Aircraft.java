package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

public class Aircraft extends Node {
    
    private Vector coordinates;
    private float xVelocity;
    private float yVelocity;
    private float zVelocity;
    
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
        this.aircraftCamera = new Camera(1024,768);
        this.aircraftCamera.setFrustumPerspective(120,1,1,1000);
        this.aircraftCamera.setViewPort(0.75f, 1.0f, 0.0f, 0.25f);
        this.aircraftCameraNode = new CameraNode("Camera Node", this.aircraftCamera);
        this.aircraftCameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        this.attachChild(this.aircraftGeometry);
        this.attachChild(this.aircraftCameraNode);
        this.aircraftCameraNode.setLocalTranslation(Vector3f.ZERO);
        this.aircraftCameraNode.lookAt(new Vector3f(1,0,0), Vector3f.UNIT_Y);
    }
    
    public Geometry getAircraftGeometry(){
        return this.aircraftGeometry;
    }
    
    public Camera getCamera(){
        return this.aircraftCamera;
    }

    public Vector getCoordinates(){
        return this.coordinates.clone();
    }
    
    public void move(Vector v){
        this.setLocalTranslation(this.coordinates.getX() + (float)v.getX(), this.coordinates.getY() + (float)v.getY(), this.coordinates.getZ() + (float)v.getZ());
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z){
        this.coordinates = new Vector(x, y, z);
        super.setLocalTranslation(x, y, z);
    }
    
    public Vector getVelocity() {
        return new Vector(this.xVelocity, this.yVelocity, this.zVelocity);
    }
    
    public void setVelocity(float x, float y, float z) {
        this.xVelocity = x;
        this.yVelocity = y;
        this.zVelocity = z;
    }
		
}

