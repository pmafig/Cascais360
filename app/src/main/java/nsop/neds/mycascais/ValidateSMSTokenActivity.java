package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import nsop.neds.mycascais.Entities.Json.ReportList;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenRequest;
import nsop.neds.mycascais.Entities.WebApi.ResendSMSTokenResponse;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

public class ValidateSMSTokenActivity extends AppCompatActivity {

    Button validateButton;
    EditText smsTokenField;
    TextView resendSmsToken;

    String token = "";
    String alertMessage = "";

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

            if (bundle.containsKey(Variables.AlertMessage)) {
                alertMessage = bundle.getString(Variables.AlertMessage);
            }

            if (bundle.containsKey(Variables.Token)) {
                token = bundle.getString(Variables.Token);

                if (!token.isEmpty() && !receivedToken) {
                    smsTokenField.setText(token);
                }
            }
        }

        final String finalToken = token;

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
                        RecoverSmsToken(finalToken);
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

                request.SmsToken = token;
                request.LanguageID = sm.getLangCodePosition() + 1;

                //new ResendSMSManager(ValidateSMSTokenActivity.this).execute(WebApiCalls.getResendSmsToken(request));

                RecoverSmsToken(token);
            }
        });

        if(!alertMessage.isEmpty()){
            LayoutManager.alertMessage(this, alertMessage);
        }
    }


    private void ValidateSmsToken() {
        EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String token = smsToken.getText().toString();

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        String url = "";

        SessionManager session = new SessionManager(this);

        /*if(session.getRecover()){
            jsonRequest = String.format("{\"SmsToken\":\"%s\", \"PhoneNumber\":\"%s\", \"CountryCode\":\"%s\"}", token, new SessionManager(this).getMobileNumber(), "+351");
            webapimethod = WebApiMethods.VALIDATERECOVERSMSTOKEN;
        }else if (session.getNewAccount()){
            jsonRequest = String.format("{\"SmsToken\":\"%s\"}", smsToken.getText());
            webapimethod = WebApiMethods.VALIDATESMSTOKEN;
        }else {*/
        progressDialog.setMessage("Validando código...");
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] availableAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

        jsonRequest = String.format("{\"SmsToken\":\"%s\", \"ssk\":\"%s\", \"userid\":\"%s\", i:false}", smsToken.getText(), ssk, userId);
        url = String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateMobilePhoneContact);


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

                String json = new MessageEncryption().Decrypt(WebApiClient.SITE_KEY, response.replace('"', ' ').trim());
                System.out.println(json);

                String message = ReportManager.getErrorReportList(json);

                progressDialog.dismiss();

                if (ReportManager.IsValid(json)) {
                    Toast.makeText(getBaseContext(), Settings.labels.ContactChanged, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ValidateSMSTokenActivity.this, EditAccountActivity.class));
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

    private void ValidateNewRegisterSmsToken() {
        /*EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String token = smsToken.getText().toString();

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        String url = "";

        progressDialog.setMessage("Validando código...");

        jsonRequest = String.format("{\"SmsToken\":\"%s\"}", token);
        url = String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateSmsToken);

        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                //TODO: Alter string
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.info_message_new_user_insuccess), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                //TODO Alter string
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.info_message_new_user_success), Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ValidateSMSTokenActivity.this, LoginActivity.class));
            }
        });*/

        if(!token.isEmpty()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(Variables.Token, token);
            startActivity(intent);
        }else{
            LayoutManager.alertMessage(this, Settings.labels.InvalidToken);
        }

    }

    private void RecoverSmsToken(String token) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(Settings.labels.ProcessingData);
        progressDialog.show();

        SessionManager sm = new SessionManager(this);
        ResendSMSTokenRequest request = new ResendSMSTokenRequest();

        request.SmsToken = token;
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

        SessionManager session = new SessionManager(this);

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
