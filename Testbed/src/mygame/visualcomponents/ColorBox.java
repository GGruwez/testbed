package mygame.visualcomponents;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class ColorBox extends Geometry {
    public ColorBox(float x, float y, float z, AssetManager assetManager, ColorRGBA color){
        super("ColorBox", new RegularBox(x, y, z));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        this.setMaterial(mat);
    }
}