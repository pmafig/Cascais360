package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nsop.neds.cascais360.Entities.Json.Dashboard;
import nsop.neds.cascais360.Entities.Json.Detail;
import nsop.neds.cascais360.Entities.Json.Event;
import nsop.neds.cascais360.Entities.Json.HighLight;
import nsop.neds.cascais360.Entities.Json.LayoutBlock;
import nsop.neds.cascais360.Entities.Json.Node;
import nsop.neds.cascais360.Entities.Json.Place;
import nsop.neds.cascais360.Manager.Layout.LayoutManager;
import nsop.neds.cascais360.R;

public class DetailManager extends AsyncTask<String, Void, Detail> {

    LinearLayout mainContent;
    Context context;
    int nid;

    public DetailManager(int nid, Context context, LinearLayout mainContent){
        this.nid = nid;
        this.context = context;
        this.mainContent = mainContent;
    }

    @Override
    protected Detail doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            SessionManager sm = new SessionManager(context);

            if(response != null) {

                JSONObject responseData = response.getJSONObject("ResponseData");

                String crc = responseData.getString("CRC");

                final JSONObject jsonObject = responseData.getJSONObject("Data");

                String _s = jsonObject.toString();

                Detail detail = new Gson().fromJson(_s, Detail.class);

                sm = null;

                return detail;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Detail detail) {
        super.onPostExecute(detail);

        try {

            if(detail.Events != null && detail.Events.size() > 0){
                LayoutManager.setEvent(mainContent, detail.Events.get(0));
            }if(detail.Places != null && detail.Places.size() > 0){
                LayoutManager.setPlace(mainContent, detail.Places.get(0));
            }if(detail.Routes != null && detail.Routes.size() > 0){
                LayoutManager.setRoute(mainContent, detail.Routes.get(0));
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //context.startActivity(new Intent(context, NoServiceActivity.class));
        }
    }

}
