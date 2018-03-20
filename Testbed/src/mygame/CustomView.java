package mygame;

import com.jme3.app.LegacyApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.profile.AppStep;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;

import java.awt.*;

public class CustomView extends LegacyApplication implements CustomCanvas {
    protected Node rootNode;
    protected Node guiNode;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected boolean showSettings;

    protected int width;
    protected int height;

    private boolean keepUpdating = false;

    protected MainSwingCanvas mainCanvas;

    private CameraNode camNode;

    public CustomView(MainSwingCanvas mainCanvas) {
        this(mainCanvas.getRootNode(), mainCanvas.getGuiNode(), new AppState[]{new CustomViewStatsAppState()});
        this.mainCanvas = mainCanvas;
    }

    public CustomView(Node rootNode, Node guiNode, AppState... initialStates) {
        super(initialStates);
        this.rootNode = rootNode;
        this.guiNode = new Node("Custom View Gui Node");
        this.showSettings = false;
        super.inputEnabled = false;
        this.setDisplayFps(false);
        this.setDisplayStatView(false);

        this.width = 1024;
        this.height = 768;

        AppSettings settings = new AppSettings(true);
        settings.setWidth(width);
        settings.setHeight(height);
        this.setSettings(settings);
        this.createCanvas(); // create canvas!
        JmeCanvasContext ctx = (JmeCanvasContext) this.getContext();
        ctx.setSystemListener(this);
        Dimension dim = new Dimension(width, height);
        ctx.getCanvas().setPreferredSize(dim);
    }

    public void start() {
        boolean loadSettings = false;
        if(this.settings == null) {
            this.setSettings(new AppSettings(true));
            loadSettings = true;
        }

        if(!this.showSettings || JmeSystem.showSettingsDialog(this.settings, loadSettings)) {
            this.setSettings(this.settings);
            super.start();
        }
    }

