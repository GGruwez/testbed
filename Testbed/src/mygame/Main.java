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
        Geometry box = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        box.setMaterial(mat);
        box.setLocalTranslation(0, 0, 0);
        
        Box plane = new Box(2,1,1);
        aircraft = new Aircraft("Plane", plane, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Material planeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        planeMaterial.setColor("Color", ColorRGBA.Green);
        aircraft.setMaterial(planeMaterial);
        aircraft.setLocalTranslation(-4, 0, 0);
        
        this.viewPort.setBackgroundColor(ColorRGBA.White);
        
        // Plane camera
        Camera planeCam = cam.clone();
        planeCam.setViewPort(0.75f, 1.0f, 0.0f, 0.25f);
        ViewPort planeCamViewPort = renderManager.createMainView("planecam view", planeCam);
        planeCamViewPort.setClearFlags(true, true, true);
        planeCamViewPort.attachScene(rootNode);
        planeCamViewPort.setBackgroundColor(ColorRGBA.Black);
        
        CameraNode planeCamNode = new CameraNode("Camera Node", planeCam);
        planeCamNode.setControlDir(ControlDirection.SpatialToCamera);
        Node planeNode = new Node("planenode");
        planeNode.attachChild(aircraft);
        planeNode.attachChild(planeCamNode);
        planeCamNode.setLocalTranslation(new Vector3f(-5, 3, 0));
        planeCamNode.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
        
        rootNode.attachChild(box);
        rootNode.attachChild(planeNode);
        
        
        sas = new RenderCamera(planeCam);
        sas.initialize(stateManager, this);
    }

    @Override
    public void simpleUpdate(float tpf) {
        rootNode.getChild("planenode").setLocalTranslation(
                rootNode.getChild("planenode").getLocalTranslation().getX() + 0.01f, 
                rootNode.getChild("planenode").getLocalTranslation().getY(),
                rootNode.getChild("planenode").getLocalTranslation().getZ());
        sas.grabCamera();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
