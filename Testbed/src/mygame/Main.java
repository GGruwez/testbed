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

            MenuBar mb=new MenuBar();
            Menu menu=new Menu("File");
            MenuItem loadDrones =new MenuItem("Load drones");
            MenuItem loadJobs = new MenuItem("Load jobs");
            MenuItem loadCubes =new MenuItem("Load cubes");
            MenuItem saveConfig =new MenuItem("Save config");
            menu.add(loadDrones);
            menu.add(loadJobs);
            menu.add(loadCubes);
            menu.add(saveConfig);
            mb.add(menu);
            window.setMenuBar(mb);


            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel panel1 = new JPanel();
            panel1.add(new Panel());
            Canvas c = ctx.getCanvas();
            panel1.add(c);
            previousCanvas[0] = canvasApplication;
            canvasPanels.add(panel1);
            tabbedPane.addTab("Regular view", null, panel1);

            JComboBox<Aircraft> aircraftComboBox = new JComboBox<>();
            JComboBox<Airport> airportComboBox = new JComboBox<>();
            JComboBox<Integer> gateComboBox = new JComboBox<>();

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

            Callback onButtonClick = new Callback() {
                @Override
                public void run() {
                    if (tabbedPane.getSelectedIndex() != 0)
                        tabChangeListener.stateChanged(null);
                }
            };

            Callback aai = new Callback() {

                private void updateAircraftComboBox(){
                    aircraftComboBox.removeAllItems();
                    for(Aircraft ac: canvasApplication.getWorld().getCollectionOfAircraft())
                        aircraftComboBox.addItem(ac);
                    aircraftComboBox.setSelectedIndex(aircraftComboBox.getItemCount()-1); // Select last aircraft
                }

                private void updateAirportComboBox(){
                    airportComboBox.removeAllItems();
                    for(Airport airport : canvasApplication.getWorld().getAirports()){
                        airportComboBox.addItem(airport);
                    }

                    gateComboBox.removeAllItems();
                    int i = 0;
                    while(i<=1){
                        gateComboBox.addItem(i);
                        i++;
                    }
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
                    canvasApplication.getWorld().addAirportAddedListener(() -> updateAirportComboBox());
                    updateAircraftComboBox();
                    updateAirportComboBox();

                    loadDrones.addActionListener((e)-> canvasApplication.loadDronesFromFile("drones.txt"));
                    loadJobs.addActionListener((e) -> canvasApplication.getWorld().loadPackagesFromFile("jobs.txt"));
                    loadCubes.addActionListener(e -> {
                        canvasApplication.getWorld().setPath(canvasApplication.getWorld().readFile("path.txt"));
                        onButtonClick.run();
                    });
                    saveConfig.addActionListener(e -> canvasApplication.getWorld().writeFile("cubePositions.txt"));
                }
            };
            canvasApplication.addCallBackAfterAppInit(aai);

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
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

            JFrame minimapFrame = new JFrame("minimap");
            
            minimapFrame.add(new Minimap(canvasApplication));
            minimapFrame.setAlwaysOnTop(true);
            minimapFrame.setVisible(true);
            minimapFrame.pack();
            minimapFrame.setResizable(false);
            minimapFrame.setDefaultCloseOperation(0);
            minimapFrame.setLocationRelativeTo(null);
            minimapFrame.setLocation(1305, 0);

            gridBagConstraints.gridy = 0;
            JTabbedPane rightTabbedPane = new JTabbedPane();
            JPanel basicPanel = new JPanel(new GridBagLayout());
            JPanel jobsPanel = new JPanel(new GridBagLayout());
            rightTabbedPane.addTab("Basic", null, basicPanel);
            rightTabbedPane.addTab("Jobs", null, jobsPanel);
            buttonPanel.add(rightTabbedPane, gridBagConstraints);
            
            
            gridBagConstraints.gridy++;
            JPanel startStopButtonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints startStopButtonPanelGridBagConstraints = new GridBagConstraints();
            startStopButtonPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            startStopButtonPanelGridBagConstraints.weightx = 0.5;
            startStopButtonPanelGridBagConstraints.gridx = 0;
            startStopButtonPanelGridBagConstraints.gridy = 0;
            startStopButtonPanel.add(playButton, startStopButtonPanelGridBagConstraints);
            startStopButtonPanelGridBagConstraints.gridx = 1;
            startStopButtonPanel.add(stopButton, startStopButtonPanelGridBagConstraints);
            basicPanel.add(startStopButtonPanel, gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(generateCylinderButton, gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(new CubeUI(canvasApplication, onButtonClick), gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(new CubeGeneratorUI(canvasApplication, onButtonClick), gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(new JLabel("Select aircraft:"), gridBagConstraints);
            gridBagConstraints.gridy++;
            basicPanel.add(aircraftComboBox, gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(new JLabel("Add aircraft:"), gridBagConstraints);
            gridBagConstraints.gridy++;
            basicPanel.add(airportComboBox, gridBagConstraints);
            gridBagConstraints.gridy++;
            basicPanel.add(gateComboBox, gridBagConstraints);

            gridBagConstraints.gridy++;
            JButton addAircraftButton = new JButton("Add aircraft");
            addAircraftButton.addActionListener(e -> canvasApplication.addNewAircraft((Airport)airportComboBox.getSelectedItem(), (int)gateComboBox.getSelectedItem()));
            basicPanel.add(addAircraftButton, gridBagConstraints);

            gridBagConstraints.gridy++;
            basicPanel.add(new JLabel("Simulation period multiplier:"), gridBagConstraints);
            gridBagConstraints.gridy++;
            JTextField changeSimulationPeriodMultiplierTextField = new JTextField(Float.toString(World.DEFAULT_SIMULATION_PERIOD_MULTIPLIER));
            changeSimulationPeriodMultiplierTextField.addActionListener(e -> {
                try {
                    float newSimulationPeriodMultiplier = Float.parseFloat(changeSimulationPeriodMultiplierTextField.getText());
                    canvasApplication.getWorld().setSimulationPeriodMultiplier(newSimulationPeriodMultiplier);
                } catch (NumberFormatException ex) {}
            });
            basicPanel.add(changeSimulationPeriodMultiplierTextField, gridBagConstraints);

            gridBagConstraints.gridy++;
            JLabel infoLabel = new JLabel();
            buttonPanel.add(infoLabel, gridBagConstraints);
            canvasApplication.addUpdateListener(() -> {
                updateInfoLabel(canvasApplication, infoLabel);
            });
            canvasApplication.addUpdateListener(() -> {
                minimapFrame.repaint();
            });
            aircraftComboBox.addActionListener(e -> {
                Aircraft selectedAircraft = (Aircraft) aircraftComboBox.getSelectedItem();
                if(selectedAircraft != null) {
                    canvasApplication.getWorld().setSelectedAircraft(selectedAircraft);
                    updateInfoLabel(canvasApplication, infoLabel);
                }
            });

            gridBagConstraints.gridy++;
            jobsPanel.add(new PackageGUI(canvasApplication));




            window.add(buttonPanel);
            window.pack();
            window.setVisible(true);
        });
    }

    private static void updateInfoLabel(MainSwingCanvas canvasApplication, JLabel infoLabel) {
        infoLabel.setText("<html>" + canvasApplication.getAircraftInfo().replace("\r\n", "<br>") + "</html>");
    }
    
    
}
