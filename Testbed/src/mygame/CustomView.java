package mygame;

import com.jme3.app.LegacyApplication;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.profile.AppStep;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;

import java.awt.*;

public class CustomView extends LegacyApplication {
    protected Node rootNode;
    protected Node guiNode;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected boolean showSettings;

    private boolean keepUpdating = false;

    private MainSwingCanvas mainCanvas;

    public CustomView(MainSwingCanvas mainCanvas) {
        this(mainCanvas.getRootNode(), mainCanvas.getGuiNode(), new AppState[]{new CustomViewStatsAppState()});
        this.mainCanvas = mainCanvas;
    }

    public CustomView(Node rootNode, Node guiNode, AppState... initialStates) {
        super(initialStates);
        this.rootNode = rootNode;
        this.guiNode = new Node("Custom View Gui Node");
        this.showSettings = true;
        super.inputEnabled = false;

        int width = 1024;
        int height = 768;

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
        super.initialize();
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

    public void simpleInitApp(){};

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
}
