package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

class MetalBox extends ColorBox{
    public MetalBox(float x, float y, float z, AssetManager assetManager){
        super(x, y, z, assetManager, ColorRGBA.DarkGray);
    }
}

class ColorBox extends Geometry{
    public ColorBox(float x, float y, float z, AssetManager assetManager, ColorRGBA color){
        super("ColorBox", new Box(x, y, z));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        this.setMaterial(mat);
    }
}

public class AirplaneModel extends Node{

    private AssetManager assetManager;

    public AirplaneModel(AssetManager manager){
        this.assetManager = manager;

        float planeLength = 8; // CALCULATED FROM CONSTANTS
        float planeHeight = 1;
        float planeWidth = 2;

        float tailSize = 6; // Length between mass centre and tail fin // FROM CONSTANTS
        float tailFinHeight = 1; // FROM CONSTANTS
        float tailFinWidth = 0.2f;
        float tailFinLength = 1;

        float wingHeight = 0.2f;
        float wingWidth = 7.4f; // FROM CONSTANTS
        float wingLength = 2; // Wing depth

        float rearWheelX = 1.22f; // FROM CONSTANTS
        float wheelY = -1.52f; // FROM CONSTANTS
        float rearWheelZ = 0.3f; // FROM CONSTANTS
        float frontWheelZ = -2.44f; // FROM CONSTANTS

        float planeTailMassOffset = planeLength - tailSize; // Difference between the front of the plane and the mass centre of the plane

        // Body
        MetalBox body = new MetalBox(planeWidth,planeHeight, planeLength, this.assetManager);
        body.setLocalTranslation(0, 0, planeTailMassOffset);
        attachChild(body);
        // Tail fin
        MetalBox tailFin = new MetalBox(tailFinWidth, tailFinHeight, tailFinLength, this.assetManager);
        tailFin.setLocalTranslation(0, 2*planeHeight, tailSize + planeTailMassOffset);
        attachChild(tailFin);
        // Left wing
        MetalBox leftWing = new MetalBox(wingWidth, wingHeight, wingLength, this.assetManager);
        leftWing.setLocalTranslation(-(wingWidth + planeWidth/2), 0, 0);
        attachChild(leftWing);
        // Right wing
        MetalBox rightWing = new MetalBox(wingWidth, wingHeight, wingLength, this.assetManager);
        rightWing.setLocalTranslation((wingWidth + planeWidth/2), 0, 0);
        attachChild(rightWing);

        // Rear wheel 1
        ColorBox rearWheel1 = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        rearWheel1.setLocalTranslation(rearWheelX, wheelY, rearWheelZ + planeTailMassOffset);
        attachChild(rearWheel1);
        // Rear wheel 2
        ColorBox rearWheel2 = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        rearWheel2.setLocalTranslation(-rearWheelX, wheelY, rearWheelZ + planeTailMassOffset);
        attachChild(rearWheel2);
        // Front wheel
        ColorBox frontWheel = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        frontWheel.setLocalTranslation(0, wheelY, frontWheelZ + planeTailMassOffset);
        attachChild(frontWheel);

        ColorBox modelCenter = new ColorBox(wingWidth, 0.3f, 0.3f, this.assetManager, ColorRGBA.Green); // origin
        attachChild(modelCenter);

    }

}
