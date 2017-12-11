package mygame;

import com.jme3.math.ColorRGBA;

import javax.swing.*;
import java.awt.*;

/**
 * Created by daanlenaerts on 23/11/2017.
 */
public class CubeUI extends JPanel {

    private World world;

    private JTextField xField = new JTextField();
    private JTextField yField = new JTextField();
    private JTextField zField = new JTextField();
    private JButton addCube = new JButton("Add cube");

    public CubeUI(World world) {
        super(new FlowLayout());

        this.world = world;

        xField.setPreferredSize(new Dimension(60, 20));
        this.add(this.xField);
        yField.setPreferredSize(new Dimension(60, 20));
        this.add(this.yField);
        zField.setPreferredSize(new Dimension(60, 20));
        this.add(this.zField);
        addCube.addActionListener( e -> {
            if(getWorld() != null) {
                getWorld().generateCube(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()), Float.parseFloat(zField.getText()), ColorRGBA.Green);
                xField.setText("");
                yField.setText("");
                zField.setText("");
            }
        });
        this.add(addCube);

    }

    public World getWorld() {
        return world;
    }

}
