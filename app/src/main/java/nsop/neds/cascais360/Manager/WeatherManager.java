package nsop.neds.cascais360.Manager;

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
import nsop.neds.cascais360.Entities.Json.Weather;

import nsop.neds.cascais360.R;
import nsop.neds.cascais360.Settings.Settings;

public class WeatherManager extends AsyncTask<String, Void, Weather> {

    Context context;
    LinearLayout weartherContent;

    public WeatherManager(Context context, LinearLayout weartherContent){
        this.context = context;
        this.weartherContent = weartherContent;
    }

    @Override
    protected Weather doInBackground(String... strings) {
        try {
            return setWeather(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Weather weather) {
        super.onPostExecute(weather);

        if(weather != null) {

            TextView currentTemp = weartherContent.findViewById(R.id.currentTemperature);
            currentTemp.setText(String.valueOf(weather.Current));

            TextView infoMinMax = weartherContent.findViewById(R.id.infoMinMax);
            infoMinMax.setText(String.format("%dº | %dº", weather.Min, weather.Max));

            ImageView iconTemp = weartherContent.findViewById(R.id.iconTemperature);

            TextView label_weatherCascais = weartherContent.findViewById(R.id.label_WeatherCascais);
            label_weatherCascais.setText(Settings.labels.WeatherCascais);
            TextView label_today = weartherContent.findViewById(R.id.label_Today);
            label_today.setText(Settings.labels.Today);

            try {
                Drawable icon = context.getDrawable(context.getResources().getIdentifier(weather.getIcon(), "drawable", context.getPackageName()));
                iconTemp.setImageDrawable(icon);
            } catch (URISyntaxException e) {
                iconTemp.setVisibility(View.GONE);
            }

        }else{
            this.weartherContent.setVisibility(View.GONE);
        }
    }


    private Weather setWeather(String data){

        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                JSONObject temp = responseData.getJSONObject("Data");

                Weather weather = new Gson().fromJson(temp.toString(), Weather.class);

                return weather;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
