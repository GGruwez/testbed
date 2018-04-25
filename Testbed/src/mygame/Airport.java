
package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import mygame.visualcomponents.RegularBox;

public class Airport extends Node{
    
    private int W;
    private int L;
    private float x;
    private float z;
    private int airportID;
    private float surface;
    private float location;
    private World world;
    private BatchNode batchNode;
    
    public Airport(int W, int L, int ID, float x,  float z, World world) {
        this.W = W; 
        this.L = L;
        this.x=x;
        this.z=z;
        this.calculateSurface();
        this.airportID = ID;
        this.world = world;
        
    }
    
    public int getW() {
        return this.W;
    }
    
    public int getL() {
        return this.L;
    }
    
    public int getID() {
        return this.airportID;
    }
    
    
    private void calculateSurface() {
        this.surface = (2*L+W)*W;
    }
    
    public void build() {
        Box gate = new RegularBox(W,2,W);
        Box landingStrip0 = new RegularBox(2*W,0.2f,L);
        Box landingStrip1 = new RegularBox(2*W,0.2f,L);
        Geometry gateG = new Geometry("gate", gate);
        Material mat1 = new Material(world.getCanvas().getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");  
        mat1.setColor("Color", ColorRGBA.Black);
        gateG.setMaterial(mat1);
        Geometry gateG1 = gateG.clone();
        
        Geometry strip0G = new Geometry("strip0",landingStrip0);
        Geometry strip1G = new Geometry("strip1",landingStrip1);
        Material mat2 = new Material(world.getCanvas().getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");  

        mat2.setColor("Color", ColorRGBA.Gray);
        strip0G.setMaterial(mat2);
        strip1G.setMaterial(mat2);
        gateG.setLocalTranslation(x- W/2, 0, z );
        gateG1.setLocalTranslation(x+ W/2,0,z );
        strip0G.setLocalTranslation(x , 0, z+ W/2);
        strip1G.setLocalTranslation(x , 0, z- W/2);
        batchNode = new BatchNode();
        batchNode.attachChild(gateG);
        batchNode.attachChild(gateG1);
        batchNode.attachChild(strip0G);
        batchNode.attachChild(strip1G);
        this.world.getCanvas().getRootNode().attachChild(batchNode);
        
    }
    
    public BatchNode getBatchNode() {
        return this.batchNode;
    }
    
    public float getX() {return x;}
    public float getZ() {return z;}
}
 