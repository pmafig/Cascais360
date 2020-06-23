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
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.App;
import nsop.neds.mycascais.Entities.Json.Colors;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.ExternalAppInfo;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.Entities.Json.Response;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegration;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegrationField;
import nsop.neds.mycascais.Entities.Json.User;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.SetDisclaimerRequest;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.DashboardManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.ResourcesManager;
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

        LinearLayout menuFragment = findViewById(R.id.menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        findViewById(R.id.menu_button_frame).setVisibility(View.GONE);

        int bitwiseAutotization = 0;

        LinearLayout disclaimerContent = findViewById(R.id.disclaimer_content);

        disclaimerConfirm = findViewById(R.id.disclaimerConfirm);
        disclaimerCancel = findViewById(R.id.disclaimerCancel);

        if(Settings.colors != null && !Settings.colors.YearColor.isEmpty()) {
            disclaimerConfirm.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
            disclaimerCancel.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        }else{
            disclaimerConfirm.setBackgroundColor(Color.parseColor("#BEBEBE"));
            disclaimerCancel.setBackgroundColor(Color.parseColor("#BEBEBE"));
        }

        diclaimerText = findViewById(R.id.disclaimer_textInfo);

        SessionManager sm = new SessionManager(this);

        Intent intent = getIntent();

        if(intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null && bundle.containsKey(Variables.PackageName)) {
                packageName = bundle.getString(Variables.PackageName);
            }
            if (bundle != null && bundle.containsKey(Variables.ExternalAppId)) {
                externalAppId = bundle.getInt(Variables.ExternalAppId);
            }
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

                if (appInfo != null && appInfo.AppName != null) {
                    diclaimerText.setText(String.format("Autoriza a applicação %s aceder aos seguintes dados:", appInfo.AppName));
                }else{
                    diclaimerText.setText("Por favor, repita a operação.");
                    disclaimerConfirm.setVisibility(View.GONE);disclaimerCancel.setVisibility(View.GONE);
                    disclaimer = null;
                }

                //diclaimerText.setText(String.format("Autoriza a applicação %s aceder aos seguintes dados: ", appInfo.AppName));

                if(disclaimer != null && disclaimer.DisclaimerFields != null) {
                    for (DisclaimerField field : disclaimer.DisclaimerFields) {
                        View disclaimerField = View.inflate(this, R.layout.block_disclalimer_field, null);
                        TextView tv_item = disclaimerField.findViewById(R.id.disclaimer_item);
                        TextView tv_value = disclaimerField.findViewById(R.id.disclaimer_value);
                        tv_item.setText(field.Name);
                        //tv_item.setTextColor(Color.parseColor(Settings.colors.YearColor));
                        tv_value.setText(field.Description);
                        disclaimerContent.addView(disclaimerField);

                        bitwiseAutotization += field.BitwiseID;
                    }
                }

                final int autorizationFields =  bitwiseAutotization;

                disclaimerConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClick(true, autorizationFields, externalAppId);
                    }
                });

                disclaimerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClick(false, autorizationFields, externalAppId);
                    }
                });
            }else{
                try {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(loginIntent, 1);
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private void setClick(final boolean accept, final int autorizationFields, final int appID){
        final ProgressDialog progressDialog = new ProgressDialog(DisclaimerActivity.this);
        SessionManager sm = new SessionManager(this);

        LoginUserResponse loginUser = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

        progressDialog.show();

        SetDisclaimerRequest disclaimerRequest = new SetDisclaimerRequest();
        disclaimerRequest.ssk = loginUser.SSK;
        disclaimerRequest.userid = loginUser.AuthID;
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
                    accept(appID);
                }else{
                    declined();
                }
            }
        });
    }

    private void accept(int appID){
        String key = "";
        SessionManager sm = new SessionManager(this);
        
        LoginUserResponse loginUserResponse = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);


        for (App app: loginUserResponse.AppList) {
            if(app.ID == appID){
                key = app.Key;
                break;
            }
        }

        sm.setExternalAppInfo(null);
        sm.setExternalAppPackageExternalId(0);
        sm.setExternalAppPackageName(null);

        ThirdPartyIntegration integrationData = new Gson().fromJson(sm.getUser(), ThirdPartyIntegration.class);
        integrationData.SessionExpirationDate = loginUserResponse.SessionExpirationDate;
        integrationData.SSK = loginUserResponse.SSK;
        integrationData.AuthID = loginUserResponse.AuthID;
        integrationData.MyCascaisID = loginUserResponse.MyCascaisID;
        integrationData.DisplayName = loginUserResponse.DisplayName;

        integrationData.Fields.clear();

        for (DisclaimerField d : disclaimer.DisclaimerFields){
            ThirdPartyIntegrationField f = new ThirdPartyIntegrationField();
            f.Description = d.Description;
            f.Name = d.Name;
            f.ValidationStatus = d.ValidationStatus;
            integrationData.Fields.add(f);
        }

        if(!key.isEmpty()) {
            try {//fc4e5f84847b4712b88f11db42fd804a
                CommonManager.launchApp(this, packageName, MessageEncryption.Encrypt(integrationData.toJson(), key));
            } catch (Exception ex) {
                CommonManager.launchApp(this, packageName, MessageEncryption.Encrypt("error... " + ex.getMessage(), key));
            }
        }
    }

    private void declined(){
        SessionManager sm = new SessionManager(this);

        sm.setExternalAppInfo(null);
        sm.setExternalAppPackageExternalId(0);
        sm.setExternalAppPackageName(null);

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.putExtra(packageName + ".vault", "declined");
        startActivityForResult(intent, 1);

    }
}
