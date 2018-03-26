package mygame;

import com.jme3.font.BitmapText;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.*;
import com.jme3.terrain.geomipmap.grid.ImageTileLoader;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.Namer;
import com.jme3.texture.Texture;
import mygame.visualcomponents.RegularBox;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainSwingCanvas extends com.jme3.app.SimpleApplication implements CustomCanvas{

    protected RenderCamera renderCamera;
    private World world;
    private BitmapText aircraftInfo;
    private TerrainGrid terrain;
    private Log log = new Log();
    private Callback callbackAfterAppInit;
    private LinkedList<Spatial> newSpatialQueue = new LinkedList<Spatial>();
    private LinkedList<Spatial> destructibleSpatialQueue = new LinkedList<Spatial>();

    public CustomView chaseCameraCustomView;
    public CustomDualView topDownCameraCustomView;
    private boolean keepUpdating = true;

    private boolean mouseVisible = false;

    public void addCallBackAfterAppInit(Callback callbackAfterAppInit) {
        this.callbackAfterAppInit = callbackAfterAppInit;
    }

    @Override
    public void simpleInitApp() {
        this.setPauseOnLostFocus(false);
        releaseMouse();
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        settings.setRenderer("JOGL");

        world = new World(this);


        Box b = new RegularBox(1, 1, 1);
        Geometry goalCube = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Specular",ColorRGBA.Red);
        mat.setColor("Ambient", ColorRGBA.Red);
        goalCube.setMaterial(mat);
        goalCube.setLocalTranslation(0, 0, 0);
        
 //        getRootNode().attachChild(apm);

         Aircraft aircraft = new Aircraft("Plane", assetManager, 0, 1.72f, 0, 0, 0, 0f, 0, 0, 0, 0, 0);
         world.addAircraft(aircraft);
         rootNode.attachChild(aircraft);

//        Box plane = new Box(1,1,2);
//        Node planemodel = (Node) assetManager.loadModel("Models/airplane6.j3o");
//        aircraft = new Aircraft("Plane", planemodel, 0, 0, 0, 0, 0, -32f, 0, 0, 0, 0, 0);
//
//        world.setAircraft(aircraft);

        // TODO: maybe remove these viewports (Attention: plane camera viewport has to be available someway to support image recognition)
        createPlaneCameraViewport();
        createChaseCameraViewport();
        createTopDownCameraViewport();
        createSideCameraViewport();

        // Move aircraft to starting position
//         Quaternion pitchQuat = new Quaternion();
//        pitchQuat.fromAngleAxis((float) 0, new Vector3f(1, 0, 0));
//        Quaternion rollQuat = new Quaternion();
//        rollQuat.fromAngleAxis(0f, new Vector3f(0, 0, 1));
//        Quaternion yawQuat = new Quaternion();
//        yawQuat.fromAngleAxis((float) (Math.PI/2), new Vector3f(0, 1, 0));
//        Quaternion totalQuat = (pitchQuat.mult(rollQuat)).mult(yawQuat);

        float d =0;
        double x =0;//(-d-1) * Math.tan(Math.PI/3) + Math.random()*d * Math.tan(Math.PI/3)*2;
        double y =0;//(-d-1) * Math.tan(Math.PI/3) + Math.random()*d * Math.tan(Math.PI/3)*2;
//        aircraft.setLocalTranslation((float) x,(float) y,d);
//        aircraft.setLocalRotation(totalQuat);

        // Set viewport background color to white
        this.viewPort.setBackgroundColor(ColorRGBA.White);

        // TODO: support for multiple aircraft
        renderCamera = new RenderCamera(aircraft.getCamera(), settings.getWidth(), settings.getHeight(), aircraft);
        renderCamera.initialize(stateManager, this);

        world.setGoal(goalCube.getLocalTranslation().getX(),
                goalCube.getLocalTranslation().getY(),
                goalCube.getLocalTranslation().getZ());
        world.startSimulation();

        aircraftInfo = new BitmapText(guiFont, false);
        aircraftInfo.setSize(guiFont.getCharSet().getRenderedSize()*3/4);
        aircraftInfo.setColor(ColorRGBA.Blue);
        aircraftInfo.setLocalTranslation(0, settings.getHeight(), 0);
        guiNode.attachChild(aircraftInfo);

        // Change camera view to show both cube and aircraft in one shot
        cam.setLocation(new Vector3f(-100, 0, 0));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.ZERO);

//        new CubeUI(world);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.85f,-1,-0.7f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.23f));
        getRootNode().addLight(sun);


        DirectionalLight moon = new DirectionalLight();
        moon.setDirection(new Vector3f(0.3f,0f,0.6f).normalizeLocal());
        moon.setColor(ColorRGBA.White.mult(0.35f));
        getRootNode().addLight(moon);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.15f));
        getRootNode().addLight(al);

