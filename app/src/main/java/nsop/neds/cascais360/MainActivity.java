package nsop.neds.cascais360;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import nsop.neds.cascais360.Manager.DashboardManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiCalls;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Settings.colors.YearColor), PorterDuff.Mode.MULTIPLY);

        new DashboardManager(this,
                (LinearLayout) findViewById(R.id.main_content), (RelativeLayout) findViewById(R.id.loadingPanel)
        ).execute(WebApiCalls.getDashBoard());
    }
}
