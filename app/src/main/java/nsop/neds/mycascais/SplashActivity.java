package nsop.neds.mycascais;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.CountryListManager;
import nsop.neds.mycascais.Manager.ExternalAppManager;
import nsop.neds.mycascais.Manager.ResourcesManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String packageName = "";
            int externalAppId = 0;

            SessionManager sm = new SessionManager(this);
            Settings.LangCode = sm.getLangCode();

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            setContentView(R.layout.activity_splash);

            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.hide();
            }

            if(intent != null && bundle != null) {
                if (bundle.containsKey(Variables.PackageName)) {
                    packageName = bundle.getString(Variables.PackageName);
                    sm.setExternalAppPackageName(packageName);
                }
                if (bundle.containsKey(Variables.ExternalAppId)) {
                    externalAppId = bundle.getInt(Variables.ExternalAppId);
                    sm.setExternalAppPackageExternalId(externalAppId);
                }
            }

            if (!packageName.isEmpty() && externalAppId > 0) {

                if(sm == null){
                    sm = new SessionManager(getBaseContext());
                }

                Resources r = sm.getResources();

                if(r != null) {
                    Settings.colors = r.Colors;
                    Settings.labels = r.Labels;
                    Settings.labels = r.Labels;
                    Settings.aboutApp = r.AboutApp;
                    Settings.menus = r.Menu;
                    Data.NotificationsEventsCategories = r.EventsCategories;
                    Data.NotificationsPlacesCategories = r.PlacesCategories;
                    Data.NotificationsRoutesCategories = r.RoutesCategories;
                    Data.Towns = r.TownCouncils;
                }

                new CountryListManager(this).execute(WebApiCalls.getCountryList(sm.getLangCodePosition() + 1));

                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                final String name = packageName;
                final int appId = externalAppId;

                Disclaimer disclaimer = null;

                if (user != null) {
                    for (Disclaimer d : user.Disclaimers) {
                        if (d.SiteID == externalAppId) {
                            disclaimer = d;
                            break;
                        }
                    }
                }

                if (disclaimer == null) {
                    Intent disclaimerIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(disclaimerIntent);
                } else if (disclaimer != null) {
                    if(disclaimer.HasDisclaimer || disclaimer.NeedUpdate) {
                        Intent disclaimerIntent = new Intent(SplashActivity.this, DisclaimerActivity.class);
                        disclaimerIntent.putExtra(Variables.PackageName, name);
                        disclaimerIntent.putExtra(Variables.ExternalAppId, externalAppId);
                        startActivity(disclaimerIntent);
                    }else if(disclaimer.HasAccepted){
                        final List<DisclaimerField> disclaimerFieldList = new ArrayList<>();

                        if(disclaimer != null && disclaimer.DisclaimerFields != null && disclaimer.DisclaimerFields.size() > 0) {
                            for (int i = 0; i < disclaimer.DisclaimerFields.size(); i++) {
                                DisclaimerField field = disclaimer.DisclaimerFields.get(i);
                                if(user.FullDisclaimer != null && user.FullDisclaimer.size() > 0) {
                                    for (int j = 0; j < user.FullDisclaimer.size(); j++) {
                                        if (field.BitwiseID == user.FullDisclaimer.get(j).BitwiseID) {

                                            DisclaimerField f = new DisclaimerField();
                                            f.Name = field.Name;
                                            f.Description = user.FullDisclaimer.get(j).Description;
                                            disclaimerFieldList.add(f);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        new CommonManager().launchApp(this, packageName, new MessageEncryption().Encrypt(new Gson().toJson(disclaimerFieldList), "fc4e5f84847b4712b88f11db42fd804a"));
                    }else{
                        new CommonManager().launchApp(this, packageName, new MessageEncryption().Encrypt(   Settings.labels.DisclaimerDeniedbyUser, "fc4e5f84847b4712b88f11db42fd804a"));
                    }
                } else {
                    new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
                }

            /*String json = String.format("{\"appid\":%s}", externalAppId);

            final String name = packageName;
            final int appId = externalAppId;

            WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.GetExternalAppInfo), json,true, new TextHttpResponseHandler(){
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    String message = WebApiMessages.DecryptMessage(responseString);
                    Toast.makeText(SplashActivity.this, Settings.labels.AppInMaintenanceMessage, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    String message = WebApiMessages.DecryptMessage(responseString);
                    Intent disclaimer = new Intent(SplashActivity.this, DisclaimerActivity.class);
                    disclaimer.putExtra(Variables.PackageName , name);
                    disclaimer.putExtra(Variables.ExternalAppId, appId);
                    startActivity(disclaimer);
                }
            });*/
                new ExternalAppManager(this).execute(WebApiCalls.getExternalAppInfo(externalAppId));
            } else {
                if (intent.hasExtra(Variables.Id)) {
                    new ResourcesManager(this, false, false).execute(WebApiCalls.getResources());
                    Intent i = new Intent(this, DetailActivity.class);
                    i.putExtra(Variables.Id, getIntent().getStringExtra(Variables.Id));
                    startActivity(i);
                } else {
                    new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
                }
            }
        }catch (Exception ex){
            new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
        }
    }
}
