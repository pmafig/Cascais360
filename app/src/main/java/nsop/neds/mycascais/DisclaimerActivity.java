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
import nsop.neds.mycascais.Entities.Json.Colors;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.ExternalAppInfo;
import nsop.neds.mycascais.Entities.Json.Resources;
import nsop.neds.mycascais.Entities.Json.Response;
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
                    diclaimerText.setText(String.format("Autoriza a APP %s aceder aos seguintes dados:", appInfo.AppName));
                }else{
                    diclaimerText.setText("A APP não tem permissões!");
                    disclaimerConfirm.setVisibility(View.GONE);disclaimerCancel.setVisibility(View.GONE);
                }

                diclaimerText.setText(String.format("Autoriza a applicação %s aceder aos seguintes dados: ", appInfo.AppName));

                if(disclaimer != null && disclaimer.DisclaimerFields != null && disclaimer.DisclaimerFields.size() > 0) {
                    for (int i = 0; i < disclaimer.DisclaimerFields.size(); i++) {
                        DisclaimerField field = disclaimer.DisclaimerFields.get(i);
                        if(user.FullDisclaimer != null && user.FullDisclaimer.size() > 0) {
                            for (int j = 0; j < user.FullDisclaimer.size(); j++) {
                                if (field.BitwiseID == user.FullDisclaimer.get(j).BitwiseID) {
                                    View disclaimerField = View.inflate(this, R.layout.block_disclalimer_field, null);
                                    TextView tv_item = disclaimerField.findViewById(R.id.disclaimer_item);
                                    TextView tv_value = disclaimerField.findViewById(R.id.disclaimer_value);
                                    tv_item.setText(field.Name);
                                    //tv_item.setTextColor(Color.parseColor(Settings.colors.YearColor));
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

    private void setClick(final boolean accept, final int autorizationFields){
        final ProgressDialog progressDialog = new ProgressDialog(DisclaimerActivity.this);
        SessionManager sm = new SessionManager(this);
        UserEntity user = AccountGeneral.getUser(this);

        //progressDialog.setMessage(Settings.labels.ProcessingData);
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

        try {
            new CommonManager().launchApp(this, packageName, new MessageEncryption().Encrypt(new Gson().toJson(disclaimerFieldList), "fc4e5f84847b4712b88f11db42fd804a"));
            //intent.putExtra(packageName + ".vault", new MessageEncryption().Encrypt(new Gson().toJson(disclaimerFieldList), "fc4e5f84847b4712b88f11db42fd804a"));
        }catch (Exception ex){
            new CommonManager().launchApp(this, packageName, new MessageEncryption().Encrypt("error... " + ex.getMessage(), "fc4e5f84847b4712b88f11db42fd804a"));
            //intent.putExtra(packageName + ".vault", "error... " + ex.getMessage());
        }
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
