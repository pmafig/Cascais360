package nsop.neds.mycascais;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.App;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegration;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegrationField;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.CountryListManager;
import nsop.neds.mycascais.Manager.ExternalAppManager;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.ResourcesManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new CommonManager().isNetworkAvailable(this)) {
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

                if (intent != null && bundle != null) {
                    if(bundle.containsKey(Variables.Id)){
                        new ResourcesManager(this, bundle.getString(Variables.Id)).execute(WebApiCalls.getResources());
                        return;
                    }

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

                    if (sm == null) {
                        sm = new SessionManager(getBaseContext());
                    }

                    Resources r = sm.getResources();

                    if (r != null) {
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

                    //new ExternalAppManager(this).execute(WebApiCalls.getExternalAppInfo(externalAppId));
                    final int finalExternalAppId = externalAppId;
                    final int finalExternalAppId1 = externalAppId;
                    final String finalPackageName = packageName;

                    new CountryListManager(this).execute(WebApiCalls.getCountryList(sm.getLangCodePosition() + 1));

                    WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.GetExternalAppInfo), String.format("{\"appid\":%s}", externalAppId),true, new TextHttpResponseHandler(){
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            new ExternalAppManager(SplashActivity.this).execute(WebApiCalls.getExternalAppInfo(finalExternalAppId));
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String data) {
                            String message = WebApiMessages.DecryptMessage(data);
                            SessionManager sm = new SessionManager(SplashActivity.this);

                            if(message != null) {
                                try {
                                    JSONObject responseData =  new JSONObject(message);;
                                    responseData = responseData.getJSONObject(Variables.ResponseData);
                                    sm.setExternalAppInfo(responseData.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //ExternalAppInfo appInfo = new Gson().fromJson(responseData.toString(), ExternalAppInfo.class);

                                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                final String name = finalPackageName;
                                final int appId = finalExternalAppId1;
                                String appKey = "";

                                Disclaimer disclaimer = null;

                                if (user != null) {
                                    for (Disclaimer d : user.Disclaimers) {
                                        if (d.SiteID == finalExternalAppId1) {
                                            disclaimer = d;
                                            break;
                                        }
                                    }
                                }

                                if (disclaimer == null) {
                                    Intent disclaimerIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(disclaimerIntent);
                                } else if (disclaimer != null) {

                                    for (App app: user.AppList) {
                                        if(app.ID == appId){
                                            appKey = app.Key;
                                            break;
                                        }
                                    }

                                    if (disclaimer.HasAccepted && !disclaimer.NeedUpdate) {
                                        ThirdPartyIntegration integrationData = new Gson().fromJson(sm.getUser(), ThirdPartyIntegration.class);

                                        for (DisclaimerField d : disclaimer.DisclaimerFields){
                                            ThirdPartyIntegrationField f = new ThirdPartyIntegrationField();
                                            f.Description = d.Description;
                                            f.Name = d.Name;
                                            f.ValidationStatus = d.ValidationStatus;
                                            integrationData.Fields.add(f);
                                        }

                                        new CommonManager().launchApp(SplashActivity.this, finalPackageName, MessageEncryption.Encrypt(integrationData.toJson(), appKey));

                                    } else if (disclaimer.HasDisclaimer || disclaimer.NeedUpdate) {
                                        Intent disclaimerIntent = new Intent(SplashActivity.this, DisclaimerActivity.class);
                                        disclaimerIntent.putExtra(Variables.PackageName, name);
                                        disclaimerIntent.putExtra(Variables.ExternalAppId, finalExternalAppId1);
                                        startActivity(disclaimerIntent);//"fc4e5f84847b4712b88f11db42fd804a"));
                                    } else {
                                        new CommonManager().launchApp(SplashActivity.this, finalPackageName, MessageEncryption.Encrypt(Settings.labels.DisclaimerDeniedbyUser, appKey)); //"fc4e5f84847b4712b88f11db42fd804a"));
                                    }
                                } else {
                                    new ResourcesManager(SplashActivity.this, true, false).execute(WebApiCalls.getResources());
                                }

                            }
                        }
                    });





                } else {
                    new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
                }
            } catch (Exception ex) {
                new ResourcesManager(this, true, false).execute(WebApiCalls.getResources());
            }
        }else{
            setContentView(R.layout.activity_no_internet);
        }
    }
}
