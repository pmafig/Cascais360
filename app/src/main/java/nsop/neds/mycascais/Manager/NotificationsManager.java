package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import nsop.neds.mycascais.Entities.Json.Notifications;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.Weather;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Settings;

public class NotificationsManager extends AsyncTask<String, Void, Search> {

    Context context;
    LinearLayout content;

    public NotificationsManager(Context context, LinearLayout content){
        this.context = context;
        this.content = content;
    }

    @Override
    protected Search doInBackground(String... strings) {
        try {
            return setNotifications(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Search content) {
        super.onPostExecute(content);

        if(content != null) {

        }
    }


    private Search setNotifications(String data){

        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                String temp = responseData.getString("NodeList");

                Notifications search = new Gson().fromJson(temp, Notifications.class);

                return search.Data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
