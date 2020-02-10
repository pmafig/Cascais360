package nsop.neds.mycascais;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.DashboardManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class DisclaimerActivity extends AppCompatActivity {

    private String packageName = "";
    private int externalAppId;

    private Disclaimer disclaimer;

    private Button disclaimerConfirm;
    private Button disclaimerCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout disclaimerContent = findViewById(R.id.disclaimer_content);

        disclaimerConfirm = findViewById(R.id.disclaimerConfirm);
        disclaimerConfirm.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        disclaimerCancel = findViewById(R.id.disclaimerCancel);
        disclaimerCancel.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));


        new MenuManager(this, toolbar, menuFragment, null);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        packageName = bundle.getString("packageName");
        externalAppId = bundle.getInt("externalAppId");


        SessionManager sm = new SessionManager(this);

        if(sm.isLoggedOn()) {
            LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

            if (user != null) {
                for (Disclaimer d : user.Disclaimers) {
                    if (d.SiteID == externalAppId) {
                        disclaimer = d;
                        break;
                    }
                }

                for (int i = 0; i < disclaimer.DisclaimerFields.size(); i++) {
                    DisclaimerField field = disclaimer.DisclaimerFields.get(i);
                    for (int j = 0; j < user.FullDisclaimer.size(); j++) {
                        if (field.BitwiseID == user.FullDisclaimer.get(j).BitwiseID) {
                            View disclaimerField = View.inflate(this, R.layout.block_disclalimer_field, null);
                            TextView tv_item = disclaimerField.findViewById(R.id.disclaimer_item);
                            TextView tv_value = disclaimerField.findViewById(R.id.disclaimer_value);
                            tv_item.setText(field.Name);
                            tv_item.setTextColor(Color.parseColor(Settings.colors.YearColor));
                            tv_value.setText(user.FullDisclaimer.get(j).Description);
                            disclaimerContent.addView(disclaimerField);
                            break;
                        }
                    }
                }
            }
            disclaimerConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

                    intent.putExtra("rt", "data");

                    startActivityForResult(intent, 1);
                }
            });
        }else{
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }

    }
}
