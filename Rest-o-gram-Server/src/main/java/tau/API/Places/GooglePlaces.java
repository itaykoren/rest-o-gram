package tau.API.Places;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonHttpParser;
//import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
//import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class GooglePlaces {

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	// Google API Key
	private static final String API_KEY = "AIzaSyC0j9m_DhbtxeAuwAseqndxovOs6Q5CM2w"; // place your API key here


	// Google Places search url's
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	//private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

	private double _latitude;
	private double _longitude;
	private double _radius;
	
	/**
	 * Searching places
	 * @param latitude - latitude of place
	 * @params longitude - longitude of place
	 * @param radius - radius of searchable area
	 * @param types - type of place to search
	 * @return list of places
	 * */
	public PlacesList search(double latitude, double longitude, double radius, String types)
			throws Exception {

		this._latitude = latitude;
		this._longitude = longitude;
		this._radius = radius;

		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			
			request.getUrl().put("location", _latitude + "," + _longitude);
			request.getUrl().put("radius", _radius); // in meters
			request.getUrl().put("sensor", "false");
			if(types != null)
				request.getUrl().put("types", types);
			//request.getUrl().put("rankby", "distance");
			request.getUrl().put("key", API_KEY);
			
			System.out.println("Request:");
			System.out.println(request.getUrl());

			HttpResponse response = request.execute();
			
			System.out.println("Response:");
			System.out.println(response.parseAsString());
			
			//PlacesList list = response.parseAs(PlacesList.class);
			// Check log cat for places response status
			//Log.d("Places Status", "" + list.status);
			//return list;
			
			return null;

		} catch (Exception e) {
			System.out.println("Error (search) : " + e.getMessage());
			return null;
		}

	}

	/**
	 * Searching single place full details
	 * @param refrence - reference id of place
	 * 				   - which you will get in search api request
	 * */
	public PlaceDetails getPlaceDetails(String reference) throws Exception {
		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("reference", reference);
			request.getUrl().put("sensor", "false");

			PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
			
			return place;

		} catch (Exception e) {
			System.out.println("Error (getPlaceDetails) : " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Creating http request Factory
	 * */
	private static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				request.setHeaders(headers);
				JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
				request.setParser(parser);
			}
		});
	}

}
