package mygame;

import com.jme3.collision.CollisionResults;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.math.ColorRGBA;

import interfaces.Autopilot;
import interfaces.AutopilotFactory;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class World {

    private static long SIMULATION_PERIOD = 5;// Simulation period in milliseconds, determines how fast autopilot calculations happen

    private ArrayList<Runnable> aircraftAddedListeners = new ArrayList<>();

    private ArrayList<Aircraft> collectionOfAircraft = new ArrayList<>();
    private Aircraft selectedAircraft;
    private Autopilot autopilot;
    private boolean simulation;
    private boolean paused = true;
    private Vector goal;

    private MainSwingCanvas mainSwingCanvas;
    
    private ColorRGBA[] usedColors;
    private HashMap<Cube,Vector> cubePositions;
    private Set<Cube> cubesInWorld;
    
    private static int W = 10;
    private static int L = 350;
    private ArrayList<Airport> airports;
    
    private boolean first = true;
    

    public World(MainSwingCanvas app) {
        this.airports = new ArrayList<>();
        this.autopilot = AutopilotFactory.createAutopilot();

        this.mainSwingCanvas = app;
        this.cubesInWorld = new HashSet<Cube>();
        this.cubePositions = new HashMap<Cube, Vector>();
        this.addAirport(0,-110);
        //this.newGround();
        // Simulated evolve
        // Run autopilot every 10 milliseconds
        Timer simulationTimer = new Timer(true);
        TimerTask simulationTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    evolveAutopilot((float) 0.01);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Evolve failed", ex);
                }
            }
        };
        simulationTimer.scheduleAtFixedRate(simulationTimerTask, 0, SIMULATION_PERIOD);

        //this.generateCubes(this.readFile("cubePositions.txt"));
    }

    public void addAircraft(Aircraft aircraft) {
        if(this.collectionOfAircraft.size() == 0){
            this.setSelectedAircraft(aircraft);
        }
        this.collectionOfAircraft.add(aircraft);
        aircraft.setWorld(this);

        for(Runnable aal: aircraftAddedListeners)
            aal.run();
    }

    public void setSelectedAircraft(Aircraft aircraft){
        this.selectedAircraft = aircraft;
    }
    
    public Aircraft getSelectedAircraft() {
        return this.selectedAircraft;
    }

    public ArrayList<Aircraft> getCollectionOfAircraft(){
        return this.collectionOfAircraft;
    }
    
    public Autopilot getAutopilot() {
        return this.autopilot;
    }

    private void evolveAutopilot(float dt){
        // Evolve every aircraft
        for(Aircraft ac: getCollectionOfAircraft()) {
            if (this.isSimulating() && !this.isPaused()) {
                AutopilotInputs autopilotInputs = ac.getAutopilotInputs();
                AutopilotOutputs autopilotOutputs = getAutopilot().timePassed(autopilotInputs); // TODO: use autopilot module (multithreading)
                ac.readAutopilotOutputs(autopilotOutputs);
                ac.updateAirplane(dt);
            }
        }
    }
    
    public void evolve(float dt) {
        // Evolve every aircraft
        for(Aircraft ac: getCollectionOfAircraft()) {
            if (this.isSimulating() && !this.isPaused()) {
                //check collision with ground
                CollisionResults results = new CollisionResults();
                ac.getAircraftGeometry().collideWith(this.mainSwingCanvas.getTerrain().getWorldBound(), results);
                boolean collidesWithAirport = false;
                CollisionResults temp = new CollisionResults();
                for (Airport airport : airports) {
                    ac.collideWith(airport.getBatchNode().getWorldBound(), temp);
                    if (temp.size() > 0) collidesWithAirport = true;
                }
                if (results.size() > 0 && !first && !collidesWithAirport) {
                    System.out.println("Danio: " + results.getClosestCollision().getGeometry().getLocalTranslation().getZ());

                 //   this.endSimulation();


                }
                if (hasToCrash(ac)) this.endSimulation();
                // Update visual position of aircraft
                ac.updateVisualCoordinates();
                ac.updateVisualRotation();
                // Aircraft's calc coordinates and actual visual position coordinates are now the same


                first = false;
            }


            // Removes the cubes the aircraft approaches // TODO: remove?
            Cube cubeToRemove = null;
            for (Cube cube : this.getCubesInWorld()) {
                Vector cubePos = this.getCubePositions().get(cube);
                if (ac.getCalcCoordinates().calculateDistance(cubePos) <= 8) {
                    cubeToRemove = cube;
                    this.getCubePositions().remove(cube);
                    cube.destroy();
                }
            }
            this.getCubesInWorld().remove(cubeToRemove);
        }

    }
    
    private boolean hasToCrash(Aircraft aircraft) {
            float wingY = (float) (aircraft.getCalcCoordinates().getY()-Math.sin(aircraft.getRoll())*AirplaneModel.WING_LENGTH);
            float tailY = (float) (aircraft.getCalcCoordinates().getY()-Math.sin(aircraft.getPitch())*AirplaneModel.TAIL_SIZE);
            if (wingY<0) System.out.println("value: " + aircraft.getCalcCoordinates().getY());
            return (wingY<0 || tailY<0);
        }
    
    public void startSimulation() {
        // TODO: support for autopilot module
        this.simulation = true;
        this.getAutopilot().simulationStarted(this.getSelectedAircraft().getConfig(), this.getSelectedAircraft().getAutopilotInputs());
    }
    
    public void endSimulation() {
        // TODO: support for autopilot module
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

    public void generateCube(float x, float y, float z, ColorRGBA color){
        Cube cube = new Cube(x,y,z, color, mainSwingCanvas.getAssetManager(), mainSwingCanvas);
        this.getCubesInWorld().add(cube);
        this.getCubePositions().put(cube, new Vector(x,y,z));
    }
    
    public void generateTestBeam(int n){
        this.usedColors = new ColorRGBA[n];
        for(int i=0; i<n; i++) {
            float z = (float)i/(float)(n-1)*(-90)-10;
            float x = (float) Math.random()*20-10;
            float y = (float) Math.random()*10;
            ColorRGBA color = generateRandomSuitableColor();
            this.getUsedColors()[i] = color;
            generateCube(x,y,z,color);
        }           
    }

    protected ColorRGBA generateRandomSuitableColor() {
        ColorRGBA color = ColorRGBA.randomColor();
        // TODO: only allow suitable colors

        while (colorIsUsed(color)) color = ColorRGBA.randomColor();
        return color;
    }

    public void generateRandomCubes(int n) {
        this.usedColors = new ColorRGBA[n]; // TODO: take previously used colors into account as well
        for(int i=1; i<n+1; i++) {
            float z = i*-40;
            float x = (float) Math.random()*10;
            float y = (float) Math.random()*(10-x);
            ColorRGBA color = generateRandomSuitableColor();
            this.getUsedColors()[i-1] = color;
            generateCube(x,y,z,color);
        }
    }
    
    public void setPath(Vector[] positions) {
        for(int i=0; i<positions.length; i++) {
            Vector currentPos = positions[i];
            ColorRGBA color = generateRandomSuitableColor();
            this.getUsedColors()[i] = color;
            generateCube(currentPos.getX(),currentPos.getY(),currentPos.getZ(),color);
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
                String line = String.valueOf(pos.getX())+empty + String.valueOf(pos.getY())+empty+String.valueOf(pos.getZ()) + System.lineSeparator();
                writer.write(line);  
            }
            writer.close();
        }
        catch(IOException e) {}
       
       
    }
    
    public MainSwingCanvas getCanvas() {
        return this.mainSwingCanvas;
    }
    
    private void addAirport(float xPos, float zPos) {
        Airport airport = new Airport(W,L,airports.size(),xPos,zPos,this);
        airports.add(airport);
        airport.build();
    }

    public void addAircraftAddedListener(Runnable aircraftAddedListener) {
        this.aircraftAddedListeners.add(aircraftAddedListener);
    }
}
