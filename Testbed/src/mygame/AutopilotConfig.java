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
    private float wingX = 4.2f;
    private float tailSize = 4.2f;
    private float engineMass = 180f;
    private float wingMass = 100f;
    private float tailMass = 100f;
    private float maxThrust = 2000;
    private float maxAOA = (float) Math.PI/12;
    private float wingLiftSlope = 10f;
    private float horStabLiftSlope = 5f;
    private float verStabLiftSlope = 5f;
    private float horAngleOfView = 120;
    private float verAngleOfView = 120;
    private int nbColums = 200;
    private int nbRows = 200;

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
        return this.nbColums;
    }

    @Override
    public int getNbRows() {
        return this.nbRows;
    }
    
}
