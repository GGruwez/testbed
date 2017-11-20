package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
    private RenderCamera sas;
    private Aircraft aircraft;
    private World world;
    private BitmapText aircraftInfo;
    private Log log = new Log();

    private boolean mouseVisible = false;
    
    public static void main(String[] args) {
//        Main app = new Main();
//        app.start();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AppSettings settings = new AppSettings(true);
                settings.setWidth(1024);
                settings.setHeight(768);

                SwingCanvasTest canvasApplication = new SwingCanvasTest();
                canvasApplication.setSettings(settings);
                canvasApplication.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) canvasApplication.getContext();
                ctx.setSystemListener(canvasApplication);
                Dimension dim = new Dimension(1024, 768);
                ctx.getCanvas().setPreferredSize(dim);

                JFrame window = new JFrame("Swing Application");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel panel = new JPanel(new FlowLayout()); // a panel
                // add all your Swing components ...
                panel.add(new JButton("Some Swing Component"));

                // add the JME canvas
                panel.add(ctx.getCanvas());

                window.add(panel);
                window.pack();
                window.setVisible(true);

                canvasApplication.start(JmeContext.Type.Canvas);
            }
        });
    }
}
