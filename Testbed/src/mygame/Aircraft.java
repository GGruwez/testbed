package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

public class Aircraft extends Geometry {
    
    private float x;
    private float y;
    private float z;
    private float xVelocity;
    private float yVelocity;
    private float zVelocity;
    
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
        super(name, mesh);
        this.setLocalTranslation(x, y, z);
    }

    public Vector getCoordinates(){
        return new Vector(this.x, this.y, this.z);
    }	
    
    @Override
    public void setLocalTranslation(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
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

