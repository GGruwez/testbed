package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeContext;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class Main {
    
    private static boolean USE_CUSTOM_WINDOW = false;

    public static void main(String[] args) {

        if(!USE_CUSTOM_WINDOW) {
            MainSwingCanvas app = new MainSwingCanvas();
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

                JComponent panel2 = new JPanel();
                panel2.add(new JButton("Button"));
                tabbedPane.addTab("Configuration", null, panel2);


                tabbedPane.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int currentIndex = tabbedPane.getSelectedIndex();
                        if (currentIndex == 1){
                            panel1.remove(c);
                        }else if(currentIndex == 0){
                            panel1.add(c);
                        }
                    }
                });

                panel.add(tabbedPane);
                window.add(panel);
                window.pack();
                window.setVisible(true);

                canvasApplication.start(JmeContext.Type.Canvas);
            });
        }
    }
}
