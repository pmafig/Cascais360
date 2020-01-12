package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import nsop.neds.cascais360.Entities.Json.Resources;
import nsop.neds.cascais360.Entities.ResourceEntity;
import nsop.neds.cascais360.MainActivity;
import nsop.neds.cascais360.OnBoardingActivity;
import nsop.neds.cascais360.Settings.Settings;


public class ResourcesManager extends AsyncTask<String, Void, Resources> {

    private LinearLayout mainContent;
    private Context context;
    private boolean redirect;

    public ResourcesManager(Context context, boolean redirect){
        this.context = context;
        this.redirect = redirect;
    }

    @Override
    protected Resources doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            SessionManager sm = new SessionManager(context);

            if(response != null) {

                JSONObject responseData = response.getJSONObject("ResponseData");

                String crc = responseData.getString("CRC");

                Resources inMemory = sm.getResources();

                if(inMemory != null && crc.startsWith(inMemory.CRC))
                    return inMemory;

                JSONObject jsonObject = responseData.getJSONObject("Data");

                Resources resources = new Gson().fromJson(jsonObject.toString(), Resources.class);
                resources.CRC = crc;

                sm.setResources(resources);

                return resources;
            }else{
                Resources inMemory = sm.getResources();

                if(inMemory != null) {
                    return inMemory;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(final Resources resources) {
        super.onPostExecute(resources);

        try {
            if(resources != null) {

                setSettings(resources);

                if(redirect) {
                    SessionManager sm = new SessionManager(context);

                    if (sm != null) {
                        if (sm.getOnboarding()) {
                            context.startActivity(new Intent(context, MainActivity.class));
                        } else {
                            sm.setOnboarding();
                            context.startActivity(new Intent(context, OnBoardingActivity.class));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setSettings(Resources r){
        Settings.colors = r.Colors;
        Settings.labels = r.Labels;
        Settings.labels = r.Labels;
        Settings.aboutApp = r.AboutApp;
    }
}
