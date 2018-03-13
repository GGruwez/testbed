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
    
    private float thrust;
    private float leftWingInclination;
    private float rightWingInclination;
    private float horStabInclination;
    private float verStabInclination;
    private float frontBreakForce;
    private float leftBreakForce;
    private float rightBreakForce;


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
    
    public float getFrontBreakForce(){
    	return this.frontBreakForce;
    }
    
    public float getLeftBreakForce(){
    	return this.frontBreakForce;
    }

    public float getRightBreakForce(){
    	return this.frontBreakForce;
    }
}
