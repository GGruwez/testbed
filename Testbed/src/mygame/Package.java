
package mygame;

import interfaces.Job;

public class Package {
    
    private Job job;
    
    private float x;
    private float z;
    
    public Package(Job job) {
        this.job = job;
        this.x = job.getAirportFrom().getCenterX();
        this.z = job.getAirportFrom().getCenterZ();
    }
    
    public float getX() {
        return x;
    }
    
    public float getZ() {
        return z;
    }
    
}
