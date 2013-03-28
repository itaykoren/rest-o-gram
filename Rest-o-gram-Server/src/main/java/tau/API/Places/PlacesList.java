package tau.API.Places;

import java.io.Serializable;
import java.util.List;

/** Implement this class from "Serializable"
* So that you can pass this class Object to another using Intents
* Otherwise you can't pass to another actitivy
* */
public class PlacesList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4596768018745098418L;

	public String status;

	public List<Place> results;

}