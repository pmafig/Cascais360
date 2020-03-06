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

import nsop.neds.mycascais.Entities.Json.Weather;
import nsop.neds.mycascais.R;
import nsop.neds.mycascais.Settings.Settings;

public class ContactAsAuth extends AsyncTask<String, Void, Void> {

    Context context;
    LinearLayout weartherContent;

    public ContactAsAuth(){

    }

    @Override
    protected Void doInBackground(String... strings) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
