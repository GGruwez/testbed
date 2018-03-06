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
    
    private float gravity = 9.81f;
    private float wingX = 7.4f;
    private float tailSize = 6f;
    private float engineMass = 312f;
    private float wingMass = 50f;
    private float tailMass = 100f;
    private float maxThrust = 167f;
    private float maxAOA = (float) Math.PI/12;
    private float wingLiftSlope = 1.97f;
    private float horStabLiftSlope = 1.97f;
    private float verStabLiftSlope = 1.53f;
    private float horAngleOfView = 120;
    private float verAngleOfView = 120;
    private int nbColumns = 200;
    private int nbRows = 200;
    	
	private String droneID = "PREDATOR";
    private float wheelY = -1.52f; //coordinates
    private float frontWheelZ = -2.44f; //coordinates
    private float rearWheelZ = 0.3f; //coordinates
    private float rearWheelX = 1.22f; //coordinates
    private float tyreSlope = 50000f;
    private float dampSlope = 1000f;
    private float tyreRadius = 0.2f;
    private float rMax = 100f;    //maximal breaking power on each wheel
    private float fcMax = 0.01f;  //maximal friction coefficient for rear wheels 
    private float horizontalAngleOfView;
    private float verticalAngleOfView;
   

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
    
    public float getrMax() {
        return this.rMax;    
    }
    
    public float getfcMax() {
        return this.fcMax;    
    }
    
}