    protected BitmapFont loadGuiFont() {
        return this.assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    public void initialize() {
        try {
            super.initialize();
        }catch(IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
        this.guiFont = this.loadGuiFont();
        this.guiNode.setQueueBucket(Bucket.Gui);
        this.guiNode.setCullHint(CullHint.Never);
        this.viewPort.attachScene(this.rootNode);
        this.guiViewPort.attachScene(this.guiNode);

        if(this.stateManager.getState(CustomViewStatsAppState.class) != null) {
            ((CustomViewStatsAppState)this.stateManager.getState(CustomViewStatsAppState.class)).setFont(this.guiFont);
            this.fpsText = ((CustomViewStatsAppState)this.stateManager.getState(CustomViewStatsAppState.class)).getFpsText();
        }

        this.simpleInitApp();
    }

    public void updateCamera(CustomViewCallback customCameraUpdate){
        if(this.getCamera() != null && this.getCameraNode() != null && this.mustKeepUpdating())
            customCameraUpdate.run(this);
    }

    public void update() {
        if(!this.keepUpdating) return;

        if(this.prof != null) {
            this.prof.appStep(AppStep.BeginFrame);
        }

        super.update();
        if(this.speed != 0.0F && !this.paused) {
            float tpf = this.timer.getTimePerFrame() * this.speed;
            if(this.prof != null) {
                this.prof.appStep(AppStep.StateManagerUpdate);
            }

            this.stateManager.update(tpf);
            this.simpleUpdate(tpf);
            if(this.prof != null) {
                this.prof.appStep(AppStep.SpatialUpdate);
            }

            this.rootNode.updateLogicalState(tpf);
            this.guiNode.updateLogicalState(tpf);
            this.rootNode.updateGeometricState();
            this.guiNode.updateGeometricState();
            if(this.prof != null) {
                this.prof.appStep(AppStep.StateManagerRender);
            }

            this.stateManager.render(this.renderManager);
            if(this.prof != null) {
                this.prof.appStep(AppStep.RenderFrame);
            }

            this.renderManager.render(tpf, this.context.isRenderable());
            this.simpleRender(this.renderManager);
            this.stateManager.postRender();
            if (this.prof != null) {
                this.prof.appStep(AppStep.EndFrame);
            }

        }
    }

    public void setDisplayFps(boolean show) {
        if(this.stateManager.getState(CustomViewStatsAppState.class) != null) {
            ((CustomViewStatsAppState)this.stateManager.getState(CustomViewStatsAppState.class)).setDisplayFps(show);
        }

    }

    public void setDisplayStatView(boolean show) {
        if(this.stateManager.getState(CustomViewStatsAppState.class) != null) {
            ((CustomViewStatsAppState)this.stateManager.getState(CustomViewStatsAppState.class)).setDisplayStatView(show);
        }

    }

    public void simpleInitApp(){
        // Create camera
        this.cam = new Camera(width, height);
        this.cam.setFrustumPerspective(120,1,1,1000); // TODO: change this..? aspect...
        this.camNode = new CameraNode("Cam Node", this.cam);
        this.camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        rootNode.attachChild(this.camNode);

        ViewPort camViewport = renderManager.createMainView("cam view", this.cam);
        camViewport.setClearFlags(true, true, true);
        camViewport.attachScene(rootNode);
        camViewport.setBackgroundColor(ColorRGBA.White);
        this.viewPort = camViewport;

        mainCanvas.addLodControlToTerrain(this.cam);

        attachPlaneCamViewport();

    };

    protected void attachPlaneCamViewport(){
        // Create black border material
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.8f, 0.8f, 0.8f, 0));

        // Create border
        int cameraWidth = mainCanvas.getSelectedAircraft().getCamera().getWidth() + 2;
        int cameraHeight = mainCanvas.getSelectedAircraft().getCamera().getHeight();
        int horizontalOffset = -23;
        // top
        Geometry rectTop = new Geometry("rectTop", new Quad(cameraWidth, 1));
        rectTop.setMaterial(mat);
        guiNode.attachChild(rectTop);
        guiNode.getChild("rectTop").setLocalTranslation(settings.getWidth()+horizontalOffset-cameraWidth, cameraHeight, 10);
        // left
        Geometry rectLeft = new Geometry("rectLeft", new Quad(1, cameraHeight));
        rectLeft.setMaterial(mat);
        guiNode.attachChild(rectLeft);
        guiNode.getChild("rectLeft").setLocalTranslation(settings.getWidth()+horizontalOffset-cameraWidth, 0, 10);
        // right
        Geometry rectRight = new Geometry("rectRight", new Quad(1, cameraHeight));
        rectRight.setMaterial(mat);
        guiNode.attachChild(rectRight);
        guiNode.getChild("rectRight").setLocalTranslation(settings.getWidth()+horizontalOffset, 0, 10);

        // Plane camera viewport
        ViewPort planeCamViewPort = renderManager.createMainView("planecam view", mainCanvas.getSelectedAircraft().getCamera());
        planeCamViewPort.setClearFlags(true, true, true);
        planeCamViewPort.attachScene(rootNode);
        planeCamViewPort.setBackgroundColor(ColorRGBA.White);
        // Render camera
        mainCanvas.renderCamera = new RenderCamera(mainCanvas.getSelectedAircraft().getCamera(), settings.getWidth(), settings.getHeight(), mainCanvas.getSelectedAircraft());
        mainCanvas.renderCamera.initialize(this.getStateManager(), this);
    }

    public void simpleUpdate(float tpf) {

        this.mainCanvas.simpleUpdate(tpf);
    }

    public void simpleRender(RenderManager rm) {}

    public void selectView(){
        this.keepUpdating = true;
    }

    public void deselectView(){
        this.keepUpdating = false;
    }

    public boolean mustKeepUpdating(){
        return this.keepUpdating;
    }

    CameraNode getCameraNode(){
        return this.camNode;
    }
}

interface CustomViewCallback {
    void run(CustomView cv);
}
