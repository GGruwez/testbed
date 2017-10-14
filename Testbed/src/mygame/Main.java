package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;

import mygame.RenderCamera;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    private RenderCamera sas;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        sas = new RenderCamera("", "test");
        sas.initialize(stateManager, this);
        Box b = new Box(1, 1, 1);
        Geometry box = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        box.setMaterial(mat);
        box.setLocalTranslation(0, 0, 0);

        rootNode.attachChild(box);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
//        System.out.println(cam.getLocation());
        
        sas.takeScreenshot();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
