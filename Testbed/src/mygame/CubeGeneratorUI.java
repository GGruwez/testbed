
package mygame;

import java.awt.*;
import java.util.ConcurrentModificationException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


class CubeGeneratorUI extends JPanel{
    private MainSwingCanvas canvasApplication;
    private JTextField text = new JTextField();
    private JButton b = new JButton("generate amount of cubes");
    
    CubeGeneratorUI(MainSwingCanvas canvasApplication, Callback onClick) {
        super(new GridBagLayout());

        this.canvasApplication = canvasApplication;

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        text.setPreferredSize(new Dimension(60,20));
        this.add(text);
        b.addActionListener(e -> {
            getWorld().generateRandomCubes(Integer.valueOf(text.getText()));
            text.setText("");
            onClick.run();
        });
        gridBagConstraints.gridx = 1;
        this.add(b, gridBagConstraints);
    }

    private World getWorld() {
        return canvasApplication.getWorld();
    }
}
