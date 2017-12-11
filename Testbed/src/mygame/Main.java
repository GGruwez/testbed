package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                        World world = canvasApplication.getWorld();
                        JComponent panel2 = new JPanel();
                        panel2.setLayout(new BoxLayout(panel2,BoxLayout.PAGE_AXIS));
                        panel2.add(new CubeUI(world));
                        panel2.add(new CubeGeneratorUI(world));
                        JButton b = new JButton("generate cylinder");
                        b.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {world.generateCylinder();}
                        });
                        panel2.add(new JButton("generate cylinder"));
                        JButton b1 = new JButton("read from file");
                        b1.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {world.generateCubes(world.readFile("cubePositions.txt"));}
                        });
                        panel2.add(b1);
                        JButton b2 = new JButton("save setup");
                        b2.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {world.writeFile("cubePositions");}
                        });
                        panel2.add(b2);
                        
                        tabbedPane.addTab("Configuration", null, panel2);
                        canvasManager.put(1, ((JmeCanvasContext) canvasApplication.chaseCameraCustomView.getContext()).getCanvas());


                        tabbedPane.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
//                                int currentIndex = tabbedPane.getSelectedIndex();
//                                panel1.remove(canvasManager.getActiveCanvas());
//                                panel1.add(canvasManager.getCanvas(currentIndex));
                                canvasApplication.deselectView();
                                panel1.removeAll();
                                canvasApplication.chaseCameraCustomView.selectView();
                                panel2.add(((JmeCanvasContext) canvasApplication.chaseCameraCustomView.getContext()).getCanvas());
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
