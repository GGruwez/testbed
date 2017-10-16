package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;

import mygame.RenderCamera;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private RenderCamera sas;
    private Aircraft aircraft;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry goalCube = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        goalCube.setMaterial(mat);
        goalCube.setLocalTranslation(0, 0, 0);
        
        Box plane = new Box(2,1,1);
        aircraft = new Aircraft("Plane", plane, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        
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
        aircraft.setLocalTranslation(-10, 0, 0);
        
        // Set viewport background color to white
        this.viewPort.setBackgroundColor(ColorRGBA.White);
        
        rootNode.attachChild(goalCube);
        rootNode.attachChild(aircraft);
        
        sas = new RenderCamera(aircraft.getCamera(), settings.getWidth(), settings.getHeight());
        sas.initialize(stateManager, this);
    }

    @Override
    public void simpleUpdate(float tpf) {
        aircraft.updateAirplane(tpf);
        sas.grabCamera();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
