package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;

public class Main {
    
    private static boolean USE_CUSTOM_WINDOW = true;
    

    public static void main(String[] args) {

        if(!USE_CUSTOM_WINDOW) {
            MainSwingCanvas app = new MainSwingCanvas();
            app.addCallBackAfterAppInit(new Callback() {
                @Override
                public void run() {
                    JFrame window = new JFrame("Testbed");
                    window.setVisible(true);
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    window.setSize(new Dimension(200,200));
                    window.setLocation(10,240);
                    window.add(new CubeUI(app.getWorld()));
                }
            });
            app.start();
        }else {
            java.awt.EventQueue.invokeLater(() -> {
                int width = 1024;
                int height = 768;

                MainSwingCanvas canvasApplication = new MainSwingCanvas();

                AppSettings settings = new AppSettings(true);
                settings.setWidth(width);
                settings.setHeight(height);
                canvasApplication.setSettings(settings);
                canvasApplication.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) canvasApplication.getContext();
                ctx.setSystemListener(canvasApplication);
                Dimension dim = new Dimension(width, height);
                ctx.getCanvas().setPreferredSize(dim);

                JFrame window = new JFrame("Testbed");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel panel = new JPanel(new BorderLayout());

                JTabbedPane tabbedPane = new JTabbedPane();

                JComponent panel1 = new JPanel();
                Canvas c = ctx.getCanvas();
                
                panel1.add(new Panel());
                panel1.add(c);

                tabbedPane.addTab("Regular view", null, panel1);
                CanvasManager canvasManager = new CanvasManager(c);

                Callback aai = new Callback() {

                    @Override
                    public void run() {
                        JComponent panel2 = new JPanel();
                        panel2.add(new CubeUI(canvasApplication.getWorld()));
                        tabbedPane.addTab("Configuration", null, panel2);
                        canvasManager.put(1, ((JmeCanvasContext) canvasApplication.cv.getContext()).getCanvas());


                        tabbedPane.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                int currentIndex = tabbedPane.getSelectedIndex();
                                panel1.remove(canvasManager.getActiveCanvas());
                                panel1.add(canvasManager.getCanvas(currentIndex));
                            }
                        });
                    }
                };
                canvasApplication.addCallBackAfterAppInit(aai);

                panel.add(tabbedPane);
                window.add(panel);
                window.pack();
                window.setVisible(true);

//                canvasApplication.start(JmeContext.Type.Canvas);
            });
        }
    }
}
