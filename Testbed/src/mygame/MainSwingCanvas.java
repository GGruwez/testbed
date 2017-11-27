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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainSwingCanvas extends com.jme3.app.SimpleApplication{

    private RenderCamera sas;
    private Aircraft aircraft;
    private World world;
    public Cube goal;
    private BitmapText aircraftInfo;
    private Log log = new Log();
    private Callback callbackAfterAppInit;

    private boolean mouseVisible = false;

    public void addCallBackAfterAppInit(Callback callbackAfterAppInit) {
        this.callbackAfterAppInit = callbackAfterAppInit;
    }

    @Override
    public void simpleInitApp() {
        this.setPauseOnLostFocus(false);
        this.setDisplayFps(false);
        this.setDisplayStatView(false);

        world = new World(this);


        Box b = new Box(1, 1, 1);
        Geometry goalCube = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Specular",ColorRGBA.Red);
        mat.setColor("Ambient", ColorRGBA.Red);
        goalCube.setMaterial(mat);
        goalCube.setLocalTranslation(0, 0, 0);

        Box plane = new Box(1,1,2);
        Node planemodel = (Node) assetManager.loadModel("Models/airplane6.j3o");
        aircraft = new Aircraft("Plane", planemodel, 0, 0, 0, 0, 0, -20, 0, 0, 0, 0, 0);
        world.setAircraft(aircraft);
        
        goal = new Cube(0, 5, -30, ColorRGBA.Red, assetManager, rootNode);
//        Cube goal2 = new Cube(0, 0, -60, ColorRGBA.Blue, assetManager, rootNode);

        // Plane camera viewport
        ViewPort planeCamViewPort = renderManager.createMainView("planecam view", aircraft.getCamera());
        planeCamViewPort.setClearFlags(true, true, true);
        planeCamViewPort.attachScene(rootNode);
        planeCamViewPort.setBackgroundColor(ColorRGBA.Black);

        // Plane chase camera viewport
        ViewPort chaseCamViewPort = renderManager.createMainView("chasecam view", world.getChaseCam());
        chaseCamViewPort.setClearFlags(true, true, true);
        chaseCamViewPort.attachScene(rootNode);
        chaseCamViewPort.setBackgroundColor(ColorRGBA.DarkGray);
        rootNode.attachChild(world.getChaseCamNode());
        // Top down camera viewport
        ViewPort topDownCamViewPort = renderManager.createMainView("top down cam view", world.getTopDownCam());
        topDownCamViewPort.setClearFlags(true, true, true);
        topDownCamViewPort.attachScene(rootNode);
        topDownCamViewPort.setBackgroundColor(ColorRGBA.Black);
        rootNode.attachChild(world.getTopDownCamNode());
        // Side camera viewport
        ViewPort sideCamViewPort = renderManager.createMainView("top down cam view", world.getSideCam());
        sideCamViewPort.setClearFlags(true, true, true);
        sideCamViewPort.attachScene(rootNode);
        sideCamViewPort.setBackgroundColor(ColorRGBA.DarkGray);
        rootNode.attachChild(world.getSideCamNode());

        // Aircraft material
        Material planeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        planeMaterial.setColor("Color", ColorRGBA.Gray);
        aircraft.getAircraftGeometry().setMaterial(planeMaterial);

        // Move aircraft to starting position
//         Quaternion pitchQuat = new Quaternion();
//        pitchQuat.fromAngleAxis((float) 0, new Vector3f(1, 0, 0));
//        Quaternion rollQuat = new Quaternion();
//        rollQuat.fromAngleAxis(0f, new Vector3f(0, 0, 1));
//        Quaternion yawQuat = new Quaternion();
//        yawQuat.fromAngleAxis((float) (Math.PI/2), new Vector3f(0, 1, 0));
//        Quaternion totalQuat = (pitchQuat.mult(rollQuat)).mult(yawQuat);

        float d =24;
        double x =0;//(-d-1) * Math.tan(Math.PI/3) + Math.random()*d * Math.tan(Math.PI/3)*2;
        double y =0;//(-d-1) * Math.tan(Math.PI/3) + Math.random()*d * Math.tan(Math.PI/3)*2;
        aircraft.setLocalTranslation((float) x,(float) y,d);
//        aircraft.setLocalRotation(totalQuat);




        // Set viewport background color to white
        this.viewPort.setBackgroundColor(ColorRGBA.White);

        //rootNode.attachChild(goalCube);
        rootNode.attachChild(aircraft);

        sas = new RenderCamera(aircraft.getCamera(), settings.getWidth(), settings.getHeight(), aircraft);
        sas.initialize(stateManager, this);

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
        cam.lookAt(new Vector3f(0, 0, 30), Vector3f.ZERO);

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

        callbackAfterAppInit.run();
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
            world.evolve(tpf);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Math.sqrt(Math.pow(aircraft.getCoordinates().getX()-goal.getX(), 2) +
                Math.pow(aircraft.getCoordinates().getY()-goal.getY(), 2) +
                Math.pow(aircraft.getCoordinates().getZ()-goal.getZ(), 2)) <=4) {
            goal.destroy();
            this.goal = new Cube(0, 0, -80, ColorRGBA.Red, assetManager, rootNode);
        }
        sas.grabCamera();

        this.refreshAircraftInfo();
        log.addLine(this.getAircraft());
        log.save();
    }

    public void refreshAircraftInfo(){
        String aircraftInfoText = "Aircraft Info:\r\n";
        aircraftInfoText += "Position: " + this.aircraft.getCoordinates().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += "Velocity: " + this.aircraft.getVelocity().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += "Acceleration: " + this.aircraft.getAcceleration().toString();
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Pitch: %.2f", this.aircraft.getPitch());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Roll: %.2f", this.aircraft.getRoll());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Heading: %.2f", this.aircraft.getHeading());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("leftWingInclination: %.2f", this.aircraft.getLeftWingInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("rightWingInclination: %.2f", this.aircraft.getRightWingInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("horStabInclination: %.2f", this.aircraft.getHorStabInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("verStabInclination: %.2f", this.aircraft.getVerStabInclination());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Elapsed time: %.2f", this.aircraft.getElapsedTime());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Manual control [q]: %b", this.aircraft.isManualControlEnabled());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Mouse released [r]: %b", this.isMouseVisible());
        aircraftInfoText += "\r\n";
        aircraftInfoText += String.format("Paused [p]: %b", this.world.isPaused());
        aircraftInfoText += "\r\n";
        aircraftInfo.setText(aircraftInfoText);
    }

    public Aircraft getAircraft(){
        return this.aircraft;
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
        // Add the names to the action listener.
        inputManager.addListener(actionListener,"SwitchControl", "ReleaseMouse", "Pause");
        inputManager.addListener(analogListener,"PlaneLeft", "PlaneRight", "PlanePosStab", "PlaneNegStab");

    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("SwitchControl") && !keyPressed) {
                MainSwingCanvas.this.getAircraft().toggleManualControl();
            }else if(name.equals("ReleaseMouse") && !keyPressed){
                inputManager.setCursorVisible(!mouseVisible);
                flyCam.setEnabled(mouseVisible);
                mouseVisible = !mouseVisible;
            }else if(name.equals("Pause") && !keyPressed){
                if(world.isPaused()){
                    world.continueSimulation();
                }else{
                    world.pauseSimulation();
                }
            }
        }
    };

    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            Aircraft ac = MainSwingCanvas.this.getAircraft();
            if(name == "PlaneLeft"){
                ac.setLeftWingInclination(ac.getLeftWingInclination() - 0.01f);
                ac.setRightWingInclination(ac.getRightWingInclination() + 0.01f);
            }else if(name == "PlaneRight"){
                ac.setLeftWingInclination(ac.getLeftWingInclination() + 0.01f);
                ac.setRightWingInclination(ac.getRightWingInclination() - 0.01f);
            }else if(name == "PlanePosStab"){
                ac.setHorStabInclination(ac.getHorStabInclination() + 0.001f);
            }else if(name == "PlaneNegStab"){
                ac.setHorStabInclination(ac.getHorStabInclination() - 0.001f);
            }
        }
    };

    private boolean isMouseVisible(){
        return mouseVisible;
    }

}
