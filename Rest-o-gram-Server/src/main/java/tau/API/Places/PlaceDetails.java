package tau.API.Places;

import java.io.Serializable;


/** Implement this class from "Serializable"
* So that you can pass this class Object to another using Intents
* Otherwise you can't pass to another actitivy
* */
public class PlaceDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9104416128586562415L;

	public String status;
	
	public Place result;

	@Override
	public String toString() {
		if (result!=null) {
			return result.toString();
		}
		return super.toString();
	}
}
