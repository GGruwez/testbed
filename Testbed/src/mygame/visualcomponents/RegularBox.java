package mygame.visualcomponents;

import com.jme3.scene.shape.Box;

/**
 * Creates box with its dimensions half those of a normal Box.
 *
 * A default Box has dimensions (2x, 2y, 2z), since:
 * x is the size of the box along the x axis, in both directions
 * y is the size of the box along the y axis, in both directions
 * z is the size of the box along the z axis, in both directions
 *
 */
public class RegularBox extends Box {

    public RegularBox(float x, float y, float z){
        super(x/2, y/2, z/2);
    }

}
