package mygame;

import com.jme3.math.ColorRGBA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UI extends JFrame {

    private World world;

    private JTextField xField = new JTextField();
    private JTextField yField = new JTextField();
    private JTextField zField = new JTextField();
    private JButton addCube = new JButton("Add cube");

    UI(World world) {
        super("Cube UI");

        this.world = world;

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(200,200));
        this.setLocation(10,240);
        this.setLayout(null);
        xField.setSize(60, 20);
        xField.setLocation(10, 10);
        this.add(this.xField);
        yField.setSize(60, 20);
        yField.setLocation(70, 10);
        this.add(this.yField);
        zField.setSize(60, 20);
        zField.setLocation(130, 10);
        this.add(this.zField);
        addCube.setSize(170, 20);
        addCube.setLocation(15, 40);
        addCube.addActionListener( e -> {
            getWorld().generateCube(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()), Float.parseFloat(zField.getText()), ColorRGBA.Green);
            xField.setText("");
            yField.setText("");
            zField.setText("");
          });
        this.add(addCube);


    }

    public World getWorld() {
        return world;
    }
}
