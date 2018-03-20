package mygame.visualcomponents;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;

public class MetalBox extends ColorBox{
    public MetalBox(float x, float y, float z, AssetManager assetManager){
        super(x, y, z, assetManager, ColorRGBA.DarkGray);
    }
}