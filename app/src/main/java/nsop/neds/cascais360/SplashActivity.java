package nsop.neds.cascais360;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Entities.ResourceEntity;
import nsop.neds.cascais360.Manager.CommonManager;
import nsop.neds.cascais360.Manager.DashboardManager;
import nsop.neds.cascais360.Manager.ResourcesManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;
import nsop.neds.cascais360.WebApi.WebApiClient;
import nsop.neds.cascais360.WebApi.WebApiMessages;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        new ResourcesManager(this).execute(WebApiCalls.getResources());

        new DashboardManager().execute(WebApiCalls.getDashBoard());
    }

}
