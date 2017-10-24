package mygame;

import com.jme3.app.SimpleApplication;
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
        aircraft = new Aircraft("Plane", plane, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
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

    @Override
    public void simpleUpdate(float tpf) {
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
}
