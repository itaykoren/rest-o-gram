package rest.o.gram.credentials;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class Credentials {
    /**
     * Ctor
     */
    public Credentials(int type, String clientId, String clientSecret) {
        this.type = type;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Type
     */
    public int getType() {
        return type;
    }

    /**
     * Client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    private final int type;
    private final String clientId;
    private final String clientSecret;
}
