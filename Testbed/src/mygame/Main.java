package mygame;

import com.jme3.app.LegacyApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

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
                final CustomCanvas[] previousCanvas = {null};
                Map<JPanel, CustomCanvas> canvasLinks = new HashMap<JPanel, CustomCanvas>();

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

                JPanel panel1 = new JPanel();
                Canvas c = ctx.getCanvas();
                panel1.add(new Panel());
                panel1.add(c);
                previousCanvas[0] = canvasApplication;
                canvasLinks.put(panel1, canvasApplication);
                JButton playButton = new JButton("start");
                playButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        World world = canvasApplication.getWorld();
                        if (world.isPaused()) world.continueSimulation();
                        }});
                panel1.add(playButton);
                JButton pauzeButton = new JButton("stop");
                pauzeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        World world = canvasApplication.getWorld();
                        if (!world.isPaused()) world.pauseSimulation();
                        }});
                panel1.add(pauzeButton);
                tabbedPane.addTab("Regular view", null, panel1);

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

                        JPanel panel3 = new JPanel();
                        panel3.add(new Panel());
                        tabbedPane.addTab("Chase cam", null, panel3);

                        canvasLinks.put(panel3, canvasApplication.chaseCameraCustomView);

                        tabbedPane.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                JPanel currentJPanel = (JPanel) tabbedPane.getSelectedComponent();
                                canvasLinks.forEach((key, value) -> key.removeAll());
                                if(canvasLinks.containsKey(currentJPanel)) {
                                    CustomCanvas currentCanvas = canvasLinks.get(currentJPanel);
                                    Canvas currentRealCanvas = ((JmeCanvasContext) currentCanvas.getContext()).getCanvas();
                                    previousCanvas[0].deselectView();

                                    currentCanvas.selectView();
                                    currentJPanel.add(currentRealCanvas);
                                    previousCanvas[0] = currentCanvas;

                                }
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
