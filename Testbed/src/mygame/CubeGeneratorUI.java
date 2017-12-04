
package mygame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CubeGeneratorUI extends JPanel{
    private World world;
    private JTextField text = new JTextField();
    private JButton b = new JButton("generate amount of cubes");
    
    public CubeGeneratorUI(World world) {
        super(new FlowLayout());
        this.world = world;
        text.setPreferredSize(new Dimension(60,20));
        this.add(text);
        b.addActionListener(e -> {
            world.generateRandomCubes(Integer.valueOf(text.getText()));
            text.setText("");
        });
        this.add(b);
    }
}
