/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;
public class CanvasManager {
    
    private Canvas activeCanvas;
    private HashMap<Integer,Canvas> map;
    
    public CanvasManager(Canvas start) {
        this.setActiveCanvas(start);
        map = new HashMap<Integer,Canvas>();
        map.put(0, start);
    }
    
    public void setActiveCanvas(Canvas c) {
        this.activeCanvas=c;
    }
    
    public Canvas getActiveCanvas() {
        return this.activeCanvas;
    }
    
    public Canvas getCanvas(int i) {
        return map.get(i);
    }
    
    public void put(int i, Canvas c) {
        map.put(i, c);
    }
}
