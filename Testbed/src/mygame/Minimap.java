
package mygame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Minimap extends JPanel {
    
    private MainSwingCanvas canvas;
    
    public Minimap(MainSwingCanvas canvas) {
        this.canvas = canvas;
        this.setPreferredSize(new Dimension(250,250));
    }
    
    @Override
    public void paint(Graphics g) {
        World world = canvas.getWorld();
        if (world==null) return;
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        for (Airport airport:world.getAirports()) {
            int x = (int) airport.getX();
            int z = (int) airport.getZ();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(scale(z)+this.getWidth()/2, scale(x)+this.getHeight()/2, 10, 5);
        }
        for (Package p:world.getPackages()) {
            int x = (int) p.getX();
            int z = (int) p.getZ(); 
            g.setColor(Color.yellow);
            g.fillRect(scale(z)+this.getWidth()/2, scale(x) + this.getHeight()/2, 3,3);
        }
        for (Aircraft aircraft:world.getCollectionOfAircraft()) {
            float x = (int) aircraft.getCalcCoordinates().getX();
            float z = (int) aircraft.getCalcCoordinates().getZ();
            g.setColor(Color.BLACK);
            g.fillRect(scale(z)+this.getWidth()/2, scale(x)+this.getHeight()/2, 5, 5);
           
        }
    }
    
    private int scale(float x) {
        return (int) (x/8000*250);
    }
    
}
