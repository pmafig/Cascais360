package nsop.neds.mycascais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.App;
import nsop.neds.mycascais.Entities.Json.Consent;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.DisclaimerField;
import nsop.neds.mycascais.Entities.Json.ExternalAppInfo;
import nsop.neds.mycascais.Entities.Json.Labels;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegration;
import nsop.neds.mycascais.Entities.Json.ThirdPartyIntegrationField;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.SetConsentRequest;
import nsop.neds.mycascais.Entities.WebApi.SetDisclaimerRequest;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;
import nsop.neds.mycascais.WebApi.WebApiMethods;

public class ConsentsActivity extends AppCompatActivity {
    private TextView consentTitle;

    private Button consentContinue;

    final List<Consent> consentList = new ArrayList<>();

    SessionManager mSession;

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consents);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        LinearLayout consentContent = findViewById(R.id.consent_content);

        TextView consent_bootom_message = findViewById(R.id.consent_bottom_message);

        consent_bootom_message.setText(Settings.labels.ConsentBottomMessage);

        consentContinue = findViewById(R.id.consentContinue);

        if(Settings.colors != null && !Settings.colors.YearColor.isEmpty()) {
            consentContinue.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        }else{
            consentContinue.setBackgroundColor(Color.parseColor("#BEBEBE"));
        }

        consentTitle = findViewById(R.id.consent_textInfo);

        mSession = new SessionManager(this);

        if(mSession != null){

            if(mSession.isLoggedOn()) {

                consentTitle.setText(Settings.labels.SubscribeNotifications);

                LoginUserResponse user = new Gson().fromJson(mSession.getUser(), LoginUserResponse.class);

                if(user.ShowConsent) {
                    findViewById(R.id.menu_button_frame).setVisibility(View.GONE);
                }else{
                    new MenuManager(this, toolbar, menuFragment, null);
                }

                if (user != null) {

                    for (final Consent c : user.AvailableConsents) {
                        View consentField = View.inflate(this, R.layout.block_consent_list_item, null);

                        TextView tv_consent = consentField.findViewById(R.id.consent_text);

                        String consentDesription = c.Description;

                        if(c.IsRequired) {
                            consentDesription += " *";
                        }

                        tv_consent.setText(consentDesription);

                        final RadioButton rd_y = consentField.findViewById(R.id.radioButtonYes);
                        rd_y.setText(Settings.labels.Yes);
                        rd_y.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(rd_y.isChecked()){
                                    addConsent(c, true);
                                }
                            }
                        });

                        final RadioButton rd_n = consentField.findViewById(R.id.radioButtonNo);
                        rd_n.setText(Settings.labels.No);

                        rd_n.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(rd_n.isChecked()){
                                    addConsent(c, false);
                                }
                            }
                        });

                        for (final Consent r : user.RegisteredConsents){
                            if (r.ConsentID == c.ID){
                                if(r.HasAccepted){
                                    rd_y.setChecked(true);
                                }else {
                                    rd_n.setChecked(true);
                                }
                            }
                        }

                        consentContent.addView(consentField);
                    }

                    allRequiredChecked(user.RegisteredConsents);
                }

                consentContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClick();
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

    private void addConsent(Consent consent, boolean state){
        int index = -1;

        if(consent != null){
            for(int i = 0; i < consentList.size(); i++){
                if(consent.ID == consentList.get(i).ID){
                    index = i;
                    break;
                }
            }

            if(index != -1){
                consentList.get(index).HasAccepted = state;
            }else{
                consentList.add(new Consent(consent.ID, state, consent.IsRequired));
            }
        }

        allRequiredChecked(consentList);
    }

    private void allRequiredChecked(List<Consent> list){
        LoginUserResponse loginUser = new Gson().fromJson(mSession.getUser(), LoginUserResponse.class);
        boolean valid = false;

        for(Consent r : loginUser.RegisteredConsents){
            boolean contains = false;
            for(int i = 0; i < list.size(); i++){
                if(r.ConsentID == list.get(i).ID){
                    contains = true;
                }
            }
            if(!contains){
                list.add(r);
            }
        }

        for (Consent c : loginUser.AvailableConsents) {
            if(c.IsRequired){
                valid = false;
                for (Consent l : list) {
                    if(c.ID == l.ID || c.ID == l.ConsentID){
                        valid = true;
                        break;
                    }
                }
            }
        }

        Button button = findViewById(R.id.consentContinue);
        button.setEnabled(valid);
        button.setBackgroundColor(!valid ? Color.parseColor(Settings.colors.Gray2) : Color.parseColor(Settings.colors.YearColor));
    }

    //private List<Consent> currentList = new ArrayList<>();

    private void setClick(){
        final ProgressDialog progressDialog = new ProgressDialog(ConsentsActivity.this);

        LoginUserResponse loginUser = new Gson().fromJson(mSession.getUser(), LoginUserResponse.class);

        progressDialog.show();

        SetConsentRequest consentRequest = new SetConsentRequest();
        consentRequest.ssk = loginUser.SSK;
        consentRequest.userid = loginUser.AuthID;
        consentRequest.Consents = consentList;

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.crm, WebApiMethods.SETCONSENT), new Gson().toJson(consentRequest), true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                String message = WebApiMessages.DecryptMessage(response);

                LoginUserResponse user = new Gson().fromJson(mSession.getUser(), LoginUserResponse.class);

                /*if(currentList.size() <= user.AvailableConsents.size()){
                    user.RegisteredConsents = currentList;
                }*/

                if(user.RegisteredConsents.size() > 0){
                    for(Consent c : consentList) {
                        boolean e = false;
                        for (int i = 0; i < user.RegisteredConsents.size(); i++) {
                            if (c.ID == user.RegisteredConsents.get(i).ID) {
                                e = true;
                                user.RegisteredConsents.get(i).HasAccepted = c.HasAccepted;
                            }
                        }
                        if(!e){
                            user.RegisteredConsents.add(c);
                        }
                    }
                }else{
                    user.RegisteredConsents = consentList;
                }

                mSession.setUser(new Gson().toJson(user));

                String externalAppInfo = mSession.getExternalAppInfo();

                if(mSession != null && !externalAppInfo.isEmpty()) {
                    Intent intent = new Intent(ConsentsActivity.this, DisclaimerActivity.class);
                    intent.putExtra(Variables.PackageName, mSession.getExternalAppPackageName());
                    intent.putExtra(Variables.ExternalAppId, mSession.getExternalAppExternalId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ConsentsActivity.this, ProfileActivity.class);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    startActivity(intent);
                }

                progressDialog.dismiss();
            }
        });
    }
}
