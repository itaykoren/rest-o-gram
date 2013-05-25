package rest.o.gram.credentials;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class RandomCredentialsFactory implements ICredentialsFactory {
    @Override
    public Credentials createFoursquareCredentials() {
        int index = getRandomIndex();
        return new Credentials(index, foursquareCredentials[index][0], foursquareCredentials[index][1]);
    }

    @Override
    public Credentials createInstagramCredentials() {
        int index = getRandomIndex();
        return new Credentials(index, instagramCredentials[index][0], instagramCredentials[index][1]);
    }

    private int getRandomIndex() {
        return random.nextInt(CREDENTIALS_AMOUNT);
    }

    /**
     * Order: Itay, Roi, Hen, Or
     */
    private String[][] foursquareCredentials =
            {{"OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T","3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF"},
             {"OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T","3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF"},
             {"OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T","3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF"},
             {"OERIKGO1WPRTY2RWWF3IMX5FUGBLSCES1OJ1F3BBLFOIBF3T","3MQLEAAV5YH2O0ZIWDVJ515KYRDROA3DPQJG4ZDPZHXXCMTF"}};

    /**
     * Order: Itay, Roi, Hen, Or
     */
    private String[][] instagramCredentials =
            {{"4d32ff70646e46a992a4ad5a0945ef3f","f409c9702dbc4c09a3100198cfd76e03"},
             {"d25c3bd1ff3e40c0bf9a9b7d35edf9e9","89ade00d75184099b56ec3c997eef3f4"},
             {"dd646f1d74714ddb96f15171e7c8a194","f2faf4ab9c6e4f019e7f8718d6363d6c"},
             {"dd646f1d74714ddb96f15171e7c8a194","f2faf4ab9c6e4f019e7f8718d6363d6c"}};

    private final int CREDENTIALS_AMOUNT = 4;
    private Random random = new Random();
}
