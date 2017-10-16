package mygame;

public class Vector {

	private final float x;
	private final float y;
	private final float z;

	public Vector(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
		
	}
	
	public float getZ(){
		return this.z;
	}


	public Vector add(Vector other){
		return new Vector(this.getX()+ other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
	}
	
	public Vector subtract(Vector other){
		return new Vector(this.getX()- other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());

	}
	
	public float euclideanLength(){
		return (float) Math.sqrt(this.dotProduct(this));
	}
	
	/**
	 * returnt de hoek tussen twee vectoren in radialen!
	 */
	public float angleBetween(Vector other){
		return (float) Math.acos(this.dotProduct(other)/(this.euclideanLength()*other.euclideanLength()));
	}
	

	/**
	 * 
	 * @param heading - heading van de aircraft tov wereld
	 * @param pitch - pitch van de aircraft tov wereld
	 * @param roll - roll van de aircraft tov wereld
	 * @return
	 */
	public Vector transform(double heading, double pitch, double roll ){
		return new Vector(this.x*(Math.cos(heading)*Math.cos(pitch))+this.y*(Math.cos(heading)*Math.sin(pitch)*Math.sin(roll)-Math.sin(heading)*Math.cos(roll))+this.z*(Math.cos(heading)*Math.sin(pitch)*Math.cos(roll)+Math.sin(heading)*Math.sin(roll)),
				this.x*(Math.sin(heading)*Math.cos(pitch))+this.y*(Math.sin(heading)*Math.sin(pitch)*Math.sin(roll)+Math.cos(heading)*Math.cos(roll))+this.z*(Math.sin(heading)*Math.sin(pitch)*Math.cos(roll)-Math.cos(heading)*Math.sin(roll)),
				this.x*(-Math.sin(pitch))+this.y*(Math.cos(pitch)*Math.sin(roll))+this.z*(Math.cos(pitch)*Math.cos(roll)));
				
				

	
	/**
	 * A.B
	 * @param other
	 * @return
	 */
	public float dotProduct(Vector other){
		return this.x*other.x + this.y*other.y + this.z*other.z;
	}
	
	/**
	 * AxB
	 * @param other
	 * @return
	 */
	public Vector crossProduct(Vector other){
		return new Vector(this.y*other.z - other.y*this.z,
				-this.x*other.z+other.x*this.z, this.x*other.y - other.x*this.y);
	}
	
	public Vector constantProduct(float constant){
		return new Vector(this.x*constant, this.y*constant, this.z*constant);
	}
        
        @Override
        public Vector clone(){
            return new Vector(this.getX(), this.getY(), this.getZ());
        }
}	
	
