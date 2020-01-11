package nsop.neds.cascais360;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Manager.CommonManager;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout menuFragment = findViewById(R.id.menu);

        new MenuManager(this, toolbar, menuFragment, Settings.labels.Settings);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());
    }

}
