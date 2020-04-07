package nsop.neds.mycascais;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nsop.neds.mycascais.Entities.Json.Category;
import nsop.neds.mycascais.Entities.Json.EventsCategories;
import nsop.neds.mycascais.Entities.Json.PlacesCategories;
import nsop.neds.mycascais.Entities.Json.RoutesCategories;
import nsop.neds.mycascais.Entities.Json.TownCouncil;
import nsop.neds.mycascais.Entities.Json.TownCouncils;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;

public class NotificationsTownActivity extends AppCompatActivity {

    LinearLayout menuFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        setContentView(R.layout.activity_notifications_towns);

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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final int id = bundle.getInt(Variables.Id);


        TextView notificationTitle = findViewById(R.id.notifications_title);
        notificationTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));

        final LinearLayout categories = findViewById(R.id.notification_category);

        categories.removeAllViews();

        notificationTitle.setText(Settings.labels.Events);

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.parseColor(Settings.colors.Gray2),
                Color.parseColor(Settings.colors.YearColor),
        };

        for(TownCouncils c : Data.Towns){
            View block = View.inflate(NotificationsTownActivity.this, R.layout.block_notification_town_item, null);

            Switch item = block.findViewById(R.id.notification_town);
            item.setText(c.Description);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                item.setThumbTintList(new ColorStateList(states, thumbColors));
                item.setTrackTintList(new ColorStateList(states, thumbColors));
            }

            item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){

                        Category category = new Category();
                        category.CategoryID = id;
                        category.TownCouncilID = 1;

                        Data.NotificationsCategoryList.add(category);
                    }
                }
            });

            categories.addView(block);
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();
        return parentIntent;
    }

}
