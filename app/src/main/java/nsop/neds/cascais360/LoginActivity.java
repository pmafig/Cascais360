package nsop.neds.cascais360;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import nsop.neds.cascais360.Authenticator.AccountGeneral;
import nsop.neds.cascais360.Encrypt.MessageEncryption;
import nsop.neds.cascais360.Entities.Json.LoginUserData;
import nsop.neds.cascais360.Manager.Broadcast.AppSignatureHelper;
import nsop.neds.cascais360.Manager.MenuManager;
import nsop.neds.cascais360.Manager.SessionManager;
import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Manager.WeatherManager;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.WebApi.ReportManager;
import nsop.neds.cascais360.WebApi.WebApiCalls;
import nsop.neds.cascais360.WebApi.WebApiClient;
import nsop.neds.cascais360.WebApi.WebApiMessages;

import static android.accounts.AccountManager.KEY_ERROR_MESSAGE;
import static nsop.neds.cascais360.Authenticator.AccountGeneral.sServerAuthenticate;

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

        Button logon = findViewById(R.id.logon);
        TextView recover = findViewById(R.id.recover);
        TextView register = findViewById(R.id.register);

        TextView terms = findViewById(R.id.terms_conditions);
        TextView privacy = findViewById(R.id.privacy_manifest);

        terms.setTextColor(Color.parseColor(Settings.colors.YearColor));
        privacy.setTextColor(Color.parseColor(Settings.colors.YearColor));
        logon.setBackgroundColor(Color.parseColor(Settings.colors.YearColor));

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

    }

    private void submit(){
        EditText accountNameField = findViewById(R.id.accountName);
        EditText accountPasswordField = findViewById(R.id.accountPassword);
        String accountName = accountNameField.getText().toString();
        String accountPassword = accountPasswordField.getText().toString();
        login(accountName, accountPassword);
    }

    private void login(String userName, String password){

        String encPass = "";

        try{
            encPass = new MessageEncryption().Encrypt(password, WebApiClient.SITE_KEY);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        SessionManager sm = new SessionManager(this);

        String jsonRequest = String.format("{\"Email\":\"%s\", \"Password\":\"%s\", \"FirebaseToken\":\"%s\", AppType:2, LanguageID:%s}",
                userName, encPass, sm.getFirebaseToken(), sm.getLangCodePosition() + 1);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Autenticação...");

        progressDialog.show();

        WebApiClient.post(String.format("/%s/%s", WebApiClient.API.WebApiAccount, WebApiClient.METHODS.login), jsonRequest,true, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String message = WebApiMessages.DecryptMessage(responseString);
                progressDialog.dismiss();
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

        LoginUserData data = new Gson().fromJson(json, LoginUserData.class);


        if(data.ResponseData.IsAuthenticated){ // ReportManager.isAuthenticated(json)
            try {
                SessionManager sm = new SessionManager(this);

                menuFragment.findViewById(R.id.user_loggedon_header).setVisibility(View.VISIBLE);
                menuFragment.findViewById(R.id.menu_button_login_frame).setVisibility(View.GONE);
                TextView name = menuFragment.findViewById(R.id.user_name);
                name.setText(data.ResponseData.DisplayName);

                sm.setDisplayname(data.ResponseData.DisplayName);
                sm.setFullName(ReportManager.getFullName(json));
                sm.setEmail(ReportManager.getEmail(json));
                sm.setMobileNumber(ReportManager.getMobileNumber(json));
                sm.setAddress(ReportManager.getAddress(json));

                submit(data.ResponseData.SSK, data.ResponseData.UserID, data.ResponseData.RefreshToken);
            }catch (Exception e){
                AccountGeneral.logout(this);
                intentNavegation();
            }
        }else{
            /*AlertDialog.Builder alertMessage = new AlertDialog.Builder(this, R.style.AlertessageDialog);
            alertMessage.setMessage(ReportManager.getReportList(json));
            alertMessage.show();*/
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
        SessionManager sm = new SessionManager(this);
        sm.clear();
        sm.setRecover();
        //startActivity(new Intent(LoginActivity.this, RecoverActivity.class));
    }

    private void register(){
        SessionManager sm = new SessionManager(this);
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

        if(getIntent().hasExtra(Variables.Id)){

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            int nid = extras.getInt(Variables.Id);

            Intent event = new Intent(this, DetailActivity.class);
            event.putExtra(Variables.Id, nid);
            event.putExtra(Variables.RequiredLogin, true);
            event.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(event);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
