package mygame;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;

public class CustomViewStatsAppState extends AbstractAppState {
    private Application app;
    protected StatsView statsView;
    protected boolean showSettings = true;
    private boolean showFps = true;
    private boolean showStats = true;
    private boolean darkenBehind = true;
    protected Node guiNode;
    protected float secondCounter = 0.0F;
    protected int frameCounter = 0;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected Geometry darkenFps;
    protected Geometry darkenStats;

    public CustomViewStatsAppState() {
    }

    public CustomViewStatsAppState(Node guiNode, BitmapFont guiFont) {
        this.guiNode = guiNode;
        this.guiFont = guiFont;
    }

    public void setFont(BitmapFont guiFont) {
        this.guiFont = guiFont;
        this.fpsText = new BitmapText(guiFont, false);
    }

    public BitmapText getFpsText() {
        return this.fpsText;
    }

    public StatsView getStatsView() {
        return this.statsView;
    }

    public float getSecondCounter() {
        return this.secondCounter;
    }

    public void toggleStats() {
        this.setDisplayFps(!this.showFps);
        this.setDisplayStatView(!this.showStats);
    }

    public void setDisplayFps(boolean show) {
        this.showFps = show;
        if(this.fpsText != null) {
            this.fpsText.setCullHint(show?CullHint.Never:CullHint.Always);
            if(this.darkenFps != null) {
                this.darkenFps.setCullHint(this.showFps && this.darkenBehind?CullHint.Never:CullHint.Always);
            }
        }

    }

    public void setDisplayStatView(boolean show) {
        this.showStats = show;
        if(this.statsView != null) {
            this.statsView.setEnabled(show);
            this.statsView.setCullHint(show?CullHint.Never:CullHint.Always);
            if(this.darkenStats != null) {
                this.darkenStats.setCullHint(this.showStats && this.darkenBehind?CullHint.Never:CullHint.Always);
            }
        }

    }

    public void setDarkenBehind(boolean darkenBehind) {
        this.darkenBehind = darkenBehind;
        this.setEnabled(this.isEnabled());
    }

    public boolean isDarkenBehind() {
        return this.darkenBehind;
    }

    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        if(app instanceof CustomView) {
            CustomView simpleApp = (CustomView)app;
            if(this.guiNode == null) {
                this.guiNode = simpleApp.guiNode;
            }

            if(this.guiFont == null) {
                this.guiFont = simpleApp.guiFont;
            }
        }

        if(this.guiNode == null) {
            throw new RuntimeException("No guiNode specific and cannot be automatically determined.");
        } else {
            if(this.guiFont == null) {
                this.guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
            }

            this.loadFpsText();
            this.loadStatsView();
            this.loadDarken();
        }
    }

    public void loadFpsText() {
        if(this.fpsText == null) {
            this.fpsText = new BitmapText(this.guiFont, false);
        }

        this.fpsText.setLocalTranslation(0.0F, this.fpsText.getLineHeight(), 0.0F);
        this.fpsText.setText("Frames per second");
        this.fpsText.setCullHint(this.showFps?CullHint.Never:CullHint.Always);
        this.guiNode.attachChild(this.fpsText);
    }

    public void loadStatsView() {
        this.statsView = new StatsView("Statistics View", this.app.getAssetManager(), this.app.getRenderer().getStatistics());
        this.statsView.setLocalTranslation(0.0F, this.fpsText.getLineHeight(), 0.0F);
        this.statsView.setEnabled(this.showStats);
        this.statsView.setCullHint(this.showStats?CullHint.Never:CullHint.Always);
        this.guiNode.attachChild(this.statsView);
    }

    public void loadDarken() {
        Material mat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.0F, 0.0F, 0.0F, 0.5F));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        this.darkenFps = new Geometry("StatsDarken", new Quad(200.0F, this.fpsText.getLineHeight()));
        this.darkenFps.setMaterial(mat);
        this.darkenFps.setLocalTranslation(0.0F, 0.0F, -1.0F);
        this.darkenFps.setCullHint(this.showFps && this.darkenBehind?CullHint.Never:CullHint.Always);
        this.guiNode.attachChild(this.darkenFps);
        this.darkenStats = new Geometry("StatsDarken", new Quad(200.0F, this.statsView.getHeight()));
        this.darkenStats.setMaterial(mat);
        this.darkenStats.setLocalTranslation(0.0F, this.fpsText.getHeight(), -1.0F);
        this.darkenStats.setCullHint(this.showStats && this.darkenBehind?CullHint.Never:CullHint.Always);
        this.guiNode.attachChild(this.darkenStats);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            this.fpsText.setCullHint(this.showFps?CullHint.Never:CullHint.Always);
            this.darkenFps.setCullHint(this.showFps && this.darkenBehind?CullHint.Never:CullHint.Always);
            this.statsView.setEnabled(this.showStats);
            this.statsView.setCullHint(this.showStats?CullHint.Never:CullHint.Always);
            this.darkenStats.setCullHint(this.showStats && this.darkenBehind?CullHint.Never:CullHint.Always);
        } else {
            this.fpsText.setCullHint(CullHint.Always);
            this.darkenFps.setCullHint(CullHint.Always);
            this.statsView.setEnabled(false);
            this.statsView.setCullHint(CullHint.Always);
            this.darkenStats.setCullHint(CullHint.Always);
        }

    }

    public void update(float tpf) {
        if(this.showFps) {
            this.secondCounter += this.app.getTimer().getTimePerFrame();
            ++this.frameCounter;
            if(this.secondCounter >= 1.0F) {
                int fps = (int)((float)this.frameCounter / this.secondCounter);
                this.fpsText.setText("Frames per second: " + fps);
                this.secondCounter = 0.0F;
                this.frameCounter = 0;
            }
        }

    }

    public void cleanup() {
        super.cleanup();
        this.guiNode.detachChild(this.statsView);
        this.guiNode.detachChild(this.fpsText);
        this.guiNode.detachChild(this.darkenFps);
        this.guiNode.detachChild(this.darkenStats);
    }
}
