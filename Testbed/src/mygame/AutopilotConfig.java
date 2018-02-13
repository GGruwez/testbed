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
    private float maxThrust = 10f;
    private float maxAOA = (float) Math.PI/12;
    private float wingLiftSlope = 1f;
    private float horStabLiftSlope = 1f;
    private float verStabLiftSlope = 0.1f;
    private float horAngleOfView = 120;
    private float verAngleOfView = 120;
    private int nbColumns = 200;
    private int nbRows = 200;
    	
	private String droneID;
    private float wheelY;
    private float frontWheelZ;
    private float rearWheelZ;
    private float rearWheelX;
    private float tyreSlope;
    private float dampSlope;
    private float tyreRadius;
    private float rMax;     //maximal breaking power on each wheel
    private float fcMax;  //maximal friction coefficient for rear wheels 
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
    
    @Override
    public String getDroneID() {
        return this.droneID;    
    }
    
    @Override
    public float getWheelY() {
        return this.wheelY;    
    }
    
    @Override
    public float getFrontWheelZ() {
        return this.frontWheelZ;    
    }
    
    @Override
    public float getRearWheelZ() {
        return this.rearWheelZ;    
    }
    
    @Override
    public float getRearWheelX() {
        return this.rearWheelX;    
    }
   
    @Override
    public float getTyreSlope() {
        return this.tyreSlope;    
    }
    
    @Override
    public float getDampSlope() {
        return this.dampSlope;    
    }
    
    @Override
    public float getTyreRadius() {
        return this.tyreRadius;    
    }
    
    @Override
    public float getrMax() {
        return this.rMax;    
    }
    
    @Override
    public float getfcMax() {
        return this.fcMax;    
    }
    
}
