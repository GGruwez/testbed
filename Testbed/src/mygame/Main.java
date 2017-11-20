package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeContext;
import javax.swing.*;
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

                JPanel panel = new JPanel(new FlowLayout()); // a panel
                // add all your Swing components ...
                panel.add(new JButton("Button"));

                // add the JME canvas
                panel.add(ctx.getCanvas());

                window.add(panel);
                window.pack();
                window.setVisible(true);

                canvasApplication.start(JmeContext.Type.Canvas);
            });
        }
    }
}
