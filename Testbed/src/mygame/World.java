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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;
import interfaces.Autopilot;
import interfaces.AutopilotFactory;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class World {

    private static long SIMULATION_PERIOD = 10; // Simulation period in milliseconds, determines how fast autopilot calculations happen

    private Aircraft aircraft;
    private Autopilot autopilot;
    private boolean simulation;
    private boolean paused = true;
    private Vector goal;

    private Camera chaseCam;
    private CameraNode chaseCamNode;
    private Camera topDownCam;
    private CameraNode topDownCamNode;
    private Camera sideCam;
    private CameraNode sideCamNode;

    private MainSwingCanvas mainSwingCanvas;
    
    private ColorRGBA[] usedColors;
    private HashMap<Cube,Vector> cubePositions;
    private Set<Cube> cubesInWorld;
    

    public World(MainSwingCanvas app) {
        this.autopilot = AutopilotFactory.createAutopilot();

        // Chase camera
        this.chaseCam = new Camera(200, 200);
        this.chaseCam.setFrustumPerspective(120, 1, 1, 1000);
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

        this.mainSwingCanvas = app;
        this.cubesInWorld = new HashSet<Cube>();
        this.cubePositions = new HashMap<Cube, Vector>();

        // Simulated evolve
        // Run autopilot every 10 milliseconds
        Timer simulationTimer = new Timer(true);
        TimerTask simulationTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    evolveAutopilot((float) SIMULATION_PERIOD / 1000);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Evolve failed", ex);
                }
            }
        };
        simulationTimer.scheduleAtFixedRate(simulationTimerTask, 0, SIMULATION_PERIOD);

        this.generateCubes(this.readFile("cubePositions.txt"));
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

    private void evolveAutopilot(float dt){
        if (this.isSimulating() && !this.isPaused()) {
            AutopilotInputs autopilotInputs = this.getAircraft().getAutopilotInputs();
            AutopilotOutputs autopilotOutputs = getAutopilot().timePassed(autopilotInputs);
            this.getAircraft().readAutopilotOutputs(autopilotOutputs);
            this.getAircraft().updateAirplane(dt);
        }
    }
    
    public void evolve(float dt) throws IOException {
        if (this.isSimulating() && !this.isPaused()) {
            // Update visual position of aircraft
            this.getAircraft().updateVisualCoordinates();
            this.getAircraft().updateVisualRotation();
            // Aircraft's calc coordinates and actual visual position coordinates are now the same

            // Camera's
            this.chaseCam.resize(200, 200, false);
            this.topDownCam.resize(200, 200, false);
            this.sideCam.resize(200, 200, false);
            Vector newChaseCamPosition = this.getAircraft().getCalcCoordinates().inverseTransform(0, 0,0 ).add(new Vector(0, 0, 6)).transform(0,0,0);
            this.chaseCamNode.setLocalTranslation(newChaseCamPosition.getX(), newChaseCamPosition.getY(), newChaseCamPosition.getZ());
            Vector aircraftCoordinates = this.getAircraft().getCalcCoordinates();
            this.chaseCamNode.lookAt(new Vector3f(aircraftCoordinates.getX(), aircraftCoordinates.getY(), aircraftCoordinates.getZ()), Vector3f.UNIT_Y);

            // CustomView camera updating
            this.mainSwingCanvas.chaseCameraCustomView.updateCamera(cv -> {
                Vector newChaseCamPosition1 = getAircraft().getCalcCoordinates().inverseTransform(0, 0,0 ).add(new Vector(0, 0, 6)).transform(0,0,0);
                cv.getCameraNode().setLocalTranslation(newChaseCamPosition1.getX(), newChaseCamPosition1.getY(), newChaseCamPosition1.getZ());
                Vector aircraftCoordinates1 = getAircraft().getCalcCoordinates();
                cv.getCameraNode().lookAt(new Vector3f(aircraftCoordinates1.getX(), aircraftCoordinates1.getY(), aircraftCoordinates1.getZ()), Vector3f.UNIT_Y);
            });
        }

        Cube cubeToRemove = null;
        for(Cube cube:this.getCubesInWorld()) {
            Vector cubePos = this.getCubePositions().get(cube);
            if(this.getAircraft().getCalcCoordinates().calculateDistance(cubePos)<=4) {
                cubeToRemove = cube;
                this.getCubePositions().remove(cube);
                cube.destroy();
            }
        }
        this.getCubesInWorld().remove(cubeToRemove);

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
        Cube cube = new Cube(1, 1, 1, color, mainSwingCanvas.getAssetManager(), mainSwingCanvas.getRootNode());
        this.getCubesInWorld().add(cube);
        this.getCubePositions().put(cube, new Vector(x,y,z));
        }
    
    public void generateTestBeam(int n){
        this.usedColors = new ColorRGBA[n];
        for(int i=0; i<n; i++) {
            float z = (float)i/(float)(n-1)*(-90)-10;
            float x = (float) Math.random()*20-10;
            float y = (float) Math.random()*10;
            ColorRGBA color = ColorRGBA.randomColor();
            while (colorIsUsed(color)) color = ColorRGBA.randomColor();
            this.getUsedColors()[i] = color;
            Cube cube = new Cube(x,y,z,color, mainSwingCanvas.getAssetManager(), mainSwingCanvas.getRootNode());
            this.getCubesInWorld().add(cube);
            this.getCubePositions().put(cube, new Vector(x,y,z));
        }           
    }
    
    public void generateRandomCubes(int n) {
        this.usedColors = new ColorRGBA[n];
        for(int i=1; i<n+1; i++) {
            float z = i*-40;
            float x = (float) Math.random()*10;
            float y = (float) Math.random()*(10-x);
            ColorRGBA color = ColorRGBA.randomColor();
            while (colorIsUsed(color)) color = ColorRGBA.randomColor();
            this.getUsedColors()[i-1] = color;
            Cube cube = new Cube(x,y,z,color, mainSwingCanvas.getAssetManager(), mainSwingCanvas.getRootNode());
            this.getCubesInWorld().add(cube);
            this.getCubePositions().put(cube, new Vector(x,y,z));
        }
    }
    
    public void generateCubes(Vector[] positions) {
        for(int i=0; i<positions.length; i++) {
            Vector currentPos = positions[i];
            ColorRGBA color = ColorRGBA.randomColor();
            while (colorIsUsed(color)) color = ColorRGBA.randomColor();
            this.getUsedColors()[i] = color;
            Cube cube = new Cube(currentPos.getX(), currentPos.getY(), currentPos.getZ(), color, mainSwingCanvas.getAssetManager(), mainSwingCanvas.getRootNode());
            this.getCubesInWorld().add(cube);
            this.getCubePositions().put(cube, new Vector(currentPos.getX(),currentPos.getY(), currentPos.getZ()));
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
        this.usedColors = new ColorRGBA[pos.length];
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
    
    public Set<Cube> getCubesInWorld() {
        return this.cubesInWorld;
    }
    
    public HashMap<Cube,Vector> getCubePositions() {
        return this.cubePositions;
    }
    
    public void generateCylinder() {
        this.generateRandomCubes(5);
    }
    
    public void writeFile(String filename) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for(Cube cube:cubesInWorld) {
                Vector pos = this.cubePositions.get(cube);
                String empty = " ";
                String line = String.valueOf(pos.getX())+empty + String.valueOf(pos.getY())+empty+String.valueOf(pos.getZ()) + "\n";
                writer.write(line);  
            }
            writer.close();
        }
        catch(IOException e) {}
       
       
    }
}
