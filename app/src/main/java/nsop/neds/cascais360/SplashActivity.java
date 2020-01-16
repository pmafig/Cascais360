package nsop.neds.cascais360;
import android.content.Intent;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.se.omapi.Session;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import nsop.neds.cascais360.Manager.ResourcesManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sm = new SessionManager(this);
        Settings.LangCode =  sm.getLangCode();

        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        if(getIntent().hasExtra(Variables.Id)){
            new ResourcesManager(this, false, false).execute(WebApiCalls.getResources());
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Variables.Id, getIntent().getStringExtra(Variables.Id));
            startActivity(intent);
        }else {
            new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
        }
    }
}
