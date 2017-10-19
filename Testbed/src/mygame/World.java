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
import p_en_o_cw_2017.AutopilotInputs;
import p_en_o_cw_2017.AutopilotInputsWriter;
import p_en_o_cw_2017.AutopilotOutputs;
import p_en_o_cw_2017.AutopilotOutputsReader;

public class World {
    
    private DataOutputStream outstream;
    private DataInputStream instream;
    private Aircraft aircraft;
    
    public World() {
        byte[] inbuf = new byte[1000];
        this.instream = new DataInputStream(new ByteArrayInputStream(inbuf));
        this.outstream = new DataOutputStream(new ByteArrayOutputStream());
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
    
    public void evolve(float dt) throws IOException {
        AutopilotOutputs input = AutopilotOutputsReader.read(this.getInputStream());
        this.getAircraft().readAutopilotOutputs(input);
        this.getAircraft().updateAirplane(dt);
        AutopilotInputs output = this.getAircraft().getAutopilotInputs();
        AutopilotInputsWriter.write(this.getOutputStream(), output);
    }
    
}
