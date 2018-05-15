
package mygame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class PackageGUI extends JPanel {
    
    private MainSwingCanvas canvas;

    JComboBox<Airport> fromAirportCombobox;
    JComboBox<Airport> toAirportCombobox;
    JComboBox<Integer> fromGateCombobox;
    JComboBox<Integer> toGateCombobox;
    DefaultListModel listModel;

    private World getWorld(){
        return canvas.getWorld();
    }
    
    public PackageGUI(MainSwingCanvas canvas) {
        super(new GridBagLayout());

        this.canvas = canvas;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        listModel = new DefaultListModel();
        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
//        list.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(200, 80));
        this.add(listScroller, gridBagConstraints);

        JLabel fromAirportLabel = new JLabel("From airport:");
        JLabel fromGateLabel = new JLabel("From gate:");
        fromAirportCombobox = new JComboBox<>();
        fromGateCombobox = new JComboBox<>();

        gridBagConstraints.gridy++;
        this.add(fromAirportLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(fromAirportCombobox, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(fromGateLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(fromGateCombobox, gridBagConstraints);

        JLabel toAirportLabel = new JLabel("To airport:");
        JLabel toGateLabel = new JLabel("To gate:");
        toAirportCombobox = new JComboBox<>();
        toGateCombobox = new JComboBox<>();

        gridBagConstraints.gridy++;
        this.add(toAirportLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(toAirportCombobox, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(toGateLabel, gridBagConstraints);
        gridBagConstraints.gridy++;
        this.add(toGateCombobox, gridBagConstraints);

        gridBagConstraints.gridy++;
        JButton addButton = new JButton("add");
        addButton.addActionListener(this::addButtonClick);
        this.add(addButton, gridBagConstraints);

        canvas.addCallBackAfterAppInit(()->{
            updateComboboxes();
            getWorld().addAirportAddedListener(this::updateComboboxes);
        });

    }

    private void addButtonClick(ActionEvent actionEvent) {
        if(fromAirportCombobox.getSelectedItem() == null ||
                toAirportCombobox.getSelectedItem() == null ||
                fromGateCombobox.getSelectedItem() == null ||
                toGateCombobox.getSelectedItem() == null)
            return;
        Airport fromAirport = ((Airport)fromAirportCombobox.getSelectedItem());
        Airport toAirport = ((Airport)toAirportCombobox.getSelectedItem());
        int fromGate = (int)fromGateCombobox.getSelectedItem();
        int toGate = (int)toGateCombobox.getSelectedItem();

        // TODO: register package
        listModel.addElement(fromAirport.getName() + "[" + fromGate + "] - " + toAirport.getName() + "[" + toGate + "]");
    }

    private void updateComboboxes(){
        fromAirportCombobox.removeAllItems();
        toAirportCombobox.removeAllItems();
        for(Airport airport : getWorld().getAirports()){
            fromAirportCombobox.addItem(airport);
            toAirportCombobox.addItem(airport);
        }

        fromGateCombobox.removeAllItems();
        toGateCombobox.removeAllItems();
        int i = 0;
        while(i<=1){
            fromGateCombobox.addItem(i);
            toGateCombobox.addItem(i);
            i++;
        }
    }
    
}
