/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.JmeSystem;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RenderCamera extends AbstractAppState implements ActionListener, SceneProcessor {

    private static final Logger logger = Logger.getLogger(RenderCamera.class.getName());
    private String filePath = null;
    private boolean capture = false;
    private boolean numbered = true;
    private Renderer renderer;
    private RenderManager rm;
    private ByteBuffer outBuf;
    private String shotName;
    private long shotIndex = 0;
    private int width, height;
    private AppProfiler prof;

    /**
     * This constructor allows you to specify the output file path of the screenshot.
     * Include the seperator at the end of the path.
     * Use an emptry string to use the application folder. Use NULL to use the system
     * default storage folder.
     * @param filePath The screenshot file path to use. Include the seperator at the end of the path.
     * @param fileName The name of the file to save the screeshot as.
     */
    public RenderCamera(String filePath, String fileName) {
        this.filePath = filePath;
        this.shotName = fileName;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()){
            InputManager inputManager = app.getInputManager();
            inputManager.addMapping("ScreenShot", new KeyTrigger(KeyInput.KEY_SYSRQ));
            inputManager.addListener(this, "ScreenShot");

            List<ViewPort> vps = app.getRenderManager().getPostViews();
            ViewPort last = vps.get(vps.size()-1);
            last.addProcessor(this);

            if (shotName == null) {
                shotName = app.getClass().getSimpleName();
            }
        }

        super.initialize(stateManager, app);
    }

    public void takeScreenshot() {
        capture = true;
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderer = rm.getRenderer();
        this.rm = rm;
        reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized() && renderer != null;
    }

    public void reshape(ViewPort vp, int w, int h) {
        outBuf = BufferUtils.createByteBuffer(w * h * 4);
        width = w;
        height = h;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
    }

    public void postFrame(FrameBuffer out) {
        if (capture){
            capture = false;

            Camera curCamera = rm.getCurrentCamera();
            int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
            int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());
            int viewWidth = (int) ((curCamera.getViewPortRight() - curCamera.getViewPortLeft()) * curCamera.getWidth());
            int viewHeight = (int) ((curCamera.getViewPortTop() - curCamera.getViewPortBottom()) * curCamera.getHeight());

            renderer.setViewPort(0, 0, width, height);
            renderer.readFrameBuffer(out, outBuf);
//            System.out.println(outBuf.array());
//System.out.println(outBuf.hasArray());
//            System.out.println(new String(outBuf.array()));
//            System.out.println(outBuf.remaining());
//            byte[] arr = new byte[outBuf.remaining()];
//            outBuf.get(arr);
//            System.out.println(arr);
            ByteBuffer outBuf2 = outBuf.duplicate();
            System.out.println(outBuf2.capacity());
            outBuf2.flip();
            byte[] bArray = new byte[outBuf2.capacity()];
            System.out.println("Contents into buffer, length : " + outBuf2.capacity());
            while (outBuf2.hasRemaining()) {
//              System.out.print((char) outBuf2.get());
            }
            outBuf2.clear();
            outBuf2.get(bArray, 0, bArray.length);
            System.out.println("Contents into Array, length : " + bArray.length);
            for (int i = 0; i < outBuf2.capacity(); i++) {
//              System.out.print((char) bArray[i]);
//              System.out.print("2");
                if(bArray[i] != 0)
                    System.out.println(Integer.toHexString(bArray[i]) + " - " + bArray[i]);
            }
            
            renderer.setViewPort(viewX, viewY, viewWidth, viewHeight);

//            File file;
//            String filename;
//            if (numbered) {
//                shotIndex++;
//                filename = shotName + shotIndex;
//            } else {
//                filename = shotName;
//            }
//
//            if (filePath == null) {
//                file = new File(JmeSystem.getStorageFolder() + File.separator + filename + ".png").getAbsoluteFile();
//            } else {
//                file = new File(filePath + filename + ".png").getAbsoluteFile();
//            }
//            logger.log(Level.FINE, "Saving ScreenShot to: {0}", file.getAbsolutePath());
//
//            try {
//                writeImageFile(file);
//            } catch (IOException ex) {
//                logger.log(Level.SEVERE, "Error while saving screenshot", ex);
//            }                
        }
    }

    @Override
    public void setProfiler(AppProfiler profiler) {
        this.prof = profiler;
    }

    /**
     *  Called by postFrame() once the screen has been captured to outBuf.
     */
    protected void writeImageFile( File file ) throws IOException {
        OutputStream outStream = new FileOutputStream(file);
        try {
            JmeSystem.writeImageFile(outStream, "png", outBuf, width, height);
        } finally {
            outStream.close();
        }
    } 

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}