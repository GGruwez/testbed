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
    private float wingX = 1;
    private float tailSize = 1;
    private float engineMass = 0.5f;
    private float wingMass = 0.5f;
    private float tailMass = 0.5f;
    private float maxThrust = 40;
    private float maxAOA = (float) Math.PI/5;
    private float wingLiftSlope = 1f;
    private float horStabLiftSlope = 1f;
    private float verStabLiftSlope = 0.1f;
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
