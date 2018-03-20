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
public class AutopilotOutputs implements p_en_o_cw_2017.AutopilotOutputs{
    
    public float thrust;
    public float leftWingInclination;
    public float rightWingInclination;
    public float horStabInclination;
    public float verStabInclination;
    public float frontBrakeForce;
    


    @Override
    public float getThrust() {
        return this.thrust;
    }

    @Override
    public float getLeftWingInclination() {
        return this.leftWingInclination;
    }

    @Override
    public float getRightWingInclination() {
        return this.rightWingInclination;
    }

    @Override
    public float getHorStabInclination() {
        return this.horStabInclination;
    }

    @Override
    public float getVerStabInclination() {
        return this.verStabInclination;
    }
    
    @Override
    public float getFrontBrakeForce() {
        return 
    }

    @Override
    public float getLeftBrakeForce() {
        return 
    }

    @Override
    public float getRightBrakeForce() {
        return 
    }
}
