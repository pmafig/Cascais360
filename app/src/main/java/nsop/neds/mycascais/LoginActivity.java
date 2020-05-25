package nsop.neds.mycascais;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.Email;
import nsop.neds.mycascais.Entities.Json.Labels;

import nsop.neds.mycascais.Entities.Json.PhoneContacts;
import nsop.neds.mycascais.Entities.WebApi.LoginUserRequest;
import nsop.neds.mycascais.Entities.WebApi.LoginUserResponse;
import nsop.neds.mycascais.Entities.WebApi.Response;
import nsop.neds.mycascais.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.mycascais.Manager.CommonManager;
import nsop.neds.mycascais.Manager.CountryListManager;
import nsop.neds.mycascais.Manager.Layout.LayoutManager;
import nsop.neds.mycascais.Manager.MenuManager;
import nsop.neds.mycascais.Manager.SessionManager;
import nsop.neds.mycascais.Manager.Variables;
import nsop.neds.mycascais.Manager.WeatherManager;
import nsop.neds.mycascais.Settings.Data;
import nsop.neds.mycascais.Settings.Settings;
import nsop.neds.mycascais.WebApi.ReportManager;
import nsop.neds.mycascais.WebApi.WebApiCalls;
import nsop.neds.mycascais.WebApi.WebApiClient;
import nsop.neds.mycascais.WebApi.WebApiMessages;

import static android.accounts.AccountManager.KEY_ERROR_MESSAGE;
import static nsop.neds.mycascais.Authenticator.AccountGeneral.sServerAuthenticate;

