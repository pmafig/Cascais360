package nsop.neds.mycascais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class NotificationsListActivity extends AppCompatActivity {

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        new MenuManager(this, toolbar, menuFragment, Settings.labels.Notifications);

        TextView eventsNotification = findViewById(R.id.personal_data);
        eventsNotification.setText(Settings.labels.Events);

        TextView routesNotification = findViewById(R.id.change_password);
        routesNotification.setText(Settings.labels.PasswordRecoveryNewPassword);

        TextView placesNotification = findViewById(R.id.notifications);
        placesNotification.setText(Settings.labels.Notifications);


    }


    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();
        return parentIntent;
    }

}
