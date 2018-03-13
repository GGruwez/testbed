package mygame.visualcomponents;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

public class ColorCylinder extends Geometry {
    public ColorCylinder(float radius, float height, boolean closed, AssetManager assetManager, ColorRGBA color){
        super("ColorCylinder", new Cylinder(20, 20, radius, height, closed));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        this.setMaterial(mat);
    }
}