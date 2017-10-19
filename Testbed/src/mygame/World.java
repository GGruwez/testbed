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
import p_en_o_cw_2017.AutopilotConfigWriter;
import p_en_o_cw_2017.AutopilotInputs;
import p_en_o_cw_2017.AutopilotInputsWriter;
import p_en_o_cw_2017.AutopilotOutputs;
import p_en_o_cw_2017.AutopilotOutputsReader;
import autopilot.Autopilot;

public class World {
    
    private DataOutputStream outstream;
    private DataInputStream instream;
    private Aircraft aircraft;
    private AutopilotConfigWriter configwriter = new AutopilotConfigWriter();
    private AutopilotInputsWriter inwriter = new AutopilotInputsWriter();
    private AutopilotOutputsReader outreader = new AutopilotOutputsReader();
    private Autopilot autopilot;

    public World() {
        byte[] inbuf = new byte[1000];
        this.instream = new DataInputStream(new ByteArrayInputStream(inbuf));
        this.outstream = new DataOutputStream(new ByteArrayOutputStream());
        this.autopilot = new Autopilot(null); // TODO: set config
    }
    
    public DataInputStream getInputStream() {
        return this.instream;
    }
    
    public DataOutputStream getOutputStream() {
        return this.outstream;
    }
    
    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
        aircraft.setWorld(this);
    }
    
    public Aircraft getAircraft() {
        return this.aircraft;
    }
    
    public AutopilotConfigWriter getConfigWriter() {
        return this.configwriter;
    }
    
    public AutopilotInputsWriter getOutputWriter() {
        return this.inwriter;
    }
    
    public AutopilotOutputsReader getInputReader() {
        return this.outreader;
    }
    
    public void evolve(float dt) throws IOException {
        AutopilotInputs output = this.getAircraft().getAutopilotInputs();
        this.getOutputWriter().write(this.getOutputStream(), output);
        autopilot.fillStreamWithOutput(this.getInputStream(), this.getOutputStream());
        AutopilotOutputs input = this.getInputReader().read(this.getInputStream());
        this.getAircraft().readAutopilotOutputs(input);
        this.getAircraft().updateAirplane(dt);
    }
    
}
