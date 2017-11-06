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
    private Vector goal;

    private Camera chaseCam;
    private CameraNode chaseCamNode;
    private Camera topDownCam;
    private CameraNode topDownCamNode;
    private Camera sideCam;
    private CameraNode sideCamNode;

    private SimpleApplication app;

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
        if (this.isSimulating()) {
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

    public void generateCube(float x, float y, float z){
        Box b = new Box(1, 1, 1);
        Geometry cube = new Geometry("", b);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        cube.setMaterial(mat);
        cube.setLocalTranslation(x, y, z);
        app.getRootNode().attachChild(cube);
    }

}
