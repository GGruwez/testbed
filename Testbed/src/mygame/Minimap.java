
package mygame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Minimap extends JPanel {
    
    private MainSwingCanvas canvas;
    private int size;
    private int buffer = 2000;
    
    public Minimap(MainSwingCanvas canvas) {
        this.canvas = canvas;
        this.setPreferredSize(new Dimension(250,250));
        this.size = 8000;
    }
    
    @Override
    public void paint(Graphics g) {
        updateSize();
        World world = canvas.getWorld();
        if (world==null) return;
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        world.getAirports().forEach((airport) -> {
            float x =  airport.getX();
            float z =  airport.getZ();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(scale(z)+this.getWidth()/2, -scale(x)+this.getHeight()/2, 10, 5);
        });
        world.getCollectionOfAircraft().forEach((aircraft) -> {
            float x =  aircraft.getCalcCoordinates().getX();
            float z =  aircraft.getCalcCoordinates().getZ();
            g.setColor(Color.BLACK);
            g.fillRect(scale(z)+this.getWidth()/2, -scale(x)+this.getHeight()/2, 5, 5);
        });
        world.getPackages().forEach((p) -> {
            float x =  p.getX();
            float z =  p.getZ(); 
            g.setColor(Color.yellow);
            g.fillRect(scale(z)+this.getWidth()/2, -scale(x) + this.getHeight()/2, 3,3);
        });
    }
    
    private void updateSize() {
        ArrayList<Airport> airports = canvas.getWorld().getAirports();
        Airport farrestX = airports.get(0);
        Airport farrestZ = airports.get(0);
        for (Airport airport:airports) {
            if (farrestX.getX()<airport.getX()) farrestX = airport;
            if (farrestZ.getZ()<airport.getZ()) farrestZ = airport;
        }
        this.size = (int) Math.max(farrestX.getX(), farrestZ.getZ());
        this.size = 2*Math.abs((int) (size + Math.signum(size)*buffer));
        System.out.println(size);
    }
    
    private int scale(float x) {
        return (int) (x/size*250);
    }
    
}
