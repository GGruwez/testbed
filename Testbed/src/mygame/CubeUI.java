package mygame;

import com.jme3.math.ColorRGBA;

import javax.swing.*;
import java.awt.*;

/**
 * Created by daanlenaerts on 23/11/2017.
 */
class CubeUI extends JPanel {

    private MainSwingCanvas canvasApplication;

    private JTextField xField = new JTextField();
    private JTextField yField = new JTextField();
    private JTextField zField = new JTextField();
    private JButton addCube = new JButton("Add cube");

    public CubeUI(MainSwingCanvas canvasApplication, Callback onClick) {
        super(new GridBagLayout());

        this.canvasApplication = canvasApplication;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        this.add(this.xField, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        this.add(this.yField, gridBagConstraints);
        gridBagConstraints.gridx = 2;
        this.add(this.zField, gridBagConstraints);
        addCube.addActionListener( e -> {
            if(getWorld() != null) {
                getWorld().generateCube(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()), Float.parseFloat(zField.getText()), ColorRGBA.Red);
                xField.setText("");
                yField.setText("");
                zField.setText("");
                onClick.run();
            }
        });
        gridBagConstraints.gridx = 3;
        this.add(addCube, gridBagConstraints);
    }

    public World getWorld() {
        return canvasApplication.getWorld();
    }

}
