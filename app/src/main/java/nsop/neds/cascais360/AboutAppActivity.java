package nsop.neds.cascais360;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.cascais360.Entities.Json.Resources;
import nsop.neds.cascais360.Manager.CommonManager;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        WebView about = findViewById(R.id.about_app);



        about.loadData(CommonManager.WebViewFormat(Settings.aboutApp), "text/html; charset=utf-8", "UTF-8");

        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout menuFragment = findViewById(R.id.menu);
        new MenuManager(this, toolbar, menuFragment, Settings.labels.AboutApp);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());
    }

}
