package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import nsop.neds.mycascais.Entities.Json.ExternalAppInfo;
import nsop.neds.mycascais.Entities.Json.Weather;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Settings;

public class ExternalAppManager extends AsyncTask<String, Void, Void> {

    Context context;

    public ExternalAppManager(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            setExternalAppInfo(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setExternalAppInfo(String data){
        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject(Variables.ResponseData);

                SessionManager sm = new SessionManager(context);
                sm.setExternalAppInfo(responseData.toString());

                //ExternalAppInfo appInfo = new Gson().fromJson(responseData.toString(), ExternalAppInfo.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