public class LoginActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    LinearLayout menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menuFragment = findViewById(R.id.menu);

        new WeatherManager(this, (LinearLayout) findViewById(R.id.wearther)).execute(WebApiCalls.getWeather());

        new MenuManager(this, toolbar, menuFragment, "MYCASCAIS");

        LinearLayout backButton = toolbar.findViewById(R.id.menu_back_frame);
        backButton.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button logon = findViewById(R.id.logon);
        TextView recover = findViewById(R.id.recover);
        TextView register = findViewById(R.id.register);
        EditText accountNameField = findViewById(R.id.accountName);
        EditText accountPasswordField = findViewById(R.id.accountPassword);

        LinearLayout frame = findViewById(R.id.terms_conditions_frame);

        TextView terms = findViewById(R.id.terms_conditions);
        TextView privacy = findViewById(R.id.privacy_manifest);

        terms.setTextColor(Color.parseColor(Settings.colors.YearColor));
        privacy.setTextColor(Color.parseColor(Settings.colors.YearColor));
        logon.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));
        recover.setTextColor(Color.parseColor(Settings.colors.YearColor));
        register.setTextColor(Color.parseColor(Settings.colors.YearColor));

        accountNameField.setHint(Settings.labels.Username);
        accountPasswordField.setHint(Settings.labels.Password);
        logon.setText(Settings.labels.LoginButton);

        logon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recover();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mAccountManager = AccountManager.get(this);

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView( R.layout.block_terms_condition_info);
                TextView title = dialog.findViewById(R.id.terms_title);
                title.setTextColor(Color.parseColor(Settings.colors.YearColor));
                title.setText(Settings.labels.PrivacyPolicy);

                WebView about = dialog.findViewById(R.id.more_info_address);
                about.loadUrl("https://sites.cascais.pt/mycascais-contents/privacyandpolicy");
                //about.loadData(CommonManager.WebViewFormatLight(Settings.aboutApp), CommonManager.MimeType(), CommonManager.Encoding());

                dialog.findViewById(R.id.close_terms).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView( R.layout.block_terms_condition_info);
                TextView title = dialog.findViewById(R.id.terms_title);
                title.setTextColor(Color.parseColor(Settings.colors.YearColor));
                title.setText(Settings.labels.TermsAndConditions);

                WebView about = dialog.findViewById(R.id.more_info_address);
                about.loadUrl("https://sites.cascais.pt/mycascais-contents/termsandconditions");
                //about.loadData(CommonManager.WebViewFormatLight(Settings.aboutApp), CommonManager.MimeType(), CommonManager.Encoding());

                dialog.findViewById(R.id.close_terms).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void submit(){
        EditText accountNameField = findViewById(R.id.accountName);
        EditText accountPasswordField = findViewById(R.id.accountPassword);

        String accountName = accountNameField.getText().toString().trim();
        Data.CurrentAccountName = accountName;
        String accountPassword = accountPasswordField.getText().toString().trim();

        if(accountName.isEmpty() || accountPassword.isEmpty()){
            LayoutManager.alertMessage(this, Settings.labels.LoginSubtitle);
        }else {
            login(accountName.trim(), accountPassword.trim());
        }
    }

    private void login(String userName, String password){

        String encPass = "";

        SessionManager sm = new SessionManager(this);

        LoginUserRequest request = new LoginUserRequest();
        request.UserName = userName;
        request.Password = new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY);
        request.FirebaseToken = sm.getFirebaseToken();
        request.AppType = 2;
        request.LanguageID = sm.getLangCodePosition() + 1;

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(Settings.labels.ProcessingData);

        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.login), new Gson().toJson(request),true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String message = WebApiMessages.DecryptMessage(responseString);
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, Settings.labels.TryAgain, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String message = WebApiMessages.DecryptMessage(responseString);
                progressDialog.dismiss();

                postSuccess(message);
            }
        });
    }

    private void postSuccess(String json){
        Response<LoginUserResponse> response = new Gson().fromJson(json, nsop.neds.mycascais.Entities.WebApi.Response.class);

        LoginUserResponse responseLogin = null;

        String jsonUser = "";

        try {
            JSONObject data = new JSONObject(json);
            JSONObject responseData = data.getJSONObject(Variables.ResponseData);
            jsonUser = responseData.toString();

            responseLogin = new Gson().fromJson(jsonUser, LoginUserResponse.class);

        }catch (JSONException ex){

        }

        if(response.ReportList != null && response.ReportList.size() > 0){

            LayoutManager.alertMessage(this, response.ReportList.get(0).Description);
        }else {
            if (responseLogin.IsAuthenticated) { // ReportManager.isAuthenticated(json)
                try {
                    SessionManager sm = new SessionManager(this);
                    sm.setUser(jsonUser);

                    menuFragment.findViewById(R.id.user_loggedon_header).setVisibility(View.VISIBLE);
                    menuFragment.findViewById(R.id.menu_button_login_frame).setVisibility(View.GONE);
                    TextView name = menuFragment.findViewById(R.id.user_name);
                    name.setText(responseLogin.DisplayName);

                    sm.setDisplayname(responseLogin.DisplayName);
                    sm.setFullName(ReportManager.getFullName(json));

                    if (responseLogin.Emails != null && responseLogin.Emails.size() > 0) {

                        String email = "";

                        for (Email e : responseLogin.Emails){
                            if(e.Main){
                                email = e.EmailAddress;
                            }
                        }

                        if(email.isEmpty()){
                            email = responseLogin.Emails.get(0).EmailAddress;
                        }

                        sm.setEmail(email);
                    }

                    if (responseLogin.PhoneContacts != null && responseLogin.PhoneContacts.size() > 0) {

                        String phoneNumber = "";

                        for (PhoneContacts contact : responseLogin.PhoneContacts){
                            if(contact.Login){
                                phoneNumber = contact.Number;
                                sm.setMobileNumberMain(true);
                            }
                        }

                        if(phoneNumber.isEmpty()){

                            for (PhoneContacts contact : responseLogin.PhoneContacts){
                                if(contact.Main){
                                    phoneNumber = contact.Number;
                                }
                            }

                            if(phoneNumber.isEmpty() && responseLogin.PhoneContacts.size() > 0) {
                                phoneNumber = responseLogin.PhoneContacts.get(0).Number;
                            }
                        }

                        sm.setMobileNumber(phoneNumber);
                    }else{
                        sm.setMobileNumber("");
                    }

                    sm.setAddress(ReportManager.getAddress(json));

                    submit(responseLogin.SSK, responseLogin.AuthID, responseLogin.RefreshToken);
                } catch (Exception e) {
                    AccountGeneral.logout(this);
                    intentNavegation();
                }
            } else {
                AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertMessageDialog);
                alertMessage.setMessage(ReportManager.getReportList(json));
                alertMessage.show();
            }
        }
    }

    public void submit(String ssk, String userId, String refreshToken) {
        final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

        final String accountType = AccountGeneral.ACCOUNT_TYPE;

        String authtoken = null;
        Bundle data = new Bundle();

        try {
            authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);

            data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
            data.putString(PARAM_USER_PASS, userPass);
            data.putString("SSK", ssk);
            data.putString("UserId", userId);
            data.putString("RefreshToken", refreshToken);

        } catch (Exception e) {
            data.putString(KEY_ERROR_MESSAGE, e.getMessage());
        }

        final Intent res = new Intent();
        res.putExtras(data);

        if (res.hasExtra(KEY_ERROR_MESSAGE)) {
            Toast.makeText(getBaseContext(), res.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
        } else {
            finishLogin(res);
        }
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);

        if (intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            mAccountManager.addAccountExplicitly(account, accountPassword, intent.getExtras());
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        }else{
            mAccountManager.setPassword(account, accountPassword);
        }

        //setResult(RESULT_OK, intent);
        //finish();
        intentNavegation();
    }

    private void recover(){
        startActivity(new Intent(LoginActivity.this, RecoverActivity.class));
    }

    private void register(){

        SessionManager sm = new SessionManager(this);
        new CountryListManager(this).execute(WebApiCalls.getCountryList(sm.getLangCodePosition() + 1));
        sm.clear();
        sm.setNewAccount();

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        //startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent = getIntent();
        return parentIntent;
    }

    public void intentNavegation(){

        SessionManager sm = new SessionManager(this);
        String externalAppInfo = sm.getExternalAppInfo();

        if(sm != null && !externalAppInfo.isEmpty()) {
            Intent intent = new Intent(this, DisclaimerActivity.class);
            intent.putExtra(Variables.PackageName, sm.getExternalAppPackageName());
            intent.putExtra(Variables.ExternalAppId, sm.getExternalAppExternalId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if(getIntent().hasExtra(Variables.Id)){
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            int nid = extras.getInt(Variables.Id);

            Intent event = new Intent(this, DetailActivity.class);
            event.putExtra(Variables.Id, nid);
            event.putExtra(Variables.RequiredLogin, true);
            event.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(event);
        }else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
