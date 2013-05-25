package rest.o.gram.Utils;

import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Or
 * Date: 5/25/13
 */
public final class EncodingUtils {

    public static final String CHARSET_NAME = "UTF-8";

    public static byte[] encodeString(final String string) {
        if (string != null && !string.isEmpty())
        {
            try
            {
                return string.getBytes(Charset.forName(CHARSET_NAME));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String decodeString(final byte[] encodedString) {
        if (encodedString != null && encodedString.length != 0)
        {
            try
            {
                return new String(encodedString, Charset.forName(CHARSET_NAME));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
