/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author Stien
 */
public class AutopilotConfig implements interfaces.AutopilotConfig {
    
    public static float gravity = 9.81f;
    public static float wingX = 4.2f;
    public static float tailSize = 4.2f;
    public static float engineMass = 180f;
    public static float wingMass = 100f;
    public static float tailMass = 100f;
    public static float maxThrust = 2000;
    public static float maxAOA = (float) Math.PI/12;
    public static float wingLiftSlope = 10f;
    public static float horStabLiftSlope = 5f;
    public static float verStabLiftSlope = 5f;

    public static float horAngleOfView = 120;
    public static float verAngleOfView = 120;
    public static int nbColumns = 200;
    public static int nbRows = 200;
    	
	public static String droneID = "PREDATOR";
    public static float wheelY = -1.52f; //coordinates
    public static float frontWheelZ = -1.5f; //coordinates
    public static float rearWheelZ = 1f; //coordinates
    public static float rearWheelX = 1.22f; //coordinates
    public static float tyreSlope = 950f;
    public static float dampSlope = 15000f;
    public static float tyreRadius = 0.2f;
    public static float rMax = 100f;    //maximal breaking power on each wheel
    public static float fcMax = 0.01f;  //maximal friction coefficient for rear wheels
    public static float horizontalAngleOfView;
    public static float verticalAngleOfView;
   

    @Override
    public float getGravity() {
        return this.gravity;    
    }

    @Override
    public float getWingX() {
        return this.wingX;
    }

    @Override
    public float getTailSize() {
        return this.tailSize;
    }

    @Override
    public float getEngineMass() {
        return this.engineMass;
    }

    @Override
    public float getWingMass() {
        return this.wingMass;
    }

    @Override
    public float getTailMass() {
        return this.tailMass;
    }

    @Override
    public float getMaxThrust() {
        return this.maxThrust;
    }

    @Override
    public float getMaxAOA() {
        return this.maxAOA;
    }

    @Override
    public float getWingLiftSlope() {
        return this.wingLiftSlope;
    }

    @Override
    public float getHorStabLiftSlope() {
        return this.horStabLiftSlope;
    }

    @Override
    public float getVerStabLiftSlope() {
        return this.verStabLiftSlope;
    }

    @Override
    public float getHorizontalAngleOfView() {
        return this.horAngleOfView;
    }

    @Override
    public float getVerticalAngleOfView() {
        return this.verAngleOfView;
    }

    @Override
    public int getNbColumns() {
        return this.nbColumns;
    }

    @Override
    public int getNbRows() {
        return this.nbRows;
    }
    
    public String getDroneID() {
        return this.droneID;    
    }
    
    public float getWheelY() {
        return this.wheelY;    
    }
    
    public float getFrontWheelZ() {
        return this.frontWheelZ;    
    }
    
    public float getRearWheelZ() {
        return this.rearWheelZ;    
    }
    
    public float getRearWheelX() {
        return this.rearWheelX;    
    }
   
    public float getTyreSlope() {
        return this.tyreSlope;    
    }
    
    public float getDampSlope() {
        return this.dampSlope;    
    }
    
    public float getTyreRadius() {
        return this.tyreRadius;    
    }

    
    public float getRMax() {
        return this.rMax;
    }

    
    public float getFcMax() {
        return this.fcMax;
    }
    
}
