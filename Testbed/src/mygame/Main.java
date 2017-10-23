package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
        aircraft = new Aircraft("Plane", plane, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0);
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
        aircraft.setLocalTranslation(0, 0, 30);
        
        // Set viewport background color to white
        this.viewPort.setBackgroundColor(ColorRGBA.White);
        
        rootNode.attachChild(goalCube);
        rootNode.attachChild(aircraft);
        
        sas = new RenderCamera(aircraft.getCamera(), settings.getWidth(), settings.getHeight());
        sas.initialize(stateManager, this);
        
        world.setGoal(goalCube.getLocalTranslation().getX(),
                goalCube.getLocalTranslation().getY(),
                goalCube.getLocalTranslation().getZ());
        world.startSimulation();
    }

    private boolean initialFrame = true;
    @Override
    public void simpleUpdate(float tpf) {
        if (initialFrame) {
            inputManager.deleteMapping(CameraInput.FLYCAM_STRAFELEFT);
            inputManager.deleteMapping(CameraInput.FLYCAM_STRAFERIGHT);
            inputManager.deleteMapping(CameraInput.FLYCAM_FORWARD);
            inputManager.deleteMapping(CameraInput.FLYCAM_BACKWARD);
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
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /** Custom Keybinding: Map named actions to inputs. */
    private void initKeys() {
      // You can map one or several inputs to one named action
      inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_D));
      // Add the names to the action listener.
      inputManager.addListener(actionListener,"Pause");
      inputManager.addListener(analogListener,"Left", "Right", "Rotate");

    }

    private ActionListener actionListener = new ActionListener() {
      public void onAction(String name, boolean keyPressed, float tpf) {
//        if (name.equals("Pause") && !keyPressed) {
//          isRunning = !isRunning;
//        }
      }
    };

    private AnalogListener analogListener = new AnalogListener() {
      public void onAnalog(String name, float value, float tpf) {
          if(name == "Right"){
              System.out.println("Right");
          }
      }
    };
}
