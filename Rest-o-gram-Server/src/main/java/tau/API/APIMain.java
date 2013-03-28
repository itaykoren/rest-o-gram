package tau.API;

import tau.API.Instagram.Instagram;
import tau.API.Places.GooglePlaces;
import tau.API.Places.Place;
import tau.API.Places.PlacesList;

public class APIMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Google places test
		System.out.println("***** Testing Google Places API *****");
		
		GooglePlaces gp = new GooglePlaces();
		PlacesList list = null;
		try {
			list = gp.search(32.112, 34.839, 10000, "bar|cafe|food|restaurant");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(list != null)
		{
			System.out.println("Status = " + list.status + ", Results = " + list.results);
			
			if(list.results != null)
			{
				for(Place place : list.results)
					System.out.println("Place = " + place);
			}
		}
		
		
		// Instagram test
		System.out.println("***** Testing Instagram API *****");
		
		Instagram instagram = new Instagram();
		instagram.searchLocation(32.0550530, 34.7566420);  // Yoezer wine bar
		
		String locationId = "56063086"; // First result of searchLocation response
		instagram.getRecentMedia(locationId);
		
		
		System.out.println();
		System.out.println("***** Done *****");
	}

}
