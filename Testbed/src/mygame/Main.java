package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {
    
    private RenderCamera sas;
    private Aircraft aircraft;
    private World world;
    private BitmapText aircraftInfo;
    private Log log = new Log();
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        world = new World();
        
        Box b = new Box(1, 1, 1);
        Geometry goalCube = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        goalCube.setMaterial(mat);
        goalCube.setLocalTranslation(0, 0, 0);
        
        Box plane = new Box(1,1,2);
        aircraft = new Aircraft("Plane", plane, 0, 0, 0, 0, 0, -20, 0, 0, 0, 0, 0, 0);
        world.setAircraft(aircraft);
        
        // Plane camera viewport
        ViewPort planeCamViewPort = renderManager.createMainView("planecam view", aircraft.getCamera());
        planeCamViewPort.setClearFlags(true, true, true);
        planeCamViewPort.attachScene(rootNode);
        planeCamViewPort.setBackgroundColor(ColorRGBA.Black);
        
        // Aircraft material
        Material planeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        planeMaterial.setColor("Color", ColorRGBA.Green);
        aircraft.getAircraftGeometry().setMaterial(planeMaterial);
        
        // Move aircraft to starting position
        aircraft.setLocalTranslation(0, 0, 60);
        
        // Set viewport background color to white
        this.viewPort.setBackgroundColor(ColorRGBA.LightGray);
        
        rootNode.attachChild(goalCube);
        rootNode.attachChild(aircraft);
        
        sas = new RenderCamera(aircraft.getCamera(), settings.getWidth(), settings.getHeight());
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
        cam.setLocation(new Vector3f(-180, 0, 0));
        cam.lookAt(new Vector3f(0, 0, 30), Vector3f.ZERO);
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
        sas.grabCamera();
        if (! world.isSimulating()) {
            stop();
        }
        
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
        aircraftInfoText += String.format("Manual control: %b", this.aircraft.isManualControlEnabled());
        aircraftInfoText += "\r\n";
        aircraftInfo.setText(aircraftInfoText);
    }
    
    public Aircraft getAircraft(){
        return this.aircraft;
    }

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
      // Add the names to the action listener.
      inputManager.addListener(actionListener,"SwitchControl");
      inputManager.addListener(analogListener,"PlaneLeft", "PlaneRight", "PlanePosStab", "PlaneNegStab");

    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("SwitchControl") && !keyPressed) {
              Main.this.getAircraft().toggleManualControl();
          }
        }
    };

    private AnalogListener analogListener = new AnalogListener() {
      public void onAnalog(String name, float value, float tpf) {
          Aircraft ac = Main.this.getAircraft();
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
}
