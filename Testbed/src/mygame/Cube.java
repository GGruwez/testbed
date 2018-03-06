/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import java.util.LinkedList;

/**
 *
 * @author Stien
 */

public class Cube extends Geometry {
    
    private float x;
    private float y;
    private float z;
    private ColorRGBA color;
    private AssetManager assetManager;
    private MainSwingCanvas canvasApplication;
    private LinkedList<Spatial> spatials = new LinkedList<Spatial>();
    
    public Cube(float x, float y, float z, ColorRGBA color, AssetManager assetManager, MainSwingCanvas canvasApplication) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.assetManager = assetManager;
        this.canvasApplication = canvasApplication;
        
        Mesh pX = new Mesh();
        Mesh pY = new Mesh();
        Mesh pZ = new Mesh();
        Mesh nX = new Mesh();
        Mesh nY = new Mesh();
        Mesh nZ = new Mesh();
        
        Vector3f [] vertices_pX = new Vector3f[4];
        vertices_pX[0] = new Vector3f(x+2.5f, y-2.5f, z+2.5f);
        vertices_pX[1] = new Vector3f(x+2.5f, y-2.5f, z-2.5f);
        vertices_pX[2] = new Vector3f(x+2.5f, y+2.5f, z+2.5f);
        vertices_pX[3] = new Vector3f(x+2.5f, y+2.5f, z-2.5f);
        
        Vector3f [] vertices_pY = new Vector3f[4];
        vertices_pY[0] = new Vector3f(x+2.5f, y+2.5f, z-2.5f);
        vertices_pY[1] = new Vector3f(x-2.5f, y+2.5f, z-2.5f);
        vertices_pY[2] = new Vector3f(x+2.5f, y+2.5f, z+2.5f);
        vertices_pY[3] = new Vector3f(x-2.5f, y+2.5f, z+2.5f);
        
        Vector3f [] vertices_pZ = new Vector3f[4];
        vertices_pZ[0] = new Vector3f(x-2.5f, y-2.5f, z+2.5f);
        vertices_pZ[1] = new Vector3f(x+2.5f, y-2.5f, z+2.5f);
        vertices_pZ[2] = new Vector3f(x-2.5f, y+2.5f, z+2.5f);
        vertices_pZ[3] = new Vector3f(x+2.5f, y+2.5f, z+2.5f);
        
        Vector3f [] vertices_nX = new Vector3f[4];
        vertices_nX[0] = new Vector3f(x-2.5f, y-2.5f, z-2.5f);
        vertices_nX[1] = new Vector3f(x-2.5f, y-2.5f, z+2.5f);
        vertices_nX[2] = new Vector3f(x-2.5f, y+2.5f, z-2.5f);
        vertices_nX[3] = new Vector3f(x-2.5f, y+2.5f, z+2.5f);
        
        Vector3f [] vertices_nY = new Vector3f[4];
        vertices_nY[0] = new Vector3f(x+2.5f, y-2.5f, z+2.5f);
        vertices_nY[1] = new Vector3f(x-2.5f, y-2.5f, z+2.5f);
        vertices_nY[2] = new Vector3f(x+2.5f, y-2.5f, z-2.5f);
        vertices_nY[3] = new Vector3f(x-2.5f, y-2.5f, z-2.5f);
        
        Vector3f [] vertices_nZ = new Vector3f[4];
        vertices_nZ[0] = new Vector3f(x+2.5f, y-2.5f, z-2.5f);
        vertices_nZ[1] = new Vector3f(x-2.5f, y-2.5f, z-2.5f);
        vertices_nZ[2] = new Vector3f(x+2.5f, y+2.5f, z-2.5f);
        vertices_nZ[3] = new Vector3f(x-2.5f, y+2.5f, z-2.5f);
        
        Vector2f [] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(5,0);
        texCoord[2] = new Vector2f(0,5);
        texCoord[3] = new Vector2f(5,5);
        
        short[] indexes = {2, 0, 1, 1, 3, 2};
        
        pX.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_pX));
        pX.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        pX.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        pX.updateBound();
        
        pY.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_pY));
        pY.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        pY.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        pY.updateBound();
        
        pZ.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_pZ));
        pZ.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        pZ.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        pZ.updateBound();
        
        nX.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_nX));
        nX.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        nX.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        nX.updateBound();
        
        nY.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_nY));
        nY.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        nY.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        nY.updateBound();
        
        nZ.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices_nZ));
        nZ.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        nZ.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        nZ.updateBound();
        
        Geometry geom_pX = new Geometry("pX", pX);
        Material mat_pX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_pX.setColor("Color", color.mult(0.85f));
        geom_pX.setMaterial(mat_pX);
        
        Geometry geom_pY = new Geometry("pY", pY);
        Material mat_pY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_pY.setColor("Color", color.mult(1f));
        geom_pY.setMaterial(mat_pY);
        
        Geometry geom_pZ = new Geometry("pZ", pZ);
        Material mat_pZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_pZ.setColor("Color", color.mult(0.70f));
        geom_pZ.setMaterial(mat_pZ);
        
        Geometry geom_nX = new Geometry("nX", nX);
        Material mat_nX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_nX.setColor("Color", color.mult(0.3f));
        geom_nX.setMaterial(mat_nX);
        
        Geometry geom_nY = new Geometry("nY", nY);
        Material mat_nY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_nY.setColor("Color", color.mult(0.15f));
        geom_nY.setMaterial(mat_nY);
        
        Geometry geom_nZ = new Geometry("nZ", nZ);
        Material mat_nZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_nZ.setColor("Color", color.mult(0.45f));
        geom_nZ.setMaterial(mat_nZ);

        spatials.add(geom_pX);
        spatials.add(geom_pY);
        spatials.add(geom_pZ);
        spatials.add(geom_nX);
        spatials.add(geom_nY);
        spatials.add(geom_nZ);

        spatials.forEach(s -> canvasApplication.addToNewSpatialQueue(s));

    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }

    public ColorRGBA getColor() {
        return this.color;
    }
    
    public void destroy() {
        spatials.forEach(s -> canvasApplication.addToDestructibleSpatialQueue(s));
        spatials.clear();
    }
}
