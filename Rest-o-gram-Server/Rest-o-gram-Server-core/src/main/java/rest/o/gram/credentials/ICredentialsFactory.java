package rest.o.gram.credentials;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public interface ICredentialsFactory {
    /**
     * Returns foursquare credentials
     */
    Credentials createFoursquareCredentials();

    /**
     * Returns instagram credentials
     */
    Credentials createInstagramCredentials();
}
