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
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.List;
import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import p_en_o_cw_2017.*;
import autopilot.Autopilot;
import p_en_o_cw_2017.AutopilotOutputs;

public class World {
    
    private DataOutputStream outstream;
    private DataInputStream instream;
    private Aircraft aircraft;
    private Autopilot autopilot;
    private boolean simulation;
    private boolean paused;
    private Vector goal;

    private Camera chaseCam;
    private CameraNode chaseCamNode;
    private Camera topDownCam;
    private CameraNode topDownCamNode;
    private Camera sideCam;
    private CameraNode sideCamNode;

    private SimpleApplication app;
    
    private ColorRGBA[] usedColors;

    public World(SimpleApplication app) {
        byte[] inbuf = new byte[1000000];
        this.instream = new DataInputStream(new ByteArrayInputStream(inbuf));
        this.outstream = new DataOutputStream(new ByteArrayOutputStream());
        this.autopilot = new Autopilot();

        // Chase camera
        this.chaseCam = new Camera(200, 200);
        this.chaseCam.setFrustumPerspective(120,1,1,1000);
        this.chaseCam.setViewPort(4f, 5f, 1f, 2f);
        this.chaseCamNode = new CameraNode("Chase cam Node", this.chaseCam);
        this.chaseCamNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        // Top down camera
        this.topDownCam = new Camera(200, 200);
        this.topDownCam.setFrustumPerspective(120, 1, 1, 1000);
        this.topDownCam.setViewPort(3f, 4f, 1f, 2f);
        this.topDownCamNode = new CameraNode("Top down cam node", this.topDownCam);
        this.topDownCamNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        this.topDownCamNode.setLocalTranslation(0, 30, 0);
        this.topDownCamNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        // Side camera
        this.sideCam = new Camera(200, 200);
        this.sideCam.setFrustumPerspective(120, 1, 1, 1000);
        this.sideCam.setViewPort(3f, 4f, 0f, 1f);
        this.sideCamNode = new CameraNode("Side cam node", this.sideCam);
        this.sideCamNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        this.sideCamNode.setLocalTranslation(-50, 0, 0);
        this.sideCamNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        this.app = app;
        
        this.usedColors = new ColorRGBA[5];
        this.generateTestBeam(5);
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
    }
    
    public Aircraft getAircraft() {
        return this.aircraft;
    }
    
    public Autopilot getAutopilot() {
        return this.autopilot;
    }
    
    public void evolve(float dt) throws IOException {
        if (this.isSimulating() && !this.isPaused()) {
            AutopilotInputs autopilotInputs = this.getAircraft().getAutopilotInputs();
            AutopilotOutputs autopilotOutputs = getAutopilot().timePassed(autopilotInputs);
            this.getAircraft().readAutopilotOutputs(autopilotOutputs);
            this.getAircraft().updateAirplane(dt);

            // Camera's
            this.chaseCam.resize(200, 200, false);
            this.topDownCam.resize(200, 200, false);
            this.sideCam.resize(200, 200, false);
            Vector newChaseCamPosition = this.getAircraft().getCoordinates().inverseTransform(0, 0,0 ).add(new Vector(0, 0, 6)).transform(0,0,0);
            this.chaseCamNode.setLocalTranslation(newChaseCamPosition.getX(), newChaseCamPosition.getY(), newChaseCamPosition.getZ());
            Vector aircraftCoordinates = this.getAircraft().getCoordinates();
            this.chaseCamNode.lookAt(new Vector3f(aircraftCoordinates.getX(), aircraftCoordinates.getY(), aircraftCoordinates.getZ()), Vector3f.UNIT_Y);
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
        this.getAutopilot().simulationStarted(this.getAircraft().getConfig(), this.getAircraft().getAutopilotInputs());
    }
    
    public void endSimulation() {
        this.simulation = false;
        this.getAutopilot().simulationEnded();
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

    public Camera getChaseCam(){
        return this.chaseCam;
    }

    public CameraNode getChaseCamNode(){
        return this.chaseCamNode;
    }

    public Camera getTopDownCam(){
        return this.topDownCam;
    }

    public CameraNode getTopDownCamNode(){
        return this.topDownCamNode;
    }

    public Camera getSideCam(){
        return this.sideCam;
    }

    public CameraNode getSideCamNode(){
        return this.sideCamNode;
    }

    public void generateCube(float x, float y, float z, ColorRGBA color){
        Box b = new Box(1, 1, 1);
        Geometry cube = new Geometry("", b);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        cube.setMaterial(mat);
        cube.setLocalTranslation(x, y, z);
        app.getRootNode().attachChild(cube);
    }
    
    public void generateTestBeam(int n){
        for(int i=0; i<n; i++) {
            float z = (float)i/(float)(n-1)*(-90)-10;
            float x = (float) Math.random()*20-10;
            float y = (float) Math.random()*10;
            ColorRGBA color = ColorRGBA.randomColor();
            while (colorIsUsed(color)) color = ColorRGBA.randomColor();
            this.getUsedColors()[i] = color;
            this.generateCube(x, y, z, color);
        }
                
    }
    
    
    
    public Vector[] readFile(String fileName) {
        ArrayList<Vector> positions = new ArrayList<Vector>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = reader.readLine()) != null) {
                String[] stringValues = line.split(" ");
                float[] values = new float[3];
                for(int i=0; i<3; i++) {
                    values[i] = Float.valueOf(stringValues[i]);
                }
                positions.add(new Vector(values[0],values[1],values[2]));
            }
            reader.close();
        }
        catch(Exception e) {}
        Vector[] pos = new Vector[positions.size()];
        for(int i=0; i<positions.size(); i++) {
            pos[i] = positions.get(i);
        }
        return pos;
    }

    public void pauseSimulation(){
        paused = true;
    }

    public void continueSimulation(){
        paused = false;
    }

    public boolean isPaused(){
        return paused;
    }
    
    public ColorRGBA[] getUsedColors() {return this.usedColors;}
    
    public boolean colorIsUsed(ColorRGBA color) {
        ColorRGBA[] usedColors = this.getUsedColors();
        int n = usedColors.length;
        for(int i=0; i<n; i++) {
            if (usedColors[i] == color) return true;
        }
        return false;
    }

}
