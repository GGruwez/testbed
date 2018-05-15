package mygame;

import com.jme3.collision.CollisionResults;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.math.ColorRGBA;

import interfaces.*;
import interfaces.AutopilotOutputs;
//import interfaces.AutopilotFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class World {


    public static float DEFAULT_SIMULATION_PERIOD_MULTIPLIER = 1;
    public static float DEFAULT_AUTOPILOT_SIMULATION_TIME_DIFFERENCE = 0.01f;
    private float simulationPeriodMultiplier = DEFAULT_SIMULATION_PERIOD_MULTIPLIER; // spm value of 1 means: 1 second simulation period equals 1 second real time


    private ArrayList<Runnable> aircraftAddedListeners = new ArrayList<>();
    private ArrayList<Runnable> airportAddedListeners = new ArrayList<>();
    private ArrayList<Runnable> packagesChangedListeners = new ArrayList<>();
    private ArrayList<Runnable> simulationPeriodChangedListeners = new ArrayList<>();

    private ArrayList<Aircraft> collectionOfAircraft = new ArrayList<>();
    private Aircraft selectedAircraft;
    private boolean simulation;
    private boolean paused = true;
    private Vector goal;

    private MainSwingCanvas mainSwingCanvas;
    
    private ColorRGBA[] usedColors;
    private HashMap<Cube,Vector> cubePositions;
    private Set<Cube> cubesInWorld;
    
    private static int W = 30;
    private static int L = 600;
    private ArrayList<Airport> airports;
    private AutopilotModule autopilotModule;
    private ArrayList<Package> packages;
    
    private boolean first = true;
    

    public World(MainSwingCanvas app) {
        this.packages = new ArrayList<>();
        this.airports = new ArrayList<>();
        this.autopilotModule = new AutopilotModuleImplementation();
        this.autopilotModule.defineAirportParams(L, W);

        this.mainSwingCanvas = app;
        this.cubesInWorld = new HashSet<Cube>();
        this.cubePositions = new HashMap<Cube, Vector>();
        this.addAirport(0,0);
        this.addAirport(4000,0);
        //this.newGround();
        // Simulated evolve
        // Run autopilot every 10 milliseconds
        createAndStartSimulationTimer();

        // TODO: support changing simulationPeriodMultiplier
        addSimulationPeriodChangedListener(()->{
            if(simulationTimer != null) {
                createAndStartSimulationTimer();
            }
        });

        //this.generateCubes(this.readFile("cubePositions.txt"));
    }

    Timer simulationTimer;

    /**
     * Creates and starts new simulation timer.
     * Previous simulation timer will be canceled.
     */
    private void createAndStartSimulationTimer(){
        if(simulationTimer != null)
            simulationTimer.cancel();
        simulationTimer = new Timer(true);
        TimerTask simulationTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    evolveAutopilot(DEFAULT_AUTOPILOT_SIMULATION_TIME_DIFFERENCE);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Evolve failed", ex);
                }
            }
        };
        simulationTimer.scheduleAtFixedRate(simulationTimerTask, 0, getSimulationPeriod());
    }

    public void addAircraft(Aircraft aircraft) {
        if(this.collectionOfAircraft.size() == 0){
            this.setSelectedAircraft(aircraft);
        }
        this.collectionOfAircraft.add(aircraft);
        aircraft.setWorld(this);

        // airport, gate, pointingToRunway, autopilotConfig
        getAutopilotModule().defineDrone(aircraft.getAirport().getID(), aircraft.getGate(), aircraft.getRunway(), aircraft.getConfig());

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
    
    public AutopilotModule getAutopilotModule() {
        return this.autopilotModule;
    }

    private void evolveAutopilot(float dt){
        // Evolve every aircraft
        int i = 0;
        for(Aircraft ac: getCollectionOfAircraft()) {
            if (this.isSimulating() && !this.isPaused()) {
                AutopilotInputs autopilotInputs = ac.getAutopilotInputs();
//                AutopilotOutputs autopilotOutputs = getAutopilot().timePassed(autopilotInputs); // TODO: use multithreading..?

                getAutopilotModule().startTimeHasPassed(i, autopilotInputs);
                AutopilotOutputs autopilotOutputs = getAutopilotModule().completeTimeHasPassed(i);

                ac.readAutopilotOutputs(autopilotOutputs);
                ac.updateAirplane(dt);
            }
            i++;
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
//                    System.out.println("Danio: " + results.getClosestCollision().getGeometry().getLocalTranslation().getZ());

                    this.endSimulation();


                }
                if (hasToCrash(ac)) this.endSimulation();
                for (Aircraft aircraft:this.collectionOfAircraft) {
                    if (!aircraft.equals(ac) && hasToCrash(ac,aircraft)) this.endSimulation();
                }
                // Update visual position of aircraft
                ac.updateVisualCoordinates();
                ac.updateVisualRotation();
                // Aircraft's calc coordinates and actual visual position coordinates are now the same

                
                this.updatePackages();

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
//        this.getAutopilot().simulationStarted(this.getSelectedAircraft().getConfig(), this.getSelectedAircraft().getAutopilotInputs());
    }
    
    public void endSimulation() {
        this.simulation = false;
        this.getAutopilotModule().simulationEnded();
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

        // centerX, centerZ, centerToRunway0X, centerToRunway0Z
        // (centerToRunway0X, centerToRunway0Z) constitutes a unit vector pointing from the center of the airport towards runway 0
        getAutopilotModule().defineAirport(airport.getX(), airport.getZ(), airport.getCenterToRunway0X(), airport.getCenterToRunway0Z());

        for(Runnable aal: airportAddedListeners)
            aal.run();
    }

    public void addAircraftAddedListener(Runnable aircraftAddedListener) {
        this.aircraftAddedListeners.add(aircraftAddedListener);
    }

    public void addAirportAddedListener(Runnable airportAddedListener) {
        this.airportAddedListeners.add(airportAddedListener);
    }

    public void addPackagesChangedListeners(Runnable listener) {
        this.packagesChangedListeners.add(listener);
    }


    public ArrayList<Airport> getAirports() {return this.airports;}


    public void addSimulationPeriodChangedListener(Runnable listener) {
        this.simulationPeriodChangedListeners.add(listener);
    }

    public void setSimulationPeriodMultiplier(float newMultiplier){
        this.simulationPeriodMultiplier = newMultiplier;
        for(Runnable l: simulationPeriodChangedListeners)
            l.run();
    }

    private long getSimulationPeriod(){// Simulation period in milliseconds, determines how fast autopilot calculations happen
        // simulationPeriodMultiplier value of 1 means:     1 second passed equals 1 second autopilot time passed
        // simulationPeriodMultiplier value of 10 means:    1 second passed equals 10 seconds autopilot time passed
        // simulationPeriodMultiplier value of 0.1 means:   1 second passed equals 0.1 seconds autopilot time passed
        return (long)((1/simulationPeriodMultiplier)*1000*DEFAULT_AUTOPILOT_SIMULATION_TIME_DIFFERENCE);
    }
    
    
    
    public void addPackage(Airport airportFrom, int gateFrom, Airport airportTo, int gateTo) {
        if (gateNotAvailable(airportFrom,gateFrom)) return;
        Package toAdd = new Package(airportFrom,gateFrom,airportTo,gateTo);
        this.autopilotModule.deliverPackage(airportFrom.getID(),gateFrom,airportTo.getID(),gateTo);
        this.packages.add(toAdd);
        for(Runnable aal: packagesChangedListeners)
            aal.run();
    }
    
    public ArrayList<Package> getPackages() {return this.packages;}
    
    private void addRandomPackage() {
        Random random = new Random();
        ArrayList<Airport> airportsAvailable = this.getAvailableAirports();
        if (airportsAvailable.size()>0) {
            int index = random.nextInt(airportsAvailable.size());
            this.addPackage(airportsAvailable.get(index), random.nextInt(1), airports.get(random.nextInt(airports.size()-1)), random.nextInt(1));
        }
    }
    
    private ArrayList<Airport> getAvailableAirports() {
        ArrayList<Airport> toReturn = new ArrayList();
        for(Airport airport:this.getAirports()) {
            if (isAvailable(airport)) toReturn.add(airport);
        }
        return toReturn;
    }
    
    private boolean isAvailable(Airport airport) {
        int counter = 0;
        for (Package p:this.getPackages()) {
            if (!p.isPickedUp() && p.getAirportFrom().equals(airport)) {
                counter++;
            }
        }
        return counter<2;
    }

    private boolean gateNotAvailable(Airport airportFrom, int gateFrom) {
        for (Package p:packages) {
            if (!p.isPickedUp() && p.getAirportFrom().equals(airportFrom)&& p.getGateFrom() == gateFrom) return true;
        }
        return false;
    }
    
    private void checkPickups() {
        for(Aircraft aircraft:this.getCollectionOfAircraft()) {
            float x = aircraft.getCalcCoordinates().getX();
            float y = aircraft.getCalcCoordinates().getY();
            float z = aircraft.getCalcCoordinates().getZ();
            for(Package p:this.getPackages()) {
                if (!p.isPickedUp() && isInGate(x,y,z,p.getAirportFrom(),p.getGateFrom())) {
                    p.setPickedUp(true);
                    p.setPickedUpBy(aircraft);
                }
            }
        }
    }
    
    private boolean isDelivered(Package p) {
        if (p.isPickedUp() && isInGate(p.getX(),p.getY(),p.getZ(),p.getAirportTo(),p.getGateTo()))
            return true;
        else return false;
    }
    
    private void checkDropOffs() {
        for (Package p:this.getPackages()) {
            if (p.isPickedUp() && isInGate(p.getX(),p.getY(),p.getZ(),p.getAirportTo(),p.getGateTo())) {
                p.setPickedUp(false);
                removePackage(p);
            }
        }
    }

    private void removePackage(Package p){
        packages.remove(p);
        for(Runnable aal: packagesChangedListeners)
            aal.run();
    }
    
    private boolean isInGate(float x, float y, float z, Airport airport, int gate) {
        int offset;
        if (gate == 0) offset = W/2;
        else offset = -W/2;
        if (x>airport.getX()-Airport.W/2+offset
                && x<airport.getX()+Airport.W/2+offset
                && y<2
                && z>airport.getZ()-Airport.W/2
                && z<airport.getZ()+Airport.W/2)
            return true;
        else return false;
    }
    
    private void updatePackages() {
        this.checkPickups();
        this.checkDropOffs();
        for (Package p:this.packages) {
            p.updatePosition();
        }
    }
    
    private boolean hasToCrash(Aircraft a1, Aircraft a2) {
        Vector v1 = a1.getCalcCoordinates();
        Vector v2 = a2.getCalcCoordinates();
        if (v1.calculateDistance(v2)<5) return true;
        return false;
    }
    
    
    }
    


