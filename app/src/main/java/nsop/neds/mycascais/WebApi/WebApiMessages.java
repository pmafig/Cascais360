package nsop.neds.mycascais.WebApi;

import org.json.JSONException;
import org.json.JSONObject;

import nsop.neds.mycascais.Encrypt.MessageEncryption;

public class WebApiMessages {
    public static String DecryptMessage(String message){

        String m = "no message returned by the service.";

        if(message != null) {

            //m = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, message.replace('"', ' ').trim());

            m = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, message);

            System.out.println(m);
        }

        return m;
    }

    public static Boolean OperationSucess(String message) throws JSONException {

        JSONObject data =  new JSONObject(message);

        Boolean reportList = data.getBoolean("OperationSucess");

        return true;
    }
}
