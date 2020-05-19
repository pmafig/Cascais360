package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.Json.Category;
import nsop.neds.mycascais.Entities.Json.EventsCategories;
import nsop.neds.mycascais.Entities.Json.NotificationsCategory;
import nsop.neds.mycascais.Entities.Json.PlacesCategories;
import nsop.neds.mycascais.Entities.Json.RoutesCategories;
import nsop.neds.mycascais.Entities.Json.TownCouncil;
import nsop.neds.mycascais.Entities.Json.TownCouncils;
import nsop.neds.mycascais.Entities.Json.UpdateSubscriptions;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMethods;

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
        final String description = bundle.getString(Variables.Description);


        TextView notificationTitle = findViewById(R.id.notifications_title);
        //notificationTitle.setTextColor(Color.parseColor(Settings.colors.YearColor));

        final LinearLayout categories = findViewById(R.id.notification_category);

        categories.removeAllViews();

        notificationTitle.setText(Settings.labels.SubscribeNotifications);

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.parseColor(Settings.colors.Gray2),
                Color.parseColor(Settings.colors.YearColor),
        };

        for(final TownCouncils c : Data.Towns){
            final View block = View.inflate(NotificationsTownActivity.this, R.layout.block_notification_town_item, null);

            Switch item = block.findViewById(R.id.notification_town);
            item.setText(c.Description);

            for(NotificationsCategory ca : Data.NotificationsCategoryList){
                if(ca.CID == id && ca.TCID == c.ID){
                    item.setChecked(true);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                item.setThumbTintList(new ColorStateList(states, thumbColors));
                item.setTrackTintList(new ColorStateList(states, thumbColors));
            }

            item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        NotificationsCategory category = new NotificationsCategory();
                        category.CID = id;
                        category.TCID = c.ID;

                        Data.NotificationsCategoryList.add(category);
                    }else{
                        for(int i = 0; i <  Data.NotificationsCategoryList.size(); i++){
                            NotificationsCategory ca = Data.NotificationsCategoryList.get(i);

                            if(ca.CID == id && ca.TCID == c.ID){
                                Data.NotificationsCategoryList.remove(i);
                            }
                        }
                    }

                    updateSubscriptions();
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


    public void updateSubscriptions(){

        SessionManager sm = new SessionManager(this);
        LoginUserResponse loginUser = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        UpdateSubscriptions updateSubscriptions = new UpdateSubscriptions();
        updateSubscriptions.ssk = loginUser.SSK;
        updateSubscriptions.userid = loginUser.AuthID;
        updateSubscriptions.Categories = Data.NotificationsCategoryList;

        String jsonRequest = new Gson().toJson(updateSubscriptions);

        WebApiClient.postNotifications(String.format("/%s/%s", WebApiClient.API.cms, WebApiMethods.UPDATESUBSCRIPTIONS), jsonRequest,  new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(NotificationsTownActivity.this, Settings.labels.TryAgain, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(NotificationsTownActivity.this, Settings.labels.SubscriptionUpdated, Toast.LENGTH_LONG).show();
            }
        });
    }
}
