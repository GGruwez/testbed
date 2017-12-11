package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeContext;

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
                       

                        JComponent panel3 = new JPanel();
                        panel3.add(new Panel());
                        tabbedPane.addTab("Chase cam", null, panel3);


                        canvasManager.put(1, ((JmeCanvasContext) canvasApplication.chaseCameraCustomView.getContext()).getCanvas());


                        tabbedPane.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                int currentIndex = tabbedPane.getSelectedIndex();
//                                panel1.remove(canvasManager.getActiveCanvas());
//                                panel1.add(canvasManager.getCanvas(currentIndex));
                                switch (currentIndex){
                                    case 0:{
                                        // Show normal view
                                        canvasApplication.chaseCameraCustomView.deselectView();
                                        panel3.remove(((JmeCanvasContext) canvasApplication.chaseCameraCustomView.getContext()).getCanvas());
                                        canvasApplication.selectView();
                                        panel1.add(ctx.getCanvas());
                                        break;
                                    }
                                    case 2:{
                                        // Show chase cam
                                        canvasApplication.deselectView();
                                        panel1.remove(ctx.getCanvas());
                                        canvasApplication.chaseCameraCustomView.selectView();
                                        panel3.add(((JmeCanvasContext) canvasApplication.chaseCameraCustomView.getContext()).getCanvas());
                                        break;
                                    }
                                }
                            }
                        });
                    }
                };
                canvasApplication.addCallBackAfterAppInit(aai);

                panel.add(tabbedPane);
                window.setLayout(new BoxLayout(window.getContentPane(),BoxLayout.X_AXIS));
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
                buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
                buttonPanel.add(playButton);
                buttonPanel.add(pauzeButton);
                buttonPanel.add(b);
                buttonPanel.add(b1);
                buttonPanel.add(b2);
                buttonPanel.add(new CubeUI(canvasApplication.getWorld()));
                buttonPanel.add(new CubeGeneratorUI(canvasApplication.getWorld()));
                window.add(buttonPanel);
                window.pack();
                window.setVisible(true);

//                canvasApplication.start(JmeContext.Type.Canvas);
            });
        }
    }
}
