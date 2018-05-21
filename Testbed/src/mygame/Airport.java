
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
    
    public static int W;
    public static int L;
    private float x;
    private float z;
    private int airportID;
    private float surface;
    private float location;
    private World world;
    private BatchNode batchNode;
    private float toRunwayx;
    private float toRunwayz;
    public Airport(int W, int L, int ID, float x,  float z,float tox, float toz, World world) {
        this.W = W; 
        this.L = L;
        this.x=x;
        this.z=z;
        this.calculateSurface();
        this.airportID = ID;
        this.world = world;
        this.toRunwayx = tox;
        this.toRunwayz = toz;
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
        Box gate = new RegularBox(W,0.3f,W);
        Box landingStrip0 = new RegularBox(2*W,0.2f,L);
        Box landingStrip1 = new RegularBox(2*W,0.2f,L);
        Geometry gateG = new Geometry("gate", gate);
        Material mat1 = new Material(world.getCanvas().getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");  
        mat1.setColor("Color", ColorRGBA.Black);
        gateG.setMaterial(mat1);

        Vector nzAxis = new Vector(0,0,-1);
        Vector Airportaxis = new Vector(toRunwayx,0,toRunwayz);
        float bias = nzAxis.angleBetween(Airportaxis);
        if (toRunwayx < 0){
            bias -= Math.PI;
        }

        Geometry gateG1 = gateG.clone();
        
        Geometry strip0G = new Geometry("strip0",landingStrip0);
        Geometry strip1G = new Geometry("strip1",landingStrip1);

        Material mat2 = new Material(world.getCanvas().getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");  

        mat2.setColor("Color", ColorRGBA.Gray);
        strip0G.setMaterial(mat2);
        strip1G.setMaterial(mat2);
        gateG.setLocalTranslation((float) (x+ W/2 * Math.cos(bias)), 0, (float) (z+ W/2 * Math.sin(bias)) );
        gateG1.setLocalTranslation((float) (x- W/2 * Math.cos(bias)), 0, (float) (z- W/2 * Math.sin(bias)) );
        strip0G.setLocalTranslation((float) (x+ W/2 * Math.sin(bias)), 0, (float) (z+ W/2 * Math.cos(bias)) );
        strip1G.setLocalTranslation((float) (x- W/2 * Math.sin(bias)), 0, (float) (z- W/2 * Math.cos(bias)) );

        gateG1.rotate(0,-bias,0);
        gateG.rotate(0,-bias,0);
        strip0G.rotate(0,-bias,0);
        strip1G.rotate(0,-bias,0);
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
    public float getY() {return 0;}
    public float getZ() {return z;}
    
    public Vector getPositionOfGate1() {
        Vector nzAxis = new Vector(0,0,-1);
        Vector Airportaxis = new Vector(toRunwayx,0,toRunwayz);
        float bias = nzAxis.angleBetween(Airportaxis);
        if (toRunwayx < 0){
            bias -= Math.PI;
        }
        return new Vector((float) (x+ W/2 * Math.cos(bias)), 0, (float) (z+ W/2 * Math.sin(bias)) );
    }

    public Vector getPositionOfGate0() {
        Vector nzAxis = new Vector(0,0,-1);
        Vector Airportaxis = new Vector(toRunwayx,0,toRunwayz);
        float bias = nzAxis.angleBetween(Airportaxis);
        if (toRunwayx < 0){
            bias -= Math.PI;
        }
        return new Vector((float) (x- W/2 * Math.cos(bias)), 0, (float) (z- W/2 * Math.sin(bias)) );
    }

    // (centerToRunway0X, centerToRunway0Z) constitutes a unit vector pointing from the center of the airport towards runway 0
    public float getCenterToRunway0X(){ return toRunwayx; } // TODO: check if correct
    public float getCenterToRunway0Z(){ return toRunwayz; } // TODO: check if correct

    public String toString(){
        return getName();
    }

    public String getName(){
        return "Airport " + this.airportID;
    }
}
 