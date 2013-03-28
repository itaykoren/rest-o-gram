package tau.API.Instagram;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;


public class Instagram {
	
	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	// Instagram API Key
	private static final String API_KEY = "4d32ff70646e46a992a4ad5a0945ef3f";
	
	// Location search url
	private static final String LOCATION_SEARCH_URL = "https://api.instagram.com/v1/locations/search?";

	// Locations url
	private static final String LOCATIONS_URL = "https://api.instagram.com/v1/locations/";
	// Media recent url
	private static final String MEDIA_RECENT_URL = "/media/recent?";
	
	
	public void searchLocation(double lat, double lon) {
		
		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(LOCATION_SEARCH_URL));
			
			request.getUrl().put("lat", lat);
			request.getUrl().put("lng", lon);
			request.getUrl().put("client_id", API_KEY);
			
			System.out.println("Request:");
			System.out.println(request.getUrl());

			HttpResponse response = request.execute();
			
			System.out.println("Response:");
			System.out.println(response.parseAsString());

		} catch (Exception e) {
			System.out.println("Error (searchLocation) : " + e.getMessage());
		}
	}
	
	public void getRecentMedia(String locationId) {
		
		try {
			
			String url = LOCATIONS_URL + locationId + MEDIA_RECENT_URL;

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(url));
			
			request.getUrl().put("client_id", API_KEY);
			
			System.out.println("Request:");
			System.out.println(request.getUrl());

			HttpResponse response = request.execute();
			
			System.out.println("Response:");
			System.out.println(response.parseAsString());

		} catch (Exception e) {
			System.out.println("Error (getRecentMedia) : " + e.getMessage());
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
