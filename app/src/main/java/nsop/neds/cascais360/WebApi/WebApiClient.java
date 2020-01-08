package nsop.neds.cascais360.WebApi;

import android.os.Build;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import nsop.neds.cascais360.Encrypt.MessageEncryption;

public class WebApiClient {
    public static final String SITE_KEY = "ae1b1452-947b-4f09-b77a-24c87f0efa6d";
    public static final String SITE_ID = "17";

    public static String BASE_URL = "https://webapi.cascais.pt/api";

    public enum API{
        cms,
        account,
        WebApiAccount
    }

    public enum METHODS{
        content,
        login,
        setsubscription,
        RefreshLoginUser,
        CreateMobileTemporaryAuthentication,
        UpdateMobilePhoneContact,
        ValidateMobilePhoneContact,
        ValidateMobileTemporaryLoginUser,
        ValidateSmsToken
    }

    private static final String USERAGENT = Build.VERSION.RELEASE;
    private static final int TIMEOUT = 3500000;

    private static AsyncHttpClient client;

    private static void setHttpClient(){
        if(client == null){
            client = new AsyncHttpClient();
            client.setTimeout(TIMEOUT);
            client.setUserAgent(USERAGENT);
        }
    }

    public static void get(String url, String message, AsyncHttpResponseHandler responseHandler) {
        setHttpClient();
        client.get(getAbsoluteUrl(url, new MessageEncryption().Encrypt(message, SITE_KEY)), responseHandler);
    }

    public static void post(String url, String message, Boolean encrypt, AsyncHttpResponseHandler responseHandler) {
        setHttpClient();
        client.post(getAbsoluteUrl(url, encrypt ?  new MessageEncryption().Encrypt(message, SITE_KEY) : message), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl, String rT) {
        String absoluteUrl = BASE_URL + relativeUrl + "/" + SITE_ID + "?rt=" + rT;
        System.out.println(absoluteUrl);
        return absoluteUrl;
    }
}
