package rest.o.gram.credentials;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class SimpleCredentialsFactory implements ICredentialsFactory {
    @Override
    public Credentials createFoursquareCredentials() {
        return new Credentials(0, FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET);
    }

    @Override
    public Credentials createInstagramCredentials() {
        return new Credentials(0, INSTAGRAM_CLIENT_ID, INSTAGRAM_CLIENT_SECRET);
    }

    // Foursquare Client Id
    private static final String FOURSQUARE_CLIENT_ID = "OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T";

    // Foursquare Client Secret
    private static final String FOURSQUARE_CLIENT_SECRET = "3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF";

    // Instagram Client Id
    private static final String INSTAGRAM_CLIENT_ID = "4d32ff70646e46a992a4ad5a0945ef3f";

    // Instagram Client Secret
    private static final String INSTAGRAM_CLIENT_SECRET = "f409c9702dbc4c09a3100198cfd76e03";
}
