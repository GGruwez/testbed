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
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RenderCamera extends AbstractAppState implements SceneProcessor {

    private boolean capture = false;
    private Renderer renderer;
    private ByteBuffer outBuf;
    private int width, height;
    private final Camera cameraToCapture;
    private int totalWidth;
    private int totalHeight;
    private Aircraft aircraft;
    
    public RenderCamera(Camera cameraToCapture, int totalWidth, int totalHeight, Aircraft aircraft){
        this.cameraToCapture = cameraToCapture;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.aircraft = aircraft;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()){

            List<ViewPort> vps = app.getRenderManager().getPostViews();
            ViewPort last = vps.get(vps.size()-1);
            last.addProcessor(this);
        }

        super.initialize(stateManager, app);
    }

    public void grabCamera() {
        capture = true;
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderer = rm.getRenderer();
        this.cameraToCapture.resize(200,200,false);
        int width = (int) ((this.cameraToCapture.getViewPortRight() - this.cameraToCapture.getViewPortLeft()) * this.cameraToCapture.getWidth());
        int height = (int) ((this.cameraToCapture.getViewPortTop() - this.cameraToCapture.getViewPortBottom()) * this.cameraToCapture.getHeight());
        
        reshape(vp, width, height);
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized() && renderer != null;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        outBuf = BufferUtils.createByteBuffer(totalWidth * totalHeight * 3);
        width = w;
        height = h;
    }

    @Override
    public void preFrame(float tpf) {
    }

    @Override
    public void postQueue(RenderQueue rq) {
    }

    @Override
    public void postFrame(FrameBuffer out) {
        if (capture){
            capture = false;
            
            Camera curCamera = this.cameraToCapture;
            int viewX = (int) (curCamera.getViewPortLeft() * curCamera.getWidth());
            int viewY = (int) (curCamera.getViewPortBottom() * curCamera.getHeight());

            renderer.readFrameBufferWithFormat(out, outBuf, Image.Format.RGB8);
            ByteBuffer outBuf2 = outBuf.duplicate();
            outBuf2.flip();
//            System.out.println(outBuf2.capacity());
            byte[] bArray = new byte[outBuf2.capacity()];
            outBuf2.clear();
            outBuf2.get(bArray, 0, bArray.length);
            
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // The following image byte array will be passed to the autopilot
            byte[] imageByteArray = new byte[width*height*3];

            int i = 1;
            for (int y = viewY; y < height+viewY; y++) {
                for (int x = width+viewX; x > viewX ; x--) {
                    // Save image to file
                    // TODO: remove this + cleanup
//                    int r = bArray[(3*(y*totalWidth+x)+0)]& 0xff;
//                    int g = bArray[(3*(y*totalWidth+x)+1)]& 0xff;
//                    int b = bArray[(3*(y*totalWidth+x)+2)]& 0xff;
//                    int rgb = 65536*r + 256*g + b;
//                    image.setRGB(x-viewX-1, height-(y-viewY)-1, rgb);

                    // Fill image byte array
                    imageByteArray[imageByteArray.length - i++] = bArray[3*(y*totalWidth+(x))+0];
                    imageByteArray[imageByteArray.length - i++] = bArray[3*(y*totalWidth+(x))+1];
                    imageByteArray[imageByteArray.length - i++] = bArray[3*(y*totalWidth+(x))+2];
                }
            }
            // Set aircraft image
            this.aircraft.setImage(imageByteArray);

//            File outputFile = new File("output.jpeg");
//            try {
//                ImageIO.write(image, "jpeg", outputFile);
//            }catch (IOException exc) {}

       }
    }
    
    public void setProfiler(AppProfiler profiler) {
     }
}