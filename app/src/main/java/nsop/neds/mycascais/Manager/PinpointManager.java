package nsop.neds.mycascais.Manager;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nsop.neds.mycascais.Entities.Json.MapMarker;
import nsop.neds.mycascais.Settings.Data;


public class PinpointManager extends AsyncTask<String, Void, List<MapMarker>> {

    public PinpointManager(){
    }

    @Override
    protected List<MapMarker> doInBackground(String... strings) {

        List<MapMarker> mapMarkers = null;

        try {

            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                JSONArray jsonArray = responseData.getJSONArray("Data");

                Type MapMarkerTypeList = new TypeToken<ArrayList<MapMarker>>(){}.getType();
                mapMarkers = new Gson().fromJson(jsonArray.toString(), MapMarkerTypeList);

                if(Data.LocalMarkers == null){
                    Data.LocalMarkers = new ArrayList<>();
                }

                for (MapMarker m: mapMarkers){
                    if(!Data.LocalMarkers.contains(m)){
                        Data.LocalMarkers.add(m);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapMarkers;
    }

    @Override
    protected void onPostExecute(final List<MapMarker> eventDetail) {
        super.onPostExecute(eventDetail);
    }
}
