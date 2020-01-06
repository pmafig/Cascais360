package nsop.neds.cascais360;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import nsop.neds.cascais360.Manager.ResourcesManager;
import nsop.neds.cascais360.WebApi.WebApiCalls;

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
        //new DashboardManager().execute(WebApiCalls.getDashBoard());
    }

}
