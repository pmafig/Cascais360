package nsop.neds.cascais360;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import androidx.appcompat.widget.Toolbar;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.WebApiClient;

public class ValidateSMSTokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_sms_token);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*TextView title = toolbar.findViewById(R.id.title_quero_ver);
        title.setTextColor(Color.parseColor(Settings.color));
        title.setText(R.string.title_activity_mycascais);*/

        Button validateButton = findViewById(R.id.accountValidateSmsToken);
        validateButton.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

        EditText smsTokenField = findViewById(R.id.smsTokenPhone);

        Intent intent = getIntent();
        smsTokenField.setText(intent.getStringExtra(Variables.SMSToken));

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sm = new SessionManager(getBaseContext());
                if(sm.getNewAccount()){
                    ValidateSmsToken();
                }else {
                    ValidateNewRegisterSmsToken();
                }
            }
        });
    }

    private void ValidateSmsToken(){
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
            Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

            String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
            String userId = mAccountManager.getUserData(availableAccounts[0], "UserId");

            jsonRequest = String.format("{\"SmsToken\":\"%s\", \"ssk\":\"%s\", \"userid\":\"%s\", i:false}", smsToken.getText(), ssk, userId);
            url = String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateMobilePhoneContact);


        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                progressDialog.dismiss();
                //startActivity(new Intent(SmsTokenValidationActivity.this, ErrorActivity.class));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                progressDialog.dismiss();
                //TODO string here
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.info_message_sms_changePhoneNumber_success), Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ValidateSMSTokenActivity.this, ProfileActivity.class));
            }
        });
    }

    private void ValidateNewRegisterSmsToken(){
        EditText smsToken = this.findViewById(R.id.smsTokenPhone);

        final String token = smsToken.getText().toString();

        String jsonRequest = "";

        final ProgressDialog progressDialog = new ProgressDialog(this);

        String url = "";

        progressDialog.setMessage("Validando código...");

        jsonRequest = String.format("{\"SmsToken\":\"%s\"}", token);
        url = String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.ValidateSmsToken);

        progressDialog.show();

        WebApiClient.post(url, jsonRequest, true, new TextHttpResponseHandler(){
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
        });
    }

}
