package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import mygame.visualcomponents.*;

public class AirplaneModel extends Node{

    private AssetManager assetManager;
    private Aircraft aircraft;

    // TODO: use constants from autopilot config
    public static float PLANE_HEIGHT = 1;
    public static float PLANE_WIDTH = 2;

    public static float TAIL_SIZE = AutopilotConfig.tailSize; // Length between mass centre and tail fin // FROM CONSTANTS
    public static float TAIL_FIN_HEIGHT = 1; // FROM CONSTANTS
    public static float TAIL_FIN_WIDTH = 0.2f;
    public static float TAIL_FIN_LENGTH = 0.5f;

    public static float WING_HEIGHT = 0.2f;
    public static float WING_WIDTH = 7.4f; // FROM CONSTANTS
    public static float WING_LENGTH = 2; // Wing depth

    public static float REAR_WHEEL_X = AutopilotConfig.rearWheelX; // FROM CONSTANTS
    public static float WHEEL_Y = AutopilotConfig.wheelY; // FROM CONSTANTS
    public static float REAR_WHEEL_Z = AutopilotConfig.rearWheelZ; // FROM CONSTANTS
    public static float FRONT_WHEEL_Z = AutopilotConfig.frontWheelZ; // FROM CONSTANTS

    public static float TYRE_RADIUS = AutopilotConfig.tyreRadius; // FROM CONSTANTS


    /**
     * The airplane's center of mass coincides with the node's origin (0, 0, 0)
     * @param manager
     */
    public AirplaneModel(AssetManager manager, Aircraft aircraft){
        this.assetManager = manager;
        this.aircraft = aircraft;

        float planeLength = getPlaneLength();
        float planeTailMassOffset = getPlaneTailMassOffset();

        // Body
        MetalBox body = new MetalBox(PLANE_WIDTH, PLANE_HEIGHT, planeLength, this.assetManager);
        body.setLocalTranslation(0, 0, planeTailMassOffset);
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
        ColorCylinder rearWheel1 = new ColorCylinder(TYRE_RADIUS, TYRE_RADIUS, true, this.assetManager, ColorRGBA.Black);
        rearWheel1.rotate(0, (float)Math.PI/2, 0);
        rearWheel1.setLocalTranslation(REAR_WHEEL_X, WHEEL_Y, REAR_WHEEL_Z);
        attachChild(rearWheel1);
        // Rear wheel 2
        ColorCylinder rearWheel2 = new ColorCylinder(TYRE_RADIUS, TYRE_RADIUS, true, this.assetManager, ColorRGBA.Black);
        rearWheel2.rotate(0, (float)Math.PI/2, 0);
        rearWheel2.setLocalTranslation(-REAR_WHEEL_X, WHEEL_Y, REAR_WHEEL_Z);
        attachChild(rearWheel2);
        // Front wheel
        ColorCylinder frontWheel = new ColorCylinder(TYRE_RADIUS, TYRE_RADIUS, true, this.assetManager, ColorRGBA.Black);
        frontWheel.rotate(0, (float)Math.PI/2, 0);
        float frontWheelZOffset = 0.5f; // Randomly chosen to make the plane look more realistic, by placing the front wheel a bit further under the plane
        frontWheel.setLocalTranslation(0, WHEEL_Y, FRONT_WHEEL_Z + frontWheelZOffset);
        attachChild(frontWheel);

        ColorBox modelCenter = new ColorBox(WING_WIDTH, 0.3f, 0.3f, this.assetManager, ColorRGBA.Orange); // origin
        attachChild(modelCenter);

    }

    protected float getPlaneLength() {
        return -2*aircraft.getEnginePlace().getZ() + TAIL_SIZE + TAIL_FIN_LENGTH;
    }

    public float getPlaneTailMassOffset() {
        return -2*aircraft.getEnginePlace().getZ(); // Difference between the front of the plane and the mass centre of the plane
    }

}
