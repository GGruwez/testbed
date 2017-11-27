package mygame;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.LegacyApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.profile.AppStep;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.system.JmeContext.Type;

public class CustomView extends LegacyApplication {
    protected Node rootNode;
    protected Node guiNode;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected FlyByCamera flyCam;
    protected boolean showSettings;
    private CustomView.AppActionListener actionListener;

    public CustomView(Node rootNode, Node guiNode) {
        this(rootNode, guiNode, new AppState[]{new StatsAppState(), new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState()});
    }

    public CustomView(Node rootNode, Node guiNode, AppState... initialStates) {
        super(initialStates);
        this.rootNode = rootNode;
        this.guiNode = new Node("Custom View Gui Node");
        this.showSettings = true;
        this.actionListener = new CustomView.AppActionListener();
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

    public Node getGuiNode() {
        return this.guiNode;
    }

    public Node getRootNode() {
        return this.rootNode;
    }

    public boolean isShowSettings() {
        return this.showSettings;
    }

    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
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
        if(this.inputManager != null) {
//            if(this.stateManager.getState(FlyCamAppState.class) != null) {
//                this.flyCam = new FlyByCamera(this.cam);
//                this.flyCam.setMoveSpeed(1.0F);
//                ((FlyCamAppState)this.stateManager.getState(FlyCamAppState.class)).setCamera(this.flyCam);
//            }

            if(this.context.getType() == Type.Display) {
                this.inputManager.addMapping("SIMPLEAPP_Exit", new Trigger[]{new KeyTrigger(1)});
            }

            if(this.stateManager.getState(StatsAppState.class) != null) {
                this.inputManager.addMapping("SIMPLEAPP_HideStats", new Trigger[]{new KeyTrigger(63)});
                this.inputManager.addListener(this.actionListener, new String[]{"SIMPLEAPP_HideStats"});
            }

            this.inputManager.addListener(this.actionListener, new String[]{"SIMPLEAPP_Exit"});
        }

        if(this.stateManager.getState(StatsAppState.class) != null) {
            ((StatsAppState)this.stateManager.getState(StatsAppState.class)).setFont(this.guiFont);
            this.fpsText = ((StatsAppState)this.stateManager.getState(StatsAppState.class)).getFpsText();
        }

        this.simpleInitApp();
    }

    public void update() {
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
            if(this.prof != null) {
                this.prof.appStep(AppStep.EndFrame);
            }

        }
    }

    public void setDisplayFps(boolean show) {
        if(this.stateManager.getState(StatsAppState.class) != null) {
            ((StatsAppState)this.stateManager.getState(StatsAppState.class)).setDisplayFps(show);
        }

    }

    public void setDisplayStatView(boolean show) {
        if(this.stateManager.getState(StatsAppState.class) != null) {
            ((StatsAppState)this.stateManager.getState(StatsAppState.class)).setDisplayStatView(show);
        }

    }

    public void simpleInitApp(){};

    public void simpleUpdate(float tpf) {
    }

    public void simpleRender(RenderManager rm) {
    }

    private class AppActionListener implements ActionListener {
        private AppActionListener() {
        }

        public void onAction(String name, boolean value, float tpf) {
            if(value) {
                if(name.equals("SIMPLEAPP_Exit")) {
                    CustomView.this.stop();
                } else if(name.equals("SIMPLEAPP_HideStats") && CustomView.this.stateManager.getState(StatsAppState.class) != null) {
                    ((StatsAppState)CustomView.this.stateManager.getState(StatsAppState.class)).toggleStats();
                }

            }
        }
    }
}
