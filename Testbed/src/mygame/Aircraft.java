package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import java.util.ArrayList;

public class Aircraft extends Geometry {
    
    private float x;
    private float y;
    private float z;
    
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
            float horStabInclination, float verStabInclination ){
        super(name, mesh);

        this.setLocalTranslation(x, y, z);
    }

    public ArrayList getCoordinates(){
        ArrayList coordinates = new ArrayList();
        coordinates.add(this.x);
        coordinates.add(this.y);
        coordinates.add(this.z);

        return coordinates;
    }	
    
    @Override
    public void setLocalTranslation(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        super.setLocalTranslation(x, y, z);
    }
		
}

