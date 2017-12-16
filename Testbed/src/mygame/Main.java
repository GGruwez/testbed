package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

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
                    window.add(new CubeUI(app));
                }
            });
            app.start();
        }else {
            java.awt.EventQueue.invokeLater(() -> {
                final CustomCanvas[] previousCanvas = {null};
                LinkedList<JPanel> canvasPanels = new LinkedList<JPanel>();

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


                JTabbedPane tabbedPane = new JTabbedPane();

                JPanel panel1 = new JPanel();
                panel1.add(new Panel());
                Canvas c = ctx.getCanvas();
                panel1.add(c);
                previousCanvas[0] = canvasApplication;
                canvasPanels.add(panel1);
                tabbedPane.addTab("Regular view", null, panel1);

                Callback aai = new Callback() {

                    @Override
                    public void run() {

                        JPanel panel3 = new JPanel();
                        panel3.add(new Panel());
                        tabbedPane.addTab("Chase cam", null, panel3);
                        canvasPanels.add(panel3);
                        JPanel panel4 = new JPanel();
                        panel3.add(new Panel());
                        tabbedPane.addTab("Top down cam", null, panel4);
                        canvasPanels.add(panel4);

                        tabbedPane.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                JPanel newJPanel = (JPanel) tabbedPane.getSelectedComponent();
                                canvasPanels.forEach(panel -> panel.removeAll());

                                previousCanvas[0].deselectView();

                                CustomCanvas newCanvas;

                                switch(tabbedPane.getSelectedIndex()){
                                    case 1 :{
                                        // Chase cam
                                        newCanvas = canvasApplication.createAndGetChaseCameraCustomView();
                                        break;
                                    }
                                    case 2 :{
                                        // Top down cam
                                        newCanvas = canvasApplication.createAndGetTopDownCameraCustomView();
                                        break;
                                    }
                                    default:{
                                        newCanvas = canvasApplication;
                                    }
                                }

                                newCanvas.selectView();
                                Canvas newRealCanvas = ((JmeCanvasContext) newCanvas.getContext()).getCanvas();
                                newJPanel.add(newRealCanvas);
                                previousCanvas[0] = newCanvas;
                            }
                        });
                    }
                };
                canvasApplication.addCallBackAfterAppInit(aai);


                window.setLayout(new BoxLayout(window.getContentPane(),BoxLayout.X_AXIS));
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tabbedPane);
                window.add(panel);
                JPanel buttonPanel = new JPanel();
                JButton playButton = new JButton("start");
                playButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        World world = canvasApplication.getWorld();
                        if (world.isPaused()) world.continueSimulation();
                        }});
                JButton pauzeButton = new JButton("stop");
                pauzeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        World world = canvasApplication.getWorld();
                        if (!world.isPaused()) world.pauseSimulation();
                        }});
                JButton b = new JButton("generate cylinder");
                        b.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {canvasApplication.getWorld().generateCylinder();}
                        });
                JButton b1 = new JButton("read from file");
                b1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {canvasApplication.getWorld().generateCubes(canvasApplication.getWorld().readFile("cubePositions.txt"));}
                    });
                JButton b2 = new JButton("save config");
                b2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {canvasApplication.getWorld().writeFile("cubePositions");}
                    });
                buttonPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.NONE;

                gbc.gridy = 0;
                buttonPanel.add(playButton, gbc);

                gbc.gridy = 1;
                buttonPanel.add(pauzeButton, gbc);

                gbc.gridy = 2;
                buttonPanel.add(b, gbc);

                gbc.gridy = 3;
                buttonPanel.add(b1, gbc);

                gbc.gridy = 4;
                buttonPanel.add(b2, gbc);

                gbc.gridy = 5;
                buttonPanel.add(new CubeUI(canvasApplication), gbc);

                gbc.gridy = 6;
                buttonPanel.add(new CubeGeneratorUI(canvasApplication), gbc);
                window.add(buttonPanel);
                window.pack();
                window.setVisible(true);

//                canvasApplication.start(JmeContext.Type.Canvas);
            });
        }
    }
}
