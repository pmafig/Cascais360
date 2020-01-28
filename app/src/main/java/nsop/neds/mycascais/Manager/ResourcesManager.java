package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.json.JSONObject;

import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.MainActivity;
import nsop.neds.mycascais.OnBoardingActivity;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.SettingsActivity;


public class ResourcesManager extends AsyncTask<String, Void, Resources> {

    private LinearLayout mainContent;
    private Context context;
    private boolean redirect;
    private boolean fromSettings;

    public ResourcesManager(Context context, boolean redirect, boolean fromSettings){
        this.context = context;
        this.redirect = redirect;
        this.fromSettings = fromSettings;
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

                if(this.fromSettings){
                    context.startActivity(new Intent(context, SettingsActivity.class));
                }

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
        Settings.menus = r.Menu;
    }
}