//        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        createChaseCameraCustomView();
        createTopDownCameraCustomView();

        createTerrain();

        callbackAfterAppInit.run();
    }

    private void createPlaneCameraViewport(){
        // Plane camera viewport
        ViewPort planeCamViewPort = renderManager.createMainView("planecam view", this.getSelectedAircraft().getCamera());
        planeCamViewPort.setClearFlags(true, true, true);
        planeCamViewPort.attachScene(rootNode);
        planeCamViewPort.setBackgroundColor(ColorRGBA.White);
    }

    private void createChaseCameraViewport(){
        // Plane chase camera viewport
        ViewPort chaseCamViewPort = renderManager.createMainView("chasecam view", this.getWorld().getChaseCam());
        chaseCamViewPort.setClearFlags(true, true, true);
        chaseCamViewPort.attachScene(rootNode);
        chaseCamViewPort.setBackgroundColor(ColorRGBA.White);
        rootNode.attachChild(this.getWorld().getChaseCamNode());
    }

    private void createTopDownCameraViewport(){
        // Top down camera viewport
        ViewPort topDownCamViewPort = renderManager.createMainView("top down cam view", this.getWorld().getTopDownCam());
        topDownCamViewPort.setClearFlags(true, true, true);
        topDownCamViewPort.attachScene(rootNode);
        topDownCamViewPort.setBackgroundColor(ColorRGBA.White);
        rootNode.attachChild(this.getWorld().getTopDownCamNode());
    }

    private void createSideCameraViewport(){
        // Side camera viewport
        ViewPort sideCamViewPort = renderManager.createMainView("top down cam view", this.getWorld().getSideCam());
        sideCamViewPort.setClearFlags(true, true, true);
        sideCamViewPort.attachScene(rootNode);
        sideCamViewPort.setBackgroundColor(ColorRGBA.White);
        rootNode.attachChild(this.getWorld().getSideCamNode());
    }

    private void createChaseCameraCustomView() {
        chaseCameraCustomView = new CustomView(this);
//        chaseCameraCustomView.start(JmeContext.Type.Canvas);
    }

    CustomView createAndGetChaseCameraCustomView(){
        this.createChaseCameraCustomView();
        return this.chaseCameraCustomView;
    }

    private void createTopDownCameraCustomView() {
        this.topDownCameraCustomView = new CustomDualView(this);
    }

    CustomView createAndGetTopDownCameraCustomView(){
        this.createTopDownCameraCustomView();
        return this.topDownCameraCustomView;
    }

    @Override
    public void update(){
        if(this.keepUpdating) super.update();
    }

    private boolean initialFrame = true;
    @Override
    public void simpleUpdate(float tpf) {
        if (initialFrame) {
            inputManager.deleteMapping(CameraInput.FLYCAM_STRAFELEFT);
            inputManager.deleteMapping(CameraInput.FLYCAM_STRAFERIGHT);
            inputManager.deleteMapping(CameraInput.FLYCAM_FORWARD);
            inputManager.deleteMapping(CameraInput.FLYCAM_BACKWARD);
            inputManager.deleteMapping(CameraInput.FLYCAM_RISE);
            inputManager.deleteMapping(CameraInput.FLYCAM_LOWER);
            initKeys();
            initialFrame = false;
        }
        try {

            // Link new items
            try {
                while (!newSpatialQueue.isEmpty()) {
                    synchronized(newSpatialQueue){
                        if(!newSpatialQueue.isEmpty()) {
                            rootNode.attachChild(newSpatialQueue.peek());
                            newSpatialQueue.pop();
                        }
                    }
                }
                // Unlink old items
                while (!destructibleSpatialQueue.isEmpty()) {
                    synchronized(destructibleSpatialQueue) {
                        if(!destructibleSpatialQueue.isEmpty()) {
                            rootNode.detachChild(destructibleSpatialQueue.peek());
                            destructibleSpatialQueue.pop();
                        }
                    }
                }

                world.evolve(tpf);
            }catch (ConcurrentModificationException cme){
                System.out.println("Concurrent modification exception, completing later.");
            }
            updateDifferentCameras();

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        renderCamera.grabCamera();

        this.refreshAircraftInfo();
        log.addLine(this.getSelectedAircraft());
        log.save();
    }

    private void updateDifferentCameras(){
        // Update chase camera
        this.chaseCameraCustomView.updateCamera(cv -> {
            Vector newChaseCamPosition = this.getSelectedAircraft().getCalcCoordinates().inverseTransform(0, 0,0 ).add(new Vector(0, 0, 20)).transform(0,0,0);
            cv.getCameraNode().setLocalTranslation(newChaseCamPosition.getX(), newChaseCamPosition.getY(), newChaseCamPosition.getZ());
            Vector aircraftCoordinates = this.getSelectedAircraft().getCalcCoordinates();
            cv.getCameraNode().lookAt(new Vector3f(aircraftCoordinates.getX(), aircraftCoordinates.getY(), aircraftCoordinates.getZ()), Vector3f.UNIT_Y);
        });

        // Update top down camera
        this.topDownCameraCustomView.updateCamera((CustomDualViewCallback) cv -> {
            // TODO: check if this is consistent with the assignment
            Vector aircraftCoordinates = this.getSelectedAircraft().getCalcCoordinates();
            cv.getCameraNode().setLocalTranslation(aircraftCoordinates.getX()-80, aircraftCoordinates.getY() + 80, aircraftCoordinates.getZ());
            cv.getCameraNode().lookAt(new Vector3f(aircraftCoordinates.getX()-80, aircraftCoordinates.getY(), aircraftCoordinates.getZ()), Vector3f.UNIT_X);

            cv.getSecondCamera().resize(cv.width, cv.height/2, false);
            cv.getSecondCameraNode().setLocalTranslation(aircraftCoordinates.getX()-30, aircraftCoordinates.getY(), aircraftCoordinates.getZ());
            cv.getSecondCameraNode().lookAt(new Vector3f(aircraftCoordinates.getX(), aircraftCoordinates.getY(), aircraftCoordinates.getZ()), Vector3f.UNIT_Y);
        });

    }

    public void refreshAircraftInfo(){
        String aircraftInfoText = "Aircraft Info:\r\n";
        aircraftInfoText += "Position: " + this.getSelectedAircraft().getCalcCoordinates().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += "Velocity: " + this.getSelectedAircraft().getVelocity().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += "Acceleration: " + this.getSelectedAircraft().getAcceleration().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Pitch: %.2f", this.getSelectedAircraft().getPitch());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Roll: %.2f", this.getSelectedAircraft().getRoll());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Heading: %.2f", this.getSelectedAircraft().getHeading());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("leftWingInclination: %.2f", this.getSelectedAircraft().getLeftWingInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("rightWingInclination: %.2f", this.getSelectedAircraft().getRightWingInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("horStabInclination: %.2f", this.getSelectedAircraft().getHorStabInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("verStabInclination: %.2f", this.getSelectedAircraft().getVerStabInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Elapsed time: %.2f", this.getSelectedAircraft().getElapsedTime());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Manual control [q]: %b", this.getSelectedAircraft().isManualControlEnabled());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Mouse released [r]: %b", this.isMouseVisible());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Paused [p]: %b", this.world.isPaused());
        aircraftInfoText += "\r\n";
        aircraftInfo.setText(aircraftInfoText);
    }

    public Aircraft getSelectedAircraft(){
        return this.getWorld().getSelectedAircraft();
    }

    public World getWorld(){return this.world;}

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /** Custom Keybinding: Map named actions to inputs. */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("SwitchControl",  new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("PlaneLeft",  new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("PlaneRight",  new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("PlanePosStab",  new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("PlaneNegStab",  new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("ReleaseMouse",  new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("CameraStrafeLeft",  new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("CameraStrafeRight",  new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("CameraForward",  new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("CameraBackward",  new KeyTrigger(KeyInput.KEY_K));
        // Add the names to the action listener.
        inputManager.addListener(actionListener,"SwitchControl", "ReleaseMouse", "Pause");
        inputManager.addListener(analogListener,"PlaneLeft", "PlaneRight", "PlanePosStab", "PlaneNegStab", "CameraStrafeLeft", "CameraStrafeRight", "CameraForward", "CameraBackward");

    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("SwitchControl") && !keyPressed) {
                MainSwingCanvas.this.getSelectedAircraft().toggleManualControl();
            }else if(name.equals("ReleaseMouse") && !keyPressed){
                releaseMouse();
            }else if(name.equals("Pause") && !keyPressed){
                if(world.isPaused()){
                    world.continueSimulation();
                }else{
                    world.pauseSimulation();
                }
            }
        }
    };

    private void releaseMouse() {
        inputManager.setCursorVisible(!mouseVisible);
        flyCam.setEnabled(mouseVisible);
        mouseVisible = !mouseVisible;
    }

    // TODO: derived from jmonkey source code, add credits
    private void moveCamera(float value, boolean sideways){
        Vector3f vel = new Vector3f();
        Vector3f pos = cam.getLocation().clone();

        if (sideways){
            cam.getLeft(vel);
        }else{
            cam.getDirection(vel);
        }
        vel.multLocal(value * 50); // 50 determines camera movement speed
        pos.addLocal(vel);

        cam.setLocation(pos);
    }

    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            Aircraft ac = MainSwingCanvas.this.getSelectedAircraft();
            switch (name) {
                case "PlaneLeft":
                    ac.setLeftWingInclination(ac.getLeftWingInclination() - 0.01f);
                    ac.setRightWingInclination(ac.getRightWingInclination() + 0.01f);
                    break;
                case "PlaneRight":
                    ac.setLeftWingInclination(ac.getLeftWingInclination() + 0.01f);
                    ac.setRightWingInclination(ac.getRightWingInclination() - 0.01f);
                    break;
                case "PlanePosStab":
                    ac.setHorStabInclination(ac.getHorStabInclination() + 0.001f);
                    break;
                case "PlaneNegStab":
                    ac.setHorStabInclination(ac.getHorStabInclination() - 0.001f);
                    break;
                case "CameraStrafeLeft":
                    moveCamera(value, true);
                    break;
                case "CameraStrafeRight":
                    moveCamera(-value, true);
                    break;
                case "CameraForward":
                    moveCamera(value, false);
                    break;
                case "CameraBackward":
                    moveCamera(-value, false);
                    break;
            }
        }
    };

    private boolean isMouseVisible(){
        return mouseVisible;
    }

    public void selectView(){
        this.keepUpdating = true;
    }

    public void deselectView(){
        this.keepUpdating = false;
    }

    public void emptyNewSpatialQueue(){
        synchronized(newSpatialQueue){
            this.newSpatialQueue.clear();
        }
    }
    
    public void crashAircraft() {
        this.getRootNode().detachChild(this.getSelectedAircraft());
        this.stop();
    }

    public void addToNewSpatialQueue(Spatial newItem){
        synchronized(newSpatialQueue) {
            this.newSpatialQueue.add(newItem);
        }
    }

    public void emptyDestructibleSpatialQueue(){
        synchronized(destructibleSpatialQueue){
            this.destructibleSpatialQueue.clear();
        }
    }

    public void addToDestructibleSpatialQueue(Spatial newItem){
        synchronized(destructibleSpatialQueue){
            this.destructibleSpatialQueue.add(newItem);
        }
    }

    private void createTerrain(){
        // Create material from Terrain Material Definition
        Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        // Load alpha map (for splat textures)
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        // load grass texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grid.png");
        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", 65f);

        this.terrain = new TerrainGrid("terrain", 512, 257, new ImageTileLoader(assetManager, new Namer() {
            public String getName(int x, int y) {
                return "Textures/Terrain/splat/heightmap.png";
            }
        }));
        terrain.setMaterial(matRock);
        terrain.setLocalTranslation(0, 0, 0);
        terrain.setLocalScale(500f, 1f, 500f);
        this.rootNode.attachChild(terrain);

        addLodControlToTerrain(getCamera());
    }

    public void addLodControlToTerrain(Camera cam){
        TerrainLodControl control = new TerrainGridLodControl(terrain, cam); // TODO: multiple camera's
        control.setLodCalculator( new DistanceLodCalculator(512, 3f) );
        this.terrain.addControl(control);
    }
    
    public TerrainGrid getTerrain() {return this.terrain;}

}
