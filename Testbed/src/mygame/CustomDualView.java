package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;

/**
 * Created by daanlenaerts on 17/12/2017.
 */
public class CustomDualView extends CustomView {

    private Camera secondCam;
    private CameraNode secondCamNode;

    public CustomDualView(MainSwingCanvas mainCanvas) {
        super(mainCanvas);
    }

    public void simpleInitApp(){
        super.simpleInitApp();

        this.secondCam = new Camera(this.width, this.height/2);
        this.secondCam.setFrustumPerspective(120, 1, 1, 1000);
        this.secondCam.setViewPort(0f, 1f, 0f, 1f);
        this.secondCamNode = new CameraNode("Side cam node", this.secondCam);
        this.secondCamNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        // Side camera viewport
        ViewPort sideCamViewPort2 = renderManager.createMainView("second cam view", secondCam);
        sideCamViewPort2.setClearFlags(true, true, true);
        sideCamViewPort2.attachScene(rootNode);
        sideCamViewPort2.setBackgroundColor(ColorRGBA.White);
        rootNode.attachChild(secondCamNode);

        attachPlaneCamViewport();
    }

    public Camera getSecondCamera(){
        return this.secondCam;
    }

    public CameraNode getSecondCameraNode(){
        return this.secondCamNode;
    }

    public void updateCamera(CustomDualViewCallback customCameraUpdate){
        if(this.getCamera() != null && this.getCameraNode() != null && this.mustKeepUpdating())
            customCameraUpdate.run(this);
    }

//    public void update() {
//        if(!this.mustKeepUpdating()) return;
//
//        super.update();
//
//        this.secondCam.resize(this.width, this.height/2, false);
//
//    }
}

interface CustomDualViewCallback {
    void run(CustomDualView cv);
}
