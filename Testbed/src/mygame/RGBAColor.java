/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author daanlenaerts
 */
public class RGBAColor {
    
    private byte R;
    private byte G;
    private byte B;
    private byte A;
    
    public RGBAColor(byte R, byte G, byte B){
        this.R = R;
        this.G = G;
        this.B = B;
        this.B = A;
    }
    
    public byte getR(){
        return this.R;
    }
    public byte getG(){
        return this.G;
    }
    public byte getB(){
        return this.B;
    }
    public byte getA(){
        return this.A;
    }
    
}
