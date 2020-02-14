package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nsop.neds.mycascais.Entities.Json.Country;
import nsop.neds.mycascais.Entities.Json.Node;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.MainActivity;
import nsop.neds.mycascais.OnBoardingActivity;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.SettingsActivity;


public class CountryListManager extends AsyncTask<String, Void, Void> {

    private Context context;

    public CountryListManager(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            JSONObject response = CommonManager.getResponseData(strings[0]);

            SessionManager sm = new SessionManager(context);

            if(response != null) {

                JSONObject responseData = response.getJSONObject(Variables.ResponseData);

                JSONObject jsonObject = responseData.getJSONObject(Variables.Data);

                Type CountryTypeList = new TypeToken<ArrayList<Country>>(){}.getType();
                List<Country> coutry_list = new Gson().fromJson(jsonObject.toString(), CountryTypeList);

                sm.setSetCountryList(jsonObject.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
