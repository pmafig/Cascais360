package nsop.neds.cascais360.Manager;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nsop.neds.cascais360.Entities.PinPointEntity;
import nsop.neds.cascais360.Settings.Data;


public class PinpointManager extends AsyncTask<String, Void, List<PinPointEntity>> {

    public PinpointManager(){
    }

    @Override
    protected List<PinPointEntity> doInBackground(String... strings) {

        List<PinPointEntity> pinpointList = new ArrayList<>();

        if(Data.PinpointEvents == null){
            Data.PinpointEvents = new HashMap<>();
        }

        try {

            JSONObject response = CommonManager.getResponseData(strings[0]);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                JSONArray jsonArray = responseData.getJSONArray("Data");

                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject event = (JSONObject) jsonArray.get(i);

                        int id = event.getInt("ID");
                        String title = event.getString("Title");
                        String image = event.getJSONArray("Images").get(0).toString();

                        JSONObject coord = ((JSONObject) event.getJSONArray("Coordinates").get(0));

                        Double lat = coord.getDouble("Lng");
                        Double log = coord.getDouble("Lat");

                        PinPointEntity p = new PinPointEntity(i + 1, id, title, image, lat, log);

                        if (!Data.PinpointEvents.containsKey(id)) {
                            Data.PinpointEvents.put(id, p);
                        }
                    }
                } catch (JSONException je) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pinpointList;
    }

    @Override
    protected void onPostExecute(final List<PinPointEntity> eventDetail) {
        super.onPostExecute(eventDetail);
    }
}
