package nsop.neds.mycascais;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import nsop.neds.mycascais.Manager.ResourcesManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String packageName = "";
        int externalAppId = 0;
        if(intent.hasExtra("packageName")) {
            packageName = bundle.getString("packageName");
        }
        if(intent.hasExtra("externalAppId")) {
            externalAppId = bundle.getInt("externalAppId");
        }

        if(!packageName.isEmpty() && externalAppId > 0){
            Intent disclaimer = new Intent(this, DisclaimerActivity.class);
            disclaimer.putExtra("packageName", packageName);
            disclaimer.putExtra("externalAppId", externalAppId);
            startActivity(disclaimer);
        }else {
            if (intent.hasExtra(Variables.Id)) {
                new ResourcesManager(this, false, false).execute(WebApiCalls.getResources());
                Intent i = new Intent(this, DetailActivity.class);
                i.putExtra(Variables.Id, getIntent().getStringExtra(Variables.Id));
                startActivity(i);
            } else {
                new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
            }
        }
    }
}
