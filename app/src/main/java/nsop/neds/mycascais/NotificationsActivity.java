package nsop.neds.mycascais;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.Search;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.NotificationsManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class NotificationsActivity extends AppCompatActivity {

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        new MenuManager(this, toolbar, menuFragment, Settings.labels.Notifications);

        TextView titleNot = findViewById(R.id.notifications_title);
        titleNot.setText(Settings.labels.SubscribeNotifications);

        TextView titleNotification = findViewById(R.id.subscription_title);
        titleNotification.setText(Settings.labels.SubscribedContents);

        TextView titleNotificationSubscription = findViewById(R.id.subscription_notification_title);
        titleNotificationSubscription.setText(Settings.labels.SubscribedNotifications);

        TextView eventsNotification = findViewById(R.id.notification_events);
        eventsNotification.setText(Settings.labels.Events);

        TextView routesNotification = findViewById(R.id.notification_routes);
        routesNotification.setText(Settings.labels.Routes);

        TextView placesNotification = findViewById(R.id.notifications_places);
        placesNotification.setText(Settings.labels.Places);

        eventsNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationsListActivity.class);
                intent.putExtra(Variables.Type,  Variables.Events);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        routesNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationsListActivity.class);
                intent.putExtra(Variables.Type, Variables.Routes);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        placesNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationsListActivity.class);
                intent.putExtra(Variables.Type, Variables.Places);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        SessionManager sm = new SessionManager(this);
        LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        new NotificationsManager(this, (LinearLayout) findViewById(R.id.notification_frame), (LinearLayout) findViewById(R.id.subscriptions_frame))
                .execute(WebApiCalls.getNotification(user.SSK, user.AuthID));
    }


    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();
        return parentIntent;
    }

}
