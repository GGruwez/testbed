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

    public static void main(String[] args) {

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

            JComboBox<Aircraft> aircraftComboBox = new JComboBox<>();

            ChangeListener tabChangeListener = new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    JPanel newJPanel = (JPanel) tabbedPane.getSelectedComponent();
                    canvasPanels.forEach(panel -> panel.removeAll());

                    previousCanvas[0].deselectView();

                    CustomCanvas newCanvas;

                    switch (tabbedPane.getSelectedIndex()) {
                        case 1: {
                            // Chase cam
                            newCanvas = canvasApplication.createAndGetChaseCameraCustomView();
                            break;
                        }
                        case 2: {
                            // Top down cam
                            newCanvas = canvasApplication.createAndGetTopDownCameraCustomView();
                            break;
                        }
                        default: {
                            newCanvas = canvasApplication;
                            // Render camera
                            canvasApplication.renderCamera = new RenderCamera(canvasApplication.getSelectedAircraft().getCamera(), settings.getWidth(), settings.getHeight(), canvasApplication.getSelectedAircraft());
                            canvasApplication.renderCamera.initialize(canvasApplication.getStateManager(), canvasApplication);
                        }
                    }

                    newCanvas.selectView();
                    Canvas newRealCanvas = ((JmeCanvasContext) newCanvas.getContext()).getCanvas();
                    newJPanel.add(newRealCanvas);
                    previousCanvas[0] = newCanvas;
                }
            };

            Callback aai = new Callback() {

                private void updateAircraftComboBox(){
                    aircraftComboBox.removeAllItems();
                    for(Aircraft ac: canvasApplication.getWorld().getCollectionOfAircraft())
                        aircraftComboBox.addItem(ac);
                    aircraftComboBox.setSelectedIndex(aircraftComboBox.getItemCount()-1); // Select last aircraft
                }

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

                    tabbedPane.addChangeListener(tabChangeListener);

                    canvasApplication.getWorld().addAircraftAddedListener(() -> updateAircraftComboBox());
                    updateAircraftComboBox();
                }
            };
            canvasApplication.setCallBackAfterAppInit(aai);

            Callback onButtonClick = new Callback() {
                @Override
                public void run() {
                    if (tabbedPane.getSelectedIndex() != 0)
                        tabChangeListener.stateChanged(null);
                }
            };

            window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.X_AXIS));
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tabbedPane);
            window.add(panel);


            JPanel buttonPanel = new JPanel(new GridBagLayout());
            JButton playButton = new JButton("start");
            playButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    World world = canvasApplication.getWorld();
                    if (world.isPaused()) {
                        world.continueSimulation();
                        onButtonClick.run();
                    }
                }
            });
            JButton stopButton = new JButton("stop");
            stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    World world = canvasApplication.getWorld();
                    if (!world.isPaused()) world.pauseSimulation();
                }
            });
            JButton generateCylinderButton = new JButton("generate cylinder");
            generateCylinderButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvasApplication.getWorld().generateCylinder();
                    onButtonClick.run();
                }
            });
            JButton readFromFileButton = new JButton("read from file");
            readFromFileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvasApplication.getWorld().setPath(canvasApplication.getWorld().readFile("path.txt"));
                    onButtonClick.run();
                }
            });
            JButton saveConfigButton = new JButton("save config");
            saveConfigButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvasApplication.getWorld().writeFile("cubePositions.txt");
                }
            });
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

            JPanel minimap = new JPanel();
            minimap.setBackground(Color.GREEN);
            minimap.setPreferredSize(new Dimension(250,250));
            buttonPanel.add(minimap);
            canvasApplication.setupMiniMap(minimap);
            
            gridBagConstraints.gridy = 0;
            JPanel startStopButtonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints startStopButtonPanelGridBagConstraints = new GridBagConstraints();
            startStopButtonPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            startStopButtonPanelGridBagConstraints.weightx = 0.5;
            startStopButtonPanelGridBagConstraints.gridx = 0;
            startStopButtonPanelGridBagConstraints.gridy = 0;
            startStopButtonPanel.add(playButton, startStopButtonPanelGridBagConstraints);
            startStopButtonPanelGridBagConstraints.gridx = 1;
            startStopButtonPanel.add(stopButton, startStopButtonPanelGridBagConstraints);
            buttonPanel.add(startStopButtonPanel, gridBagConstraints);

            gridBagConstraints.gridy = 1;
            buttonPanel.add(generateCylinderButton, gridBagConstraints);

            gridBagConstraints.gridy = 2;
            buttonPanel.add(readFromFileButton, gridBagConstraints);

            gridBagConstraints.gridy = 3;
            buttonPanel.add(saveConfigButton, gridBagConstraints);

            gridBagConstraints.gridy = 4;
            buttonPanel.add(new CubeUI(canvasApplication, onButtonClick), gridBagConstraints);

            gridBagConstraints.gridy = 5;
            buttonPanel.add(new CubeGeneratorUI(canvasApplication, onButtonClick), gridBagConstraints);

            gridBagConstraints.gridy = 6;
            buttonPanel.add(aircraftComboBox, gridBagConstraints);

            gridBagConstraints.gridy = 7;
            JButton addAircraftButton = new JButton("Add aircraft");
            addAircraftButton.addActionListener(e -> canvasApplication.addNewAircraft());
            buttonPanel.add(addAircraftButton, gridBagConstraints);

            gridBagConstraints.gridy = 8;
            JTextField changeSimulationPeriodMultiplierTextField = new JTextField(Float.toString(World.DEFAULT_SIMULATION_PERIOD_MULTIPLIER));
            changeSimulationPeriodMultiplierTextField.addActionListener(e -> {
                try {
                    float newSimulationPeriodMultiplier = Float.parseFloat(changeSimulationPeriodMultiplierTextField.getText());
                    canvasApplication.getWorld().setSimulationPeriodMultiplier(newSimulationPeriodMultiplier);
                } catch (NumberFormatException ex) {}
            });
            buttonPanel.add(changeSimulationPeriodMultiplierTextField, gridBagConstraints);

            gridBagConstraints.gridy = 9;
            JLabel infoLabel = new JLabel();
            buttonPanel.add(infoLabel, gridBagConstraints);
            canvasApplication.addUpdateListener(() -> {
                updateInfoLabel(canvasApplication, infoLabel);
            });
            aircraftComboBox.addActionListener(e -> {
                Aircraft selectedAircraft = (Aircraft) aircraftComboBox.getSelectedItem();
                if(selectedAircraft != null) {
                    canvasApplication.getWorld().setSelectedAircraft(selectedAircraft);
                    updateInfoLabel(canvasApplication, infoLabel);
                }
            });
            
            
            
            window.add(buttonPanel);
            window.pack();
            window.setVisible(true);
        });
    }

    private static void updateInfoLabel(MainSwingCanvas canvasApplication, JLabel infoLabel) {
        infoLabel.setText("<html>" + canvasApplication.getAircraftInfo().replace("\r\n", "<br>") + "</html>");
    }
}
