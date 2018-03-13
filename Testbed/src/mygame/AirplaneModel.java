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
        super("ColorBox", new RegularBox(x, y, z));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        this.setMaterial(mat);
    }
}

public class AirplaneModel extends Node{

    private AssetManager assetManager;

    // TODO: use constants from autopilot config
    public static float PLANE_LENGTH = 8; // CALCULATED FROM CONSTANTS
    public static float PLANE_HEIGHT = 1;
    public static float PLANE_WIDTH = 2;

    public static float TAIL_SIZE = 6; // Length between mass centre and tail fin // FROM CONSTANTS
    public static float TAIL_FIN_HEIGHT = 1; // FROM CONSTANTS
    public static float TAIL_FIN_WIDTH = 0.2f;
    public static float TAIL_FIN_LENGTH = 0.5f;

    public static float WING_HEIGHT = 0.2f;
    public static float WING_WIDTH = 7.4f; // FROM CONSTANTS
    public static float WING_LENGTH = 2; // Wing depth

    public static float REAR_WHEEL_X = 1.22f; // FROM CONSTANTS
    public static float WHEEL_Y = -1.52f; // FROM CONSTANTS
    public static float REAR_WHEEL_Z = 1f; // FROM CONSTANTS
    public static float FRONT_WHEEL_Z = -1.5f; // FROM CONSTANTS

    public static float PLANE_TAIL_MASS_OFFSET = PLANE_LENGTH - TAIL_SIZE; // Difference between the front of the plane and the mass centre of the plane


    /**
     * The airplane's center of mass coincides with the node's origin (0, 0, 0)
     * @param manager
     */
    public AirplaneModel(AssetManager manager){
        this.assetManager = manager;

        // Body
        MetalBox body = new MetalBox(PLANE_WIDTH, PLANE_HEIGHT, PLANE_LENGTH, this.assetManager);
        body.setLocalTranslation(0, 0, PLANE_TAIL_MASS_OFFSET);
        attachChild(body);
        // Tail fin
        MetalBox tailFin = new MetalBox(TAIL_FIN_WIDTH, TAIL_FIN_HEIGHT, TAIL_FIN_LENGTH, this.assetManager);
        tailFin.setLocalTranslation(0, PLANE_HEIGHT, TAIL_SIZE-TAIL_FIN_LENGTH/2);
        attachChild(tailFin);
        // Left wing
        MetalBox leftWing = new MetalBox(WING_WIDTH, WING_HEIGHT, WING_LENGTH, this.assetManager);
        leftWing.setLocalTranslation(-(PLANE_WIDTH /2), 0, 0);
        attachChild(leftWing);
        // Right wing
        MetalBox rightWing = new MetalBox(WING_WIDTH, WING_HEIGHT, WING_LENGTH, this.assetManager);
        rightWing.setLocalTranslation((PLANE_WIDTH /2), 0, 0);
        attachChild(rightWing);

        // Rear wheel 1
        ColorBox rearWheel1 = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        rearWheel1.setLocalTranslation(REAR_WHEEL_X, WHEEL_Y, REAR_WHEEL_Z);
        attachChild(rearWheel1);
        // Rear wheel 2
        ColorBox rearWheel2 = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        rearWheel2.setLocalTranslation(-REAR_WHEEL_X, WHEEL_Y, REAR_WHEEL_Z);
        attachChild(rearWheel2);
        // Front wheel
        ColorBox frontWheel = new ColorBox(0.3f, 0.3f, 0.3f, this.assetManager, ColorRGBA.Black);
        frontWheel.setLocalTranslation(0, WHEEL_Y, FRONT_WHEEL_Z);
        attachChild(frontWheel);

        ColorBox modelCenter = new ColorBox(WING_WIDTH, 0.3f, 0.3f, this.assetManager, ColorRGBA.Orange); // origin
        attachChild(modelCenter);

    }

}
