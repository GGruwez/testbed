
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
        
        world.getAirports().forEach((airport) -> {
            Vector nzAxis = new Vector(0,0,-1);
            Vector Airportaxis = new Vector(airport.getCenterToRunway0X(),0,airport.getCenterToRunway0Z());
            float bias = nzAxis.angleBetween(Airportaxis);
            if (airport.getCenterToRunway0X() < 0){
                bias -= Math.PI;
            }
            System.out.println((int) Math.floor(10*Math.cos(-bias) + 5*Math.sin(-bias)));

            float x =  airport.getX();
            float z =  airport.getZ();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(scale(z)+this.getWidth()/2, -scale(x)+this.getHeight()/2, (int) Math.abs(Math.floor(10*Math.cos(-bias) + 5*Math.sin(-bias))), Math.abs((int)Math.floor(10*Math.sin(-bias) + 5*Math.cos(-bias))));
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
    
    private int scale(float x) {
        return (int) (x/8000*250);
    }
    
}
