package nsop.neds.mycascais;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.mycascais.Entities.Json.EventsCategories;
import nsop.neds.mycascais.Entities.Json.PlacesCategories;
import nsop.neds.mycascais.Entities.Json.RoutesCategories;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class NotificationsListActivity extends AppCompatActivity {

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        setContentView(R.layout.activity_notifications_list);

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

        TextView notificationTitle = findViewById(R.id.notifications_title);
        notificationTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));

        String type = getIntent().getStringExtra(Variables.Type);

        LinearLayout categories = findViewById(R.id.notification_category);

        categories.removeAllViews();

        switch (type) {
            case Variables.Events:
                notificationTitle.setText(Settings.labels.Events);

                for(final EventsCategories e : Data.NotificationsEventsCategories){
                    View block = View.inflate(NotificationsListActivity.this, R.layout.block_notification_item, null);

                    TextView item = block.findViewById(R.id.notification_item);
                    item.setText(e.Description);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NotificationsListActivity.this, NotificationsTownActivity.class);
                            intent.putExtra(Variables.Description, e.Description);
                            intent.putExtra(Variables.Id, e.ID);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                    categories.addView(block);
                }

                break;
            case Variables.Routes:
                notificationTitle.setText(Settings.labels.Routes);

                for(final RoutesCategories r : Data.NotificationsRoutesCategories){
                    View block = View.inflate(NotificationsListActivity.this, R.layout.block_notification_item, null);

                    TextView item = block.findViewById(R.id.notification_item);
                    item.setText(r.Description);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NotificationsListActivity.this, NotificationsTownActivity.class);
                            intent.putExtra(Variables.Description, r.Description);
                            intent.putExtra(Variables.Id, r.ID);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                    categories.addView(block);
                }

                break;
            case Variables.Places:
                notificationTitle.setText(Settings.labels.Places);

                for(final PlacesCategories p : Data.NotificationsPlacesCategories){
                    View block = View.inflate(NotificationsListActivity.this, R.layout.block_notification_item, null);

                    TextView item = block.findViewById(R.id.notification_item);
                    item.setText(p.Description);

                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NotificationsListActivity.this, NotificationsTownActivity.class);
                            intent.putExtra(Variables.Description, p.Description);
                            intent.putExtra(Variables.Id, p.ID);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                    categories.addView(block);
                }
                break;
        }

    }


    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();
        return parentIntent;
    }

}
