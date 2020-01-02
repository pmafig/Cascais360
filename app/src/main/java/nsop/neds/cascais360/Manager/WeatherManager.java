package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import nsop.neds.cascais360.Entities.WeatherEntity;
import nsop.neds.cascais360.R;

public class WeatherManager extends AsyncTask<String, Void, WeatherEntity> {

    Context context;
    //NavigationView navigation;


    WeatherEntity weather;

    public WeatherManager(Context context){
        this.context = context;
        //this.navigation = navigationView;
    }

    @Override
    protected WeatherEntity doInBackground(String... strings) {
        try {
            this.weather = setWeather(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.weather;
    }

    @Override
    protected void onPostExecute(WeatherEntity weather) {
        super.onPostExecute(weather);

        /*try {

            //region Weather Constructor
            try {
                TextView currentTemp = navigation.findViewById(R.id.currentTemperature);
                currentTemp.setText(weather.getCurrent());

                TextView infoMinMax = navigation.findViewById(R.id.infoMinMax);
                infoMinMax.setText(String.format("%dº | %dº", weather.getMin(), weather.getMax()));

                ImageView iconTemp = navigation.findViewById(R.id.iconTemperature);

                Drawable icon = context.getDrawable(context.getResources().getIdentifier(weather.getIcon(), "drawable", context.getPackageName()));

                icon.setTint(context.getResources().getColor(R.color.colorBlack));

                iconTemp.setImageDrawable(icon);

            }catch (Exception e){
                LinearLayout infoTemp = navigation.findViewById(R.id.weather);
                infoTemp.setVisibility(View.GONE);
            }

        } catch (Exception e) {
             Log.e("Error", e.getMessage());
            context.startActivity(new Intent(context, NoServiceActivity.class));
        }*/
    }


    private WeatherEntity setWeather(String data){

        WeatherEntity weather = null;

        try {
            JSONObject response = CommonManager.getResponseData(data);

            if(response != null) {
                JSONObject responseData = response.getJSONObject("ResponseData");

                JSONObject temp = responseData.getJSONObject("Data");

                weather = new WeatherEntity(temp.getString("Current"), temp.getInt("Min"), temp.getInt("Max"), temp.getString("Text"), temp.getString("Icon"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){

        }

        return weather;
    }
}
