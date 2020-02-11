package nsop.neds.mycascais;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.ExternalAppInfo;
import nsop.neds.mycascais.Entities.Json.Response;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.SetDisclaimerRequest;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.DashboardManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class DisclaimerActivity extends AppCompatActivity {

    private String packageName = "";
    private int externalAppId;

    private Disclaimer disclaimer;

    private TextView diclaimerText;

    private Button disclaimerConfirm;
    private Button disclaimerCancel;

    final List<DisclaimerField> disclaimerFieldList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        int bitwiseAutotization = 0;

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout disclaimerContent = findViewById(R.id.disclaimer_content);

        disclaimerConfirm = findViewById(R.id.disclaimerConfirm);
        disclaimerConfirm.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        disclaimerCancel = findViewById(R.id.disclaimerCancel);
        disclaimerCancel.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        diclaimerText = findViewById(R.id.disclaimer_textInfo);

        new MenuManager(this, toolbar, menuFragment, null);

        SessionManager sm = new SessionManager(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(intent.hasExtra(Variables.PackageName)) {
            packageName = bundle.getString(Variables.PackageName);
        }
        if(intent.hasExtra(Variables.ExternalAppId)) {
            externalAppId = bundle.getInt(Variables.ExternalAppId);
        }

        if(sm != null){
            String externalAppInfo = sm.getExternalAppInfo();
            ExternalAppInfo appInfo = null;

            if(!externalAppInfo.isEmpty()) {
                appInfo = new Gson().fromJson(sm.getExternalAppInfo(), ExternalAppInfo.class);
            }

            if(sm.isLoggedOn()) {
                LoginUserResponse user = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                if (user != null) {
                    for (Disclaimer d : user.Disclaimers) {
                        if (d.SiteID == externalAppId) {
                            disclaimer = d;
                            break;
                        }
                    }
                }

                if (appInfo != null) {
                    diclaimerText.setText(String.format(Settings.labels.DisclaimerTextInfo, appInfo.AppName));
                }else{
                    diclaimerText.setVisibility(View.GONE);
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

                            DisclaimerField f = new DisclaimerField();
                            f.Name = field.Name;
                            f.Description = user.FullDisclaimer.get(j).Description;
                            disclaimerFieldList.add(f);

                            bitwiseAutotization += field.BitwiseID;
                            break;
                        }
                    }
                }

                final int autorizationFields =  bitwiseAutotization;

                disclaimerConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClick(true, autorizationFields);
                    }
                });

                disclaimerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClick(false, autorizationFields);
                    }
                });
            }else{
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }
    }

    private void setClick(final boolean accept, final int autorizationFields){
        final ProgressDialog progressDialog = new ProgressDialog(DisclaimerActivity.this);
        SessionManager sm = new SessionManager(this);
        UserEntity user = AccountGeneral.getUser(this);

        progressDialog.setMessage(Settings.labels.ProcessingData);
        progressDialog.show();

        SetDisclaimerRequest disclaimerRequest = new SetDisclaimerRequest();
        disclaimerRequest.SSK = user.getSsk();
        disclaimerRequest.UserID = user.getUserId();
        disclaimerRequest.HasAccepted = accept;
        disclaimerRequest.AllowedFields = autorizationFields;
        disclaimerRequest.ExternalSiteID = sm.getExternalAppExternalId();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiMethods.SETDISCLAIMERRESPONSE), new Gson().toJson(disclaimerRequest), true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();

                if(accept) {
                    accept();
                }else{
                    diclained();
                }
            }
        });
    }

    private void accept(){
        SessionManager sm = new SessionManager(this);

        sm.setExternalAppInfo(null);
        sm.setExternalAppPackageExternalId(0);
        sm.setExternalAppPackageName(null);

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.putExtra(packageName + ".vault", new Gson().toJson(disclaimerFieldList));
        startActivityForResult(intent, 1);
    }

    private void diclained(){
        SessionManager sm = new SessionManager(this);

        sm.setExternalAppInfo(null);
        sm.setExternalAppPackageExternalId(0);
        sm.setExternalAppPackageName(null);

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.putExtra(packageName + ".vault", "declined");
        startActivityForResult(intent, 1);

    }
}
