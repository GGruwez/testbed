
package mygame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


class CubeGeneratorUI extends JPanel{
    private MainSwingCanvas canvasApplication;
    private JTextField text = new JTextField();
    private JButton b = new JButton("generate amount of cubes");
    
    CubeGeneratorUI(MainSwingCanvas canvasApplication) {
        super(new FlowLayout());

        this.canvasApplication = canvasApplication;

        text.setPreferredSize(new Dimension(60,20));
        this.add(text);
        b.addActionListener(e -> {
            getWorld().generateRandomCubes(Integer.valueOf(text.getText()));
            text.setText("");
        });
        this.add(b);
    }

    private World getWorld() {
        return canvasApplication.getWorld();
    }
}
