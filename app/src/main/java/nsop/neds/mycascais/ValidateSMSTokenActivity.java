package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.Labels;
import nsop.neds.mycascais.Entities.Json.PhoneContacts;
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.Json.ValidationStates;
import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenRequest;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenResponse;
import nsop.neds.mycascais.Manager.ContactAsAuth;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class ValidateSMSTokenActivity extends AppCompatActivity {

    Button validateButton;
    EditText smsTokenField;
    TextView resendSmsToken;

    String token = "";
    String mobileNumber = "";
    int mobileId;
    String alertMessage = "";
    boolean isAuth = false;

    boolean receivedToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_sms_token);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*TextView title = toolbar.findViewById(R.id.title_quero_ver);
        title.setTextColor(Color.parseColor(Settings.color));
        title.setText(R.string.title_activity_mycascais);*/

        validateButton = findViewById(R.id.accountValidateSmsToken);
        smsTokenField = findViewById(R.id.smsTokenPhone);
        resendSmsToken = findViewById(R.id.resendSmsToken);

        validateButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        resendSmsToken.setText(Settings.labels.ResendSMS);

        //region obtem token via sms ou via direta (inserido pelo utilizador)
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            if (bundle.containsKey(Variables.ReceivedToken)) {
                receivedToken = true;
                token = bundle.getString(Variables.ReceivedToken);
            }

            if (bundle.containsKey(Variables.MobileId)) {
                mobileId = bundle.getInt(Variables.MobileId);
            }

            if (bundle.containsKey(Variables.IsAuth)) {
                isAuth = bundle.getBoolean(Variables.IsAuth);
            }

            if (bundle.containsKey(Variables.MobileNumber)) {
                mobileNumber = bundle.getString(Variables.MobileNumber);
            }

            if (bundle.containsKey(Variables.AlertMessage)) {
                alertMessage = bundle.getString(Variables.AlertMessage);
            }

            if (bundle.containsKey(Variables.Token)) {
                token = bundle.getString(Variables.Token);

                if (!token.isEmpty() && !receivedToken) {
                    //JUST FOR DEBUGGING
                    //smsTokenField.setText(token);
                }
            }
        }

        final String finalMobileNumber = mobileNumber;

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            switch (Data.SmsValidationContext) {
                case changeContact:
                    ValidateSmsToken();
                    break;
                case newAccount:
                    ValidateNewRegisterSmsToken();
                    break;
                case recoverAccount:
                    RecoverSmsToken(finalMobileNumber);
                    break;
                case addAuth:
                    ValidateCustomerContact();
                    break;
            }
            }
        });

        resendSmsToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionManager sm = new SessionManager(ValidateSMSTokenActivity.this);
                ResendSMSTokenRequest request = new ResendSMSTokenRequest();

                request.PhoneNumber = finalMobileNumber;
                request.LanguageID = sm.getLangCodePosition() + 1;

                //new ResendSMSManager(ValidateSMSTokenActivity.this).execute(WebApiCalls.getResendSmsToken(request));

                RecoverSmsToken(finalMobileNumber);
            }
        });

        if(!alertMessage.isEmpty()){
            LayoutManager.alertMessage(this, alertMessage);
        }
    }


    private void ValidateSmsToken() {
        EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String token = smsToken.getText().toString();

        SessionManager sm = new SessionManager(ValidateSMSTokenActivity.this);

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Validando código...");

        final UserEntity user = AccountGeneral.getUser(this);

        jsonRequest = String.format("{\"Token\":\"%s\", \"ssk\":\"%s\", \"userid\":\"%s\", LanguageID:%s}", token, user.getSsk(), user.getUserId(), sm.getLangCodePosition() + 1);
        String url = String.format("/%s/%s", WebApiClient.API.crm, WebApiClient.METHODS.ValidateEntityState);

        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                //TODO neste momento não é possivel fazer esta operação - message
                //startActivity(new Intent(SmsTokenValidationActivity.this, ErrorActivity.class));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                progressDialog.dismiss();

                final String message = WebApiMessages.DecryptMessage(response);

                JSONObject jsonMessage = null;
                String receivedMessage = "";

                try {
                    jsonMessage = new JSONObject(message);
                } catch (JSONException ex) { }

                try {
                    if (jsonMessage.has("ReportList") && !jsonMessage.isNull("ReportList")) {

                        Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

                        List<ReportList> reportList = null;

                        reportList = new Gson().fromJson(jsonMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);


                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < reportList.size(); i++) {
                            sb.append(reportList.get(i).Description);
                            if (i + 1 < reportList.size()) {
                                sb.append("\n");
                            }
                        }

                        if(sb.length() > 0) {
                            receivedMessage = sb.toString();
                            LayoutManager.alertMessage(ValidateSMSTokenActivity.this, receivedMessage);
                        }else{
                            SessionManager sm = new SessionManager(ValidateSMSTokenActivity.this);

                            if(sm.getMobileNumber() == null || sm.getMobileNumber().equals("")){
                                sm.setMobileNumber(mobileNumber);
                            }

                            if(isAuth){
                                sm.setMobileNumberMain(true);

                                LoginUserResponse userINSession = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                for(PhoneContacts phone : userINSession.PhoneContacts){
                                    if(phone.Number.startsWith(mobileNumber)){
                                        if(phone.ValidationStates == null){
                                            phone.ValidationStates = new ArrayList<>();
                                            ValidationStates vs = new ValidationStates();
                                            vs.TypeID = 2;
                                            vs.Active = true;
                                            phone.ValidationStates.add(vs);
                                        }else{
                                            for(int i = 0; i < userINSession.PhoneContacts.size(); i++){
                                                if(userINSession.PhoneContacts.get(0).Number.startsWith(mobileNumber)) {
                                                    if(userINSession.PhoneContacts.get(0).ValidationStates != null){
                                                        userINSession.PhoneContacts.get(0).ValidationStates.get(0).TypeID = 2;
                                                        userINSession.PhoneContacts.get(0).Login = true;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }

                                sm.setUser(new Gson().toJson(userINSession));

                                new ContactAsAuth().execute(WebApiCalls.setMobileAuth(user.getSsk(), user.getUserId(), mobileId));
                            }else{
                                LoginUserResponse userINSession = new Gson().fromJson(sm.getUser(), LoginUserResponse.class);

                                for(PhoneContacts phone : userINSession.PhoneContacts){
                                    if(phone.Number.startsWith(mobileNumber)){
                                        if(phone.ValidationStates == null){
                                            phone.ValidationStates = new ArrayList<>();
                                            ValidationStates vs = new ValidationStates();
                                            vs.TypeID = 2;
                                            vs.Active = true;
                                            phone.ValidationStates.add(vs);
                                        }else{
                                            for(int i = 0; i < userINSession.PhoneContacts.size(); i++){
                                                if(userINSession.PhoneContacts.get(0).Number.startsWith(mobileNumber)) {
                                                    if(userINSession.PhoneContacts.get(0).ValidationStates != null){
                                                        userINSession.PhoneContacts.get(0).ValidationStates.get(0).TypeID = 2;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }

                                sm.setUser(new Gson().toJson(userINSession));
                            }

                            Intent intent = new Intent(ValidateSMSTokenActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }

                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(ValidateSMSTokenActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ValidateNewRegisterSmsToken() {
        EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String insertToken = smsToken.getText().toString();

        if(!insertToken.isEmpty() && insertToken.equals(token)){
            Intent intent = new Intent(ValidateSMSTokenActivity.this, RegisterActivity.class);
            intent.putExtra(Variables.Token, token);
            startActivity(intent);
        }else {
            LayoutManager.alertMessage(this, Settings.labels.InvalidToken);
        }
    }

    private void RecoverSmsToken(String phoneNumber) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(Settings.labels.ProcessingData);
        progressDialog.show();

        SessionManager sm = new SessionManager(this);
        ResendSMSTokenRequest request = new ResendSMSTokenRequest();

        request.PhoneNumber = phoneNumber;
        request.LanguageID = sm.getLangCodePosition() + 1;

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ResendSMSToken), new Gson().toJson(request), true,  new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(ValidateSMSTokenActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                ResendSmsTokenSuccess(response);
            }
        });

    }

    private void ResendSmsTokenSuccess(String response){
        final String message = WebApiMessages.DecryptMessage(response);

        JSONObject responseMessage = null;

        try {
            responseMessage = new JSONObject(message);

            ResendSMSTokenResponse responseData = null;

            if (responseMessage.has(Variables.ResponseData)) {
                responseData = new Gson().fromJson(responseMessage.getJSONObject(Variables.ResponseData).toString(), ResendSMSTokenResponse.class);
            }

            Type ReportListType = new TypeToken<ArrayList<ReportList>>() {}.getType();

            if (responseMessage.has(Variables.ReportList)) {
                List<ReportList> reportList = new Gson().fromJson(responseMessage.getJSONArray(Variables.ReportList).toString(), ReportListType);

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < reportList.size(); i++) {
                    sb.append(reportList.get(i).Description);
                    if (i + 1 < reportList.size()) {
                        sb.append("\n");
                    }
                }
                if(sb.length() > 0) {
                    LayoutManager.alertMessage(this, Settings.labels.ResendSMS, sb.toString());
                }else{
                    Toast.makeText(this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException ex) {

        }
    }

    private void ValidateCustomerContact() {
        EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String token = smsToken.getText().toString();

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        String url = "";

        final SessionManager session = new SessionManager(this);

        progressDialog.setMessage("Validando código...");
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] availableAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

        jsonRequest = String.format("{\"Token\":\"%s\", \"ssk\":\"%s\", \"userid\":\"%s\", i:false}", token, ssk, userId);
        url = String.format("/%s/%s", WebApiClient.API.cms, WebApiClient.METHODS.ValidateCustomerContact);

        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(ValidateSMSTokenActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                String json = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, response.replace('"', ' ').trim());

                String message = ReportManager.getErrorReportList(json);

                progressDialog.dismiss();

                if (ReportManager.IsValidated(json)) {

                    Toast.makeText(getBaseContext(), Settings.labels.ContactAuth, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ValidateSMSTokenActivity.this, ProfileActivity.class));
                } else {
                    if (!message.isEmpty()) {
                        LayoutManager.alertMessage(ValidateSMSTokenActivity.this, message.trim());
                    } else {
                        LayoutManager.alertMessage(ValidateSMSTokenActivity.this, Settings.labels.AppInMaintenanceMessage);
                    }
                }
            }
        });
    }
}
