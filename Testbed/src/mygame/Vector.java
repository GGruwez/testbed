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
	
	
	// transformatiematrixen, daar kan ik echt nie mee werken
	// ik kijk daar wel eens naar als ik mn boek van mechanica 3 vind, staat daar allemaal in
	public Vector transform(float heading, float pitch, float roll ){
		
		return new Vector(0,0,0);
	}
	
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
	
