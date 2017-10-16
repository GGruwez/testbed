/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import p_en_o_cw_2017.AutopilotConfigReader;
import p_en_o_cw_2017.AutopilotConfigWriter;
import p_en_o_cw_2017.AutopilotInputsReader;
import p_en_o_cw_2017.AutopilotInputsWriter;
import p_en_o_cw_2017.AutopilotOutputsReader;
import p_en_o_cw_2017.AutopilotOutputsWriter;

public class World {
    
    private ByteArrayInputStream instream;
    private ByteArrayOutputStream outstream;
    private Aircraft aircraft;
    private AutopilotConfigReader configreader = new AutopilotConfigReader();
    private AutopilotConfigWriter configwriter = new AutopilotConfigWriter();
    private AutopilotInputsReader inreader = new AutopilotInputsReader();
    private AutopilotInputsWriter inwriter = new AutopilotInputsWriter();
    private AutopilotOutputsReader outreader = new AutopilotOutputsReader();
    private AutopilotOutputsWriter outwriter = new AutopilotOutputsWriter();
    
    public World() {
        byte[] inbuf = new byte[1000];
        this.instream = new ByteArrayInputStream(inbuf);
        this.outstream = new ByteArrayOutputStream();
    }
    
    public ByteArrayInputStream getInputStream() {
        return this.instream;
    }
    
    public ByteArrayOutputStream getOutputStream() {
        return this.outstream;
    }
    
    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
        aircraft.setWorld(this);
    }
    
    public Aircraft getAircraft() {
        return this.aircraft;
    }
    
    public AutopilotConfigReader getConfigReader() {
        return this.configreader;
    }
    
    public AutopilotConfigWriter getConfigWriter() {
        return this.configwriter;
    }
    
    public AutopilotInputsReader getInputReader() {
        return this.inreader;
    }
    
    public AutopilotInputsWriter getInputWriter() {
        return this.inwriter;
    }
    
    public AutopilotOutputsReader getOutputReader() {
        return this.outreader;
    }
    
    public AutopilotOutputsWriter getOutputWriter() {
        return this.outwriter;
    }
    
}
