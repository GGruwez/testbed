/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import p_en_o_cw_2017.*;
import autopilot.Autopilot;
import p_en_o_cw_2017.AutopilotOutputs;

public class World {
    
    private DataOutputStream outstream;
    private DataInputStream instream;
    private Aircraft aircraft;
    private Autopilot autopilot;
    private boolean simulation;
    private Vector goal;

    public World() {
        byte[] inbuf = new byte[1000];
        this.instream = new DataInputStream(new ByteArrayInputStream(inbuf));
        this.outstream = new DataOutputStream(new ByteArrayOutputStream());
        this.autopilot = new Autopilot();
    }
    
    public DataInputStream getInputStream() {
        return this.instream;
    }
    
    public DataOutputStream getOutputStream() {
        return this.outstream;
    }
    
    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
        getAircraft().setWorld(this);
        getAutopilot().setConfig(this.getAircraft().getConfig());
    }
    
    public Aircraft getAircraft() {
        return this.aircraft;
    }
    
    public Autopilot getAutopilot() {
        return this.autopilot;
    }
    
    public void evolve(float dt) throws IOException {
        if (this.isSimulating()) {
            AutopilotInputs output = this.getAircraft().getAutopilotInputs();
            AutopilotInputsWriter.write(this.getOutputStream(), output);
            getAutopilot().fillStreamWithOutput(this.getInputStream(), this.getOutputStream());
            AutopilotOutputs input = AutopilotOutputsReader.read(this.getInputStream());
            this.getAircraft().readAutopilotOutputs(input);
            this.getAircraft().updateAirplane(dt);
        }
        double distanceToGoal = Math.sqrt(
            Math.pow(getAircraft().getCoordinates().getX()-getGoal().getX(), 2) +
            Math.pow(getAircraft().getCoordinates().getY()-getGoal().getY(), 2) +
            Math.pow(getAircraft().getCoordinates().getZ()-getGoal().getZ(), 2) );
        if (distanceToGoal<=4) {
            endSimulation();
        }
    }
    
    public void startSimulation() {
        this.simulation = true;
    }
    
    public void endSimulation() {
        this.simulation = false;
    }
    
    public boolean isSimulating() {
        return this.simulation;
    }
    
    public Vector getGoal() {
        return this.goal;
    }
    
    public void setGoal(float x, float y, float z) {
        this.goal = new Vector(x, y, z);
    }
    
}
